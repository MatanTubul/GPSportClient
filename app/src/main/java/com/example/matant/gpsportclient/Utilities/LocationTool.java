package com.example.matant.gpsportclient.Utilities;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnLocationChangedListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 *  * This class will manage the GoogleApiClient and the location changing handle
 * Created by nirb on 12/18/2015.
 */
public class LocationTool implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private Fragment frag;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private boolean mRequestingLocationUpdates;
    public OnLocationChangedListener delegate= null;

    public LocationTool (Fragment frag, OnLocationChangedListener onLocationChangedListener)
    {
        mRequestingLocationUpdates = true;
        delegate = onLocationChangedListener;
        this.frag = frag;
        buildGoogleApiClientAndCreateLocationRequest();
    }

    public boolean ismRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public LocationRequest getmLocationRequest() {
        return mLocationRequest;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public Location getmLastLocation() {
        return mLastLocation;
    }

    public void setmLastLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
    }

    public void buildGoogleApiClientAndCreateLocationRequest() {

    if (checkPlayServices()) {
        buildGoogleApiClient();
        createLocationRequest();
    }
}

    private synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(frag.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (mGoogleApiClient!=null)
            Log.d("mGoogleApiClient!=null", "mGoogleApiClient!=null");
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(Constants.DISPLACEMENT);
    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(frag.getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, frag.getActivity(), Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(frag.getActivity().getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                frag.getActivity().finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onConnected(Bundle bundle) {

            // Gets the best and most recent location currently available,
            // which may be null in rare cases when a location is not available.
            Log.d("Connected", "Connected");

            if (mRequestingLocationUpdates) {
                delegate.startLocationUpdates();
                Log.d("startLocationUpdates", "startLocationUpdates");
            }

            this.setmLastLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
            if (mLastLocation != null) {
                Log.d("mLastLocation != null", "mLastLocation != null");
                delegate.updateUI();
            }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(this.frag.getClass().toString(), "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }




}
