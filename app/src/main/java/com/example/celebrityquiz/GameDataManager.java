package com.example.celebrityquiz;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.util.GAuthToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
    The GameDataManager class is a singleton object.
 */
public class GameDataManager {
    public static final String ROOT = "gameData";

    private static GameDataManager instance;
    private FirebaseDatabase database;
    private List<GameData> gameDataList;

    private GameDataManager(){
        this.database = FirebaseDatabase.getInstance();
        gameDataList = new ArrayList<GameData>();
        database.getReference(ROOT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Subin", "GameDataChanged");
                collectGameData(snapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * @return  instance of the GameDataManager
     */
    public static GameDataManager getInstance(){
        if(instance == null)
            instance = new GameDataManager();
        return instance;
    }

    /**
     * Add GameData to firebase.
     *
     * @param gameData
     */
    public void addGameData(GameData gameData){
        this.gameDataList.add(gameData);
        database.getReference(ROOT).push().setValue(gameData);
    }

    private void collectGameData(Iterable<DataSnapshot> gameDataList) {
        this.gameDataList.clear();

        for (DataSnapshot dataSnapshot : gameDataList) {
            this.gameDataList.add(dataSnapshot.getValue(GameData.class));
        }

        Log.d("Subin", this.gameDataList.toString());
    }

    public List<GameData> getGameDataList(){
        return this.gameDataList;
    }

    /**
     * return the GameData list ordered by given user email.
     *
     * @param givenUserEmail
     * @return List<GameData>
     * */
    public List<GameData> getGameDataListOrderById(String givenUserEmail){
        ArrayList<GameData> orderBy = new ArrayList<GameData>();

        for (GameData gameData : this.gameDataList) {
            String userEmail = gameData.getUserEmail();
            if(givenUserEmail.equals(userEmail))
                orderBy.add(gameData);
        }

        return orderBy;
    }

    /**
     * return the GameData list ordered by given game mode.
     * you can refer the game mode value declared in GameData class.
     *
     * @param givenGameMdoe
     * @return List<GameData>
     */
    public List<GameData> getGameDataListOrderByGameMode(int givenGameMdoe){
        ArrayList<GameData> orderBy = new ArrayList<GameData>();

        for (GameData gameData : this.gameDataList) {
            int gameMode = gameData.getGameMode();
            if(givenGameMdoe == gameMode)
                orderBy.add(gameData);
        }

        return orderBy;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    // for test.
    public void log(){
        Log.d("GameDataList", GameDataManager.getInstance().getGameDataList().toString());
        Log.d("GameDataListGameMode1", GameDataManager.getInstance().getGameDataListOrderByGameMode(GameData.GAMEMODE_NORMAL).toString());
        Log.d("GameDataListCurrentID", GameDataManager.getInstance().getGameDataListOrderById(FirebaseAuth.getInstance().getCurrentUser().getEmail()).toString());
    }
}
