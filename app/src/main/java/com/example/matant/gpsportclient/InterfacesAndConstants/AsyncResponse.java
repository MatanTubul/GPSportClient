package com.example.matant.gpsportclient.InterfacesAndConstants;



/**
 * our interface that implemented by each class that should
 * connect to the DB
 * Created by nir b on 11/08/2015.
 */
public interface AsyncResponse {
    void handleResponse(String resStr); // return the result form the server
    void sendDataToDBController(); // send a request to the server
    void preProcess(); // spinner that show popup window while the request to the server is executing.
}