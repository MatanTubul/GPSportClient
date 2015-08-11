package com.example.matant.gpsportclient;

import java.io.InputStream;

/**
 * Created by nir b on 11/08/2015.
 */
public interface AsyncResponse {
    void handleResponse(InputStream output);

    void sendDataToDBController();
}