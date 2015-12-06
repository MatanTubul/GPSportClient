package com.example.matant.gpsportclient.Utilities;

import android.graphics.Bitmap;

/**
 * Created by nirb on 11/25/2015.
 */
public class ViewEventListRow {

    private String playerName;
    private String playerId;
    private Bitmap playerImg;
    private String playerStatus;
    private boolean isManager;

    public ViewEventListRow(String name,Bitmap img, String id){
        playerName = name;
        playerId = id;
        playerImg = img;
    }

    public ViewEventListRow(String name, String id){
        playerName = name;
        playerId = id;
    }

    public ViewEventListRow(String name, String id, String status, boolean isManager){
        playerName = name;
        playerId = id;
        playerStatus = status;
        this.isManager = isManager;

    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Bitmap getPlayerImg() {
        return playerImg;
    }

    public void setPlayerImg(Bitmap playerImg) {
        this.playerImg = playerImg;
    }

}
