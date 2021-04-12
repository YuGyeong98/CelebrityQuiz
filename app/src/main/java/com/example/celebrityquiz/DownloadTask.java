package com.example.celebrityquiz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    //AsyncTask<doInBackground()의 변수 종류, onProgressUpdate()에서 사용할 변수 종류, onPostExecute()에서 사용할 변수종류>

    // Declare static variables to switch between download results
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;
    private static final int TYPE_PAUSED = 2;
    private static final int TYPE_CANCELED = 3;

    private DownloadListener downloadListener;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    DownloadTask(DownloadListener downloadListener, Context context) {
        this.downloadListener = downloadListener;
        this.context = context;
    }

    @Override
    protected Integer doInBackground(String... strings) {//Integer은 onPostExecute()에서 사용할 변수 종류
        InputStream inputStream = null;//데이터가 들어오는 통로
        RandomAccessFile accessFile = null;//원래 스트림은 원칙적으로 순서대로 처리되지만 예외적으로 임의 접근처리를 할수있게 하는 입출력스트림, 바로 Object를 상속받는다.
        File file = null;

        try {
            long downloadedLength = 0;
            String downloadUrl = strings[0];
            String fileName = "myJson";
            // Save file in local directory, 파일 액세스 및 저장(File file = new File(context.getFilesDir(), filename);)
            File directory = context.getFilesDir();
            file = new File(directory, fileName);

            if(file.exists()) {
                file.delete(); // Clear available files
                downloadedLength = 0;
            }

            long contentLength = getContentLength(downloadUrl);//downloadUrl의 크기 반환
            if(contentLength == 0) return TYPE_FAILED;

            OkHttpClient client = new OkHttpClient();//REST 호출 전송, HTTP 기반의 요청, 응답 처리
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes = " + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();

            Response response = client.newCall(request).execute();
            inputStream = Objects.requireNonNull(response.body()).byteStream();
            accessFile = new RandomAccessFile(file, "rw");
            accessFile.seek(downloadedLength); // omit the downloaded bytes
            byte[] b = new byte[1024];
            int total = 0;
            int len;
            while ((len = inputStream.read(b)) != -1) {//바이트 1024개만큼을 읽고 b에 넘겨준다. 읽을 게 없으면 -1을 출력
                if (isCanceled) {
                    return TYPE_CANCELED;
                } else if(isPaused) {
                    return TYPE_PAUSED;
                } else {
                    total += len;
                    accessFile.write(b, 0, len);//지정된 바이트 배열의 오프셋 0으로 부터 len 바이트를 이 파일에 출력
                    // calculate the percentages of downloaded part
                    int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                    publishProgress(progress);//onProgressUpdate실행 - 사용자에게 진행을 알릴 때 사용
                    // onProgressUpdate()에 progress가 들어오며 메소드가 호출
                }
            }
            Objects.requireNonNull(response.body()).close();
            return TYPE_SUCCESS;//onPostExecute()에 반환
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();//통로 끊기
                }
                if (accessFile != null) {
                    accessFile.close();
                }
                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        int progress = values[0];
        if (progress > lastProgress) {
            downloadListener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {//백그라운드 작업이 완료된 후 결과값
        switch (status) {
            case TYPE_SUCCESS:
                downloadListener.onSuccess();
                break;
            case TYPE_FAILED:
                downloadListener.onFailed();
                break;
            case TYPE_PAUSED:
                downloadListener.onPaused();
                break;
            case TYPE_CANCELED:
                downloadListener.onCanceled();
            default:
                break;
        }
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            long contentLength = Objects.requireNonNull(response.body()).contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }
}
