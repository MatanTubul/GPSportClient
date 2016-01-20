package com.example.matant.gpsportclient.DataClasses;

import android.graphics.Bitmap;

/**
 * class which demonstrate individual list of players in
 * the view event fragment
 * Created by nirb on 11/25/2015.
 */
public class ViewEventListRow {

    private String playerName;
    private String playerId;
    private Bitmap playerImg;

    public String getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(String playerStatus) {
        this.playerStatus = playerStatus;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager;
    }

    private String playerStatus;
    private boolean isManager;

    public ViewEventListRow(String name, String id, Bitmap img, String status, boolean isManager){
        playerName = name;
        playerId = id;
        playerStatus = status;
        playerImg = img;
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
