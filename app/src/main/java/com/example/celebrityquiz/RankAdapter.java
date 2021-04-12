package com.example.celebrityquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;

public class RankAdapter extends RecyclerView.Adapter{

    GameDataManager gameDataManager;
    private Context context;

    public RankAdapter(Context context){
        this.context = context;
        this.gameDataManager = GameDataManager.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutInflater = LayoutInflater.from(context).inflate(R.layout.rank_item, parent, false);

        return new RecyclerView.ViewHolder(layoutInflater) {};
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        TextView id = holder.itemView.findViewById(R.id.id);
        TextView domain = holder.itemView.findViewById(R.id.domain);
        TextView score = holder.itemView.findViewById(R.id.score);
        TextView rank = holder.itemView.findViewById(R.id.rank);

        List<GameData> gameDataList = gameDataManager.getGameDataListOrderByGameMode(GameData.GAMEMODE_NORMAL);
        gameDataList.sort(new Comparator<GameData>(){
            @Override
            public int compare(GameData o1, GameData o2) {
                if(o1.getScore() >= o2.getScore())
                    return -1;
                else
                    return 1;
            }
        });
        GameData gameData = gameDataList.get(position);
        if(gameData.getUserEmail() != null)
            id.setText(gameData.getUserEmail());
        if(gameData.getDomain() != null)
            domain.setText(gameData.getDomain());
        score.setText(String.valueOf(gameData.getScore()));
        rank.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return gameDataManager.getGameDataListOrderByGameMode(GameData.GAMEMODE_NORMAL).size();
    }
}
