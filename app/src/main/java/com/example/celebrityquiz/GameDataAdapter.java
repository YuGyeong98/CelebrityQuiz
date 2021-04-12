package com.example.celebrityquiz;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class GameDataAdapter extends RecyclerView.Adapter {

    private GameDataManager gameDataManager;
    private String userEmail;
    private Context context;

    public GameDataAdapter(Context context) {
        this.context = context;
        this.gameDataManager = GameDataManager.getInstance();
        this.userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutInflater = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);

        return new RecyclerView.ViewHolder(layoutInflater) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        TextView date = holder.itemView.findViewById(R.id.dateText);
        TextView domain = holder.itemView.findViewById(R.id.domainText);
        TextView score = holder.itemView.findViewById(R.id.scoreText);
        Button button = holder.itemView.findViewById(R.id.reviewButton);

        List<GameData> gameDataList = gameDataManager.getGameDataListOrderById(userEmail);
        GameData gameData = gameDataList.get(position);
        if (gameData.getDate() != null)
            date.setText(gameData.getDate());
        if (gameData.getDomain() != null)
            domain.setText(gameData.getDomain());
        score.setText(String.valueOf(gameData.getScore()));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("subin", "onCLick");
                Intent intent = new Intent(context, QuizActivity.class);
                intent.putExtra("isReview", true);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.gameDataManager.getGameDataListOrderById(userEmail).size();
    }
}
