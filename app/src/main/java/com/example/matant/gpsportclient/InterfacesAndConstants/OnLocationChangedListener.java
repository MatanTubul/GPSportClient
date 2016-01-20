package com.example.matant.gpsportclient.InterfacesAndConstants;

/**
 * class which handling sets of function that execute
 * when the user location is changed
 * Created by nirb on 12/19/2015.
 */
public interface OnLocationChangedListener {

    void startLocationUpdates(); // get to retrieve location updates
    void stopLocationUpdates(); // stop to retrieve location updates
    void updateUI(); // updating the map on the UI
}
