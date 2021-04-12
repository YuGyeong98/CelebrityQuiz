package com.example.celebrityquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    // Declare Variables
    private RadioButton radioButtonCelebrity;
    private RadioButton radioButtonScience;
    private RadioButton radioButtonAnimal;

    private RadioButton radioButtonLevelOne;
    private RadioButton radioButtonLevelTwo;
    private RadioButton radioButtonLevelThree;

    private RadioButton radioButton30;
    private RadioButton radioButton60;
    private RadioButton radioButton90;

    private ProgressBar progressBarDownload;

    public String jsonUrl;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity();
        }

        mAuth = FirebaseAuth.getInstance();

        // Define Quiz views
        radioButtonCelebrity = findViewById(R.id.radioButtonCelebrity);
        radioButtonScience = findViewById(R.id.radioButtonScience);
        radioButtonAnimal = findViewById(R.id.radioButtonAnimal);
        radioButtonCelebrity.setChecked(true);
        radioButtonScience.setChecked(false);
        radioButtonAnimal.setChecked(false);

        // Define Level views
        radioButtonLevelOne = findViewById(R.id.radioButtonLevelOne);
        radioButtonLevelTwo = findViewById(R.id.radioButtonLevelTwo);
        radioButtonLevelThree = findViewById(R.id.radioButtonLevelThree);
        radioButtonLevelOne.setChecked(true);
        radioButtonLevelTwo.setChecked(false);
        radioButtonLevelThree.setChecked(false);

        // Define Time views
        radioButton30 = findViewById(R.id.radioButton30);
        radioButton60 = findViewById(R.id.radioButton60);
        radioButton90 = findViewById(R.id.radioButton90);
        radioButton30.setChecked(true);
        radioButton60.setChecked(false);
        radioButton90.setChecked(false);

        // Define Download views
        progressBarDownload = findViewById(R.id.progressBarDownload);
        progressBarDownload.setMax(100);

        // Define Update and Starting buttons
        Button buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setEnabled(true);
        downloadTask = null; // Always initialize task to null
    }

    private DownloadTask downloadTask;

    // Define Download methods
    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            progressBarDownload.setProgress(progress);
        }

        @Override
        public void onSuccess() {
            Log.d("FIREBASEUSERID", mAuth.getUid());
            downloadTask = null;
            progressBarDownload.setProgress(progressBarDownload.getMax());

            if(radioButtonLevelOne.isChecked()) QuizInfoOriginator.getInstance().setLevel(1);
            if(radioButtonLevelTwo.isChecked()) QuizInfoOriginator.getInstance().setLevel(2);
            if(radioButtonLevelThree.isChecked()) QuizInfoOriginator.getInstance().setLevel(3);

            if(radioButton30.isChecked()) QuizInfoOriginator.getInstance().setSeconds(30);
            if(radioButton60.isChecked()) QuizInfoOriginator.getInstance().setSeconds(60);
            if(radioButton90.isChecked()) QuizInfoOriginator.getInstance().setSeconds(90);

            if(radioButtonCelebrity.isChecked()) QuizInfoOriginator.getInstance().setDomain(QuizInfoOriginator.DOMAIN_CELEBRITY);
            if(radioButtonScience.isChecked()) QuizInfoOriginator.getInstance().setDomain(QuizInfoOriginator.DOMAIN_SCIENCE);
            if(radioButtonAnimal.isChecked()) QuizInfoOriginator.getInstance().setDomain(QuizInfoOriginator.DOMAIN_ANIMAL);
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            //when download failed, close the foreground notification and create a new one about the failure
            Toast.makeText(getApplicationContext(), "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    public void onButtonUpdate(View view) {
        if(downloadTask == null) {
            // Import data from internet
            if(radioButtonCelebrity.isChecked()){
                jsonUrl = "https://api.jsonbin.io/b/5e8f60bb172eb6438960f731";
            }else if(radioButtonScience.isChecked()){
                jsonUrl = "https://api.jsonbin.io/b/5dd263732e22356f234d90cb/175";
            }else if(radioButtonAnimal.isChecked()){
                jsonUrl = "https://api.jsonbin.io/b/5dd263732e22356f234d90cb/174";
            }
            downloadTask = new DownloadTask(downloadListener, this);
            downloadTask.execute(jsonUrl);
        }
    }

    private void startActivity(){
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
}