package com.example.matant.gpsportclient.DataClasses;

import org.json.JSONObject;

/**
 * Created by matant on 12/10/2015.
 */
public class InvitationsRowModel {
    private String place,time,event_date,event_curr_participants,id;
    private int sportImage;
     private JSONObject eventRecord;

    public InvitationsRowModel(String loc,String time,String date,String curr,String id,int sportType,JSONObject obj){
        this.place = loc;
        this.time = time;
        this.event_date = date;
        this.event_curr_participants = curr;
        this.id = id;
        this.sportImage = sportType;
        this.eventRecord = obj;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_curr_participants() {
        return event_curr_participants;
    }

    public void setEvent_curr_participants(String event_curr_participants) {
        this.event_curr_participants = event_curr_participants;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSportImage() {
        return sportImage;
    }

    public void setSportImage(int sportImage) {
        this.sportImage = sportImage;
    }

    public JSONObject getEventRecord() {
        return eventRecord;
    }
}
