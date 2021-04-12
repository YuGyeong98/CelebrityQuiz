package com.example.celebrityquiz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        CardView startQuizCardView = findViewById(R.id.startQuizCardView);
        CardView historyCardView = findViewById(R.id.settingsCardView);
        CardView rankCardView = findViewById(R.id.rankCardView);
        CardView reviewCardView = findViewById(R.id.reviewCardView);

        startQuizCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(QuizInfoOriginator.getInstance().isDownloaded()) {
                    Intent intent = new Intent(HomeActivity.this, QuizActivity.class);
                    intent.putExtra("isReview", false);
                    startActivity(intent);
                }
                else
                    Toast.makeText(HomeActivity.this, "You need to download the quizzes first", Toast.LENGTH_SHORT).show();
            }
        });

        historyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        rankCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RankActivity.class));
            }
        });

        reviewCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ReviewActivity.class));
            }
        });

    }
}
