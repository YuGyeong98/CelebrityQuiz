package com.example.celebrityquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    // Declare variables
    private List<Quiz> quizList;
    private int seconds;
    private int indexCurrentQuestion;

    private TextView questionView;
    private ImageView imageView;
    private RadioGroup radioGroup;
    private RadioButton radioButtonOne;
    private RadioButton radioButtonTwo;
    private RadioButton radioButtonThree;
    private RadioButton radioButtonFour;
    private Button buttonPrevious;
    private Button buttonNext;
    private TextView textTime;
    private CountDownTimer countDownTimer;

    private Button buttonAddTime;
    private int remainTime;
    private int leftTime;
    private int level;
    private String domain;
    private Button buttonRemoveOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_quiz);

        // Hide toolbar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Define Activity views
        questionView = findViewById(R.id.celebrityQuestion);
        imageView = findViewById(R.id.celebrityImage);
        radioGroup = findViewById(R.id.celebrityOption);
        radioButtonOne = findViewById(R.id.radioButtonOne);
        radioButtonTwo = findViewById(R.id.radioButtonTwo);
        radioButtonThree = findViewById(R.id.radioButtonThree);
        radioButtonFour = findViewById(R.id.radioButtonFour);
        textTime = findViewById(R.id.textTime);

        // setOnClickListener and set checked onClick for each button
        radioButtonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RadioButton) view).setChecked(true);
                quizList.get(indexCurrentQuestion).userAnswer = 1;
            }
        });

        radioButtonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RadioButton) view).setChecked(true);
                quizList.get(indexCurrentQuestion).userAnswer = 2;
            }
        });

        radioButtonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RadioButton) view).setChecked(true);
                quizList.get(indexCurrentQuestion).userAnswer = 3;
            }
        });

        radioButtonFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RadioButton) view).setChecked(true);
                quizList.get(indexCurrentQuestion).userAnswer = 4;
            }
        });

        // Define button views
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);

        // Access intent interface and get variables
        Intent intent = getIntent();
        if(!intent.getBooleanExtra("isReview", false)) {
            level = QuizInfoOriginator.getInstance().getLevel();
            seconds = QuizInfoOriginator.getInstance().getSeconds();
            domain = QuizInfoOriginator.getInstance().getDomain();
            leftTime = 0;
            String string = null;

            // Safely read data from saved file
            try {
                FileInputStream fileInputStream = openFileInput("myJson");//myJson 파일 열기
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();//문자열을 더할 때 새로운 객체를 생성하는 것이 아니라 기존 데이터에 더한다.
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);//문자열 더하기
                }
                string = stringBuilder.toString();//만들어진 문자열 출력
            } catch (IOException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();//json으로 받은 데이터를 내가 만든 객체에 자동으로 set 해주는 기능을 제공
            Type type = new TypeToken<List<Quiz>>() {
            }.getType();
            List<Quiz> list = gson.fromJson(string, type);//stringBuilder.toString(); 이 문자열을 Quiz 클래스 생성자 형태로 저장??
            // Set sublist based on user set level
            if (level == 1) {
                assert list != null;
                quizList = list.subList(0, 5);
            } else if (level == 2) {
                assert list != null;
                quizList = list.subList(5, 10);
            } else {
                assert list != null;
                quizList = list.subList(10, 15);
            }

        }
        else
        {
            GameDataManager gameDataManager = GameDataManager.getInstance();
            GameData gameData = gameDataManager.getGameDataListOrderById(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get(intent.getIntExtra("position", 0));


            level = gameData.getLevel();
            seconds = gameData.getSeconds();
            domain = gameData.getDomain();
            leftTime = 0;

            quizList = gameData.getQuizList();
        }

        // initialise and set for each index in current activity as current question
        indexCurrentQuestion = 0;
        Quiz currentQuestion = quizList.get(indexCurrentQuestion);
        currentQuestionView(currentQuestion);
        buttonPrevious.setEnabled(false); // Disable previous button when current index is 0

        // See function
        startTimer();

        // When user submit quiz, stop time and start Solution Activity
        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                Intent i = new Intent(QuizActivity.this, SolutionActivity.class);
                i.putExtra("score", getScore());
                // Change List to ArrayList to accommodate subList
                ArrayList<Quiz> list = new ArrayList<>(quizList);
                i.putExtra("quizList", list);
                i.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP );

                GameDataManager gameDataManager = GameDataManager.getInstance();
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH:mm", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                gameDataManager.addGameData(new GameData(userEmail, quizList, remainTime + leftTime, Integer.valueOf(textTime.getText().toString()), getScore()*1000 + 100 + level*100 - leftTime, GameData.GAMEMODE_NORMAL,
                        level, domain, currentDateandTime));

                startActivity(i);
                finish();
            }
        });

        buttonAddTime = findViewById(R.id.buttonAddTime);
        buttonAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTime();
                buttonAddTime.setEnabled(false);
            }
        });

        buttonRemoveOption = findViewById(R.id.buttonRemoveOption);
        buttonRemoveOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Quiz currentQuestion = quizList.get(indexCurrentQuestion);
                removeOption(currentQuestion);
                buttonRemoveOption.setEnabled(false);
            }
        });
    }

    // Start countdown. OnFinish, start Solution Activity
    public void startTimer() {
        textTime.setText(String.valueOf(seconds));
        countDownTimer = new CountDownTimer(seconds * 1000, 1000) {//(총 시간, Tick 에 대한 시간) - 1초에 한번씩 총 seconds만큼 동작
            @Override
            public void onTick(long millisUntilFinished) {//타이머가 종료될 때까지 동작
                textTime.setText(String.valueOf((int) (millisUntilFinished / 1000)));
                remainTime = (int) (millisUntilFinished / 1000);
                leftTime++;
            }

            @Override
            public void onFinish() {//타이머가 종료될 때 동작
                Intent i = new Intent(QuizActivity.this, SolutionActivity.class);
                i.putExtra("score", getScore());
                // Change List to ArrayList to accommodate subList
                ArrayList<Quiz> list = new ArrayList<>(quizList);
                i.putExtra("quizList", list);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        }.start();
    }

    // Cancel timer to prevent countDown in background
    // If not defined, Solution Activity will start even when user goes back to
    // Main Activity because Quiz Activity doesn't get destroyed instantly
    public void stopTimer() {
        countDownTimer.cancel();
    }

    // Pre-define new views before setting previous question as current question, for index < 0
    public void onButtonPrevious(View view) {
        if(indexCurrentQuestion != 0) {
            indexCurrentQuestion--;
            if(indexCurrentQuestion == 0) buttonPrevious.setEnabled(false);
            if(indexCurrentQuestion != (quizList.size() - 1)) buttonNext.setEnabled(true);
            Quiz currentQuestion = quizList.get(indexCurrentQuestion);
            currentQuestionView(currentQuestion);

            radioGroup = findViewById(R.id.celebrityOption);
            if(currentQuestion.userAnswer == 0) radioGroup.clearCheck();
            else {
                switch (currentQuestion.userAnswer) {
                    case 1: {
                        radioGroup.check(R.id.radioButtonOne);
                        break;
                    }
                    case 2: {
                        radioGroup.check(R.id.radioButtonTwo);
                        break;
                    }
                    case 3: {
                        radioGroup.check(R.id.radioButtonThree);
                        break;
                    }
                    case 4: {
                        radioGroup.check(R.id.radioButtonFour);
                        break;
                    }
                }
            }
        }
    }

    // Pre-define new views before setting next question as current question, for index > list.size()
    public void onButtonNext(View view) {
        if(indexCurrentQuestion != (quizList.size() - 1)) {
            indexCurrentQuestion++;
            if(indexCurrentQuestion == (quizList.size() - 1)) buttonNext.setEnabled(false);
            if(indexCurrentQuestion != 0) buttonPrevious.setEnabled(true);
            Quiz currentQuestion = quizList.get(indexCurrentQuestion);
            currentQuestionView(currentQuestion);

            radioGroup = findViewById(R.id.celebrityOption);
            if(currentQuestion.userAnswer == 0) radioGroup.clearCheck();
            else {
                switch (currentQuestion.userAnswer) {
                    case 1: {
                        radioGroup.check(R.id.radioButtonOne);
                        break;
                    }
                    case 2: {
                        radioGroup.check(R.id.radioButtonTwo);
                        break;
                    }
                    case 3: {
                        radioGroup.check(R.id.radioButtonThree);
                        break;
                    }
                    case 4: {
                        radioGroup.check(R.id.radioButtonFour);
                        break;
                    }
                }
            }
        }
    }

    //buttonAddTime
    public void addTime() {
        createNewTimer(remainTime + 10);
    }

    //buttonAddTime
    public void createNewTimer(final int timeInMillis) {
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(timeInMillis * 1000, 1000) {
            @Override
            public void onTick(final long millisUntilFinished) {
                textTime.setText(String.valueOf((int) (millisUntilFinished / 1000)));
                seconds = (int) millisUntilFinished;
            }

            @Override
            public void onFinish() {
                Intent i = new Intent(QuizActivity.this, SolutionActivity.class);
                i.putExtra("score", getScore());
                // Change List to ArrayList to accommodate subList
                ArrayList<Quiz> list = new ArrayList<>(quizList);
                i.putExtra("quizList", list);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        }.start();
    }

    //buttonRemoveOption
    public void removeOption(Quiz currentQuestion){
        Random rand = new Random();
        int random = (rand.nextInt(4)) + 1;//1~4

        if(currentQuestion.correctAnswer == 1){
            while(random == 1){
                random = (rand.nextInt(4)) + 1;
            }if(random == 2){
                radioButtonTwo.setText("");
            }else if(random == 3){
                radioButtonThree.setText("");
            }else if(random == 4){
                radioButtonFour.setText("");
            }
        }else if(currentQuestion.correctAnswer == 2){
            while(random == 2){
                random = (rand.nextInt(4)) + 1;
            }if(random == 1){
                radioButtonOne.setText("");
            }else if(random == 3){
                radioButtonThree.setText("");
            }else if(random == 4){
                radioButtonFour.setText("");
            }
        }else if(currentQuestion.correctAnswer == 3){
            while(random == 3){
                random = (rand.nextInt(4)) + 1;
            }if(random == 1){
                radioButtonOne.setText("");
            }else if(random == 2){
                radioButtonTwo.setText("");
            }else if(random == 4){
                radioButtonFour.setText("");
            }
        }else if(currentQuestion.correctAnswer == 4){
            while(random == 4){
                random = (rand.nextInt(4)) + 1;
            }if(random == 1){
                radioButtonOne.setText("");
            }else if(random == 2){
                radioButtonTwo.setText("");
            }else if(random == 3){
                radioButtonThree.setText("");
            }
        }
    }

    public void currentQuestionView(Quiz currentQuestion) {
        questionView.setText(String.format("%s. %s", indexCurrentQuestion + 1, currentQuestion.question));
        radioButtonOne.setText(currentQuestion.one);
        radioButtonTwo.setText(currentQuestion.two);
        radioButtonThree.setText(currentQuestion.three);
        radioButtonFour.setText(currentQuestion.four);
        Glide.with(imageView.getContext()).load(currentQuestion.imageUrl).into(imageView);
    }

    // Calculate score
    public int getScore() {
        int score = 0;
        for (int i = 0; i < quizList.size(); i++) {
            if (quizList.get(i).userAnswer == quizList.get(i).correctAnswer) score++;
        }
        return score;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
    }
}
