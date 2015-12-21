package com.example.matant.gpsportclient.Utilities;

/**
 * this class is representing a specific row in the Waiting Event List Adapter.
 * Created by matant on 12/16/2015.
 */
public class WaitingEventUserRow  {
    private String place,time,event_date,event_curr_participants,id;
    private String queueIndex;
    private int sportImage;

    public WaitingEventUserRow(String loc,String time,String date,String curr,String id,String  placeInQueue,int sportType){
        this.place = loc;
        this.time = time;
        this.event_date = date;
        this.event_curr_participants = curr;
        this.id = id;
        this.queueIndex = placeInQueue;
        this.sportImage = sportType;
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

    public String getPlaceInQueue() {
        return queueIndex;
    }

    public void setPlaceInQueue(String placeInQueue) {
        this.queueIndex = placeInQueue;
    }

    public int getSportImage() {
        return sportImage;
    }

    public void setSportImage(int sportImage) {
        this.sportImage = sportImage;
    }
}
