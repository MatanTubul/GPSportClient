package com.example.matant.gpsportclient;



/**
 * Created by nir b on 11/08/2015.
 */
public interface AsyncResponse {
    void handleResponse(String resStr);
    void sendDataToDBController();
    void preProcess();
}