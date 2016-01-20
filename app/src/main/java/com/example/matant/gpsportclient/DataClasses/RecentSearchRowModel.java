package com.example.matant.gpsportclient.DataClasses;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * this class demonstrate an individual row from the
 * recent searches list
 * Created by matant on 12/21/2015.
 */
public class RecentSearchRowModel {
    private JSONObject jobjData;

    public RecentSearchRowModel(String jobj){
        try {
            this.jobjData = new JSONObject(jobj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJobjData() {
        return jobjData;
    }

    public void setJobjData(JSONObject jobjData) {
        this.jobjData = jobjData;
    }
    public String getValue(String key){
        String res = "";
        try {
            res =  this.jobjData.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

}
