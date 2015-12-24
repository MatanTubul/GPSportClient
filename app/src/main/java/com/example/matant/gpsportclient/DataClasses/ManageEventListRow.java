package com.example.matant.gpsportclient.DataClasses;

import org.json.JSONObject;

/**
 * Created by matant on 11/4/2015.
 */
public class ManageEventListRow {
    private int sportImage;
    private  String sportType,Date,Location,Participants,eventId,etime;
    private JSONObject eventRecord;
    private boolean isManager;


    public ManageEventListRow(int image,String type, String loc, String date,String partic,String eventid,String event_time, JSONObject eventObj,boolean isMng ){
        this.sportImage = image;
        this.sportType = type;
        this.Date = date;
        this.Location  = loc;
        this.Participants = partic;
        this.eventId = eventid;
        this.etime = event_time;
        this.eventRecord = eventObj;
        this.isManager = isMng;
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

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public JSONObject getEventRecord() {
        return eventRecord;
    }

    public void setEventRecord(JSONObject eventRecord) {
        this.eventRecord = eventRecord;
    }

    public boolean isManager() {
        return isManager;
    }
}
