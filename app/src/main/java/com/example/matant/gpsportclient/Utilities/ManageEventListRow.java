package com.example.matant.gpsportclient.Utilities;

import android.widget.ImageButton;

/**
 * Created by matant on 11/4/2015.
 */
public class ManageEventListRow {
    private int sportImage;
    private  String sportType,Date,Location,Participants;

    public ManageEventListRow(int image,String type, String loc, String date,String partic){
        this.sportImage = image;
        this.sportType = type;
        this.Date = date;
        this.Location  = loc;
        this.Participants = partic;
    }

    public int getSportImage() {
        return sportImage;
    }

    public void setSportImage(int sportImage) {
        this.sportImage = sportImage;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getEventLocation() {
        return Location;
    }

    public void setEventLocation(String location) {
        Location = location;
    }

    public String getParticipants() {
        return Participants;
    }

    public void setParticipants(String participants) {
        Participants = participants;
    }
}
