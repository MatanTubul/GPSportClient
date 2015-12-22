package com.example.matant.gpsportclient.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
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