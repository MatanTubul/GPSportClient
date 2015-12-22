package com.example.matant.gpsportclient.Utilities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnLocationChangedListener;
import com.example.matant.gpsportclient.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

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
    private LocationManager locationManager;
    private boolean gpsEnabled,networkEnabled;

    public LocationTool(Fragment f){
        this.frag = f;
    }

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
        locationManager = (LocationManager) frag.getActivity().getSystemService(Context.LOCATION_SERVICE);
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!gpsEnabled && !networkEnabled){
            Log.d("Location Services is off","failed");
            LocationAlertDialog();
        }
        else
        {
            Log.d("Location Services is on","OK");
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(Constants.UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
            if(gpsEnabled)
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            else
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setSmallestDisplacement(Constants.DISPLACEMENT);
        }

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
        Log.d("Location services is off","failed");
    }

    public void LocationAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(frag.getActivity());
        alertDialog.setTitle("Location settings");
        alertDialog.setIcon(R.drawable.warning_32);
        alertDialog.setMessage("We cannot retrieve your location. Please click on Settings and make sure your Location services is enabled");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        frag.getActivity().startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    /**
     * this function get Longitude and Latitude coordinates and send back the real street address.
     * @param LATITUDE
     * @param LONGITUDE
     * @param ctx
     * @return
     */
    public String getCompleteAddressString(double LATITUDE, double LONGITUDE, Context ctx) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current location address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location address", "Can not get Address!");
        }
        return strAdd;
    }

    /**
     * this function convert real address to geographical coordinates.
     * @param strAddress -real address
     * @return LatLng object which contain the coordinates
     */
    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(frag.getActivity());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
            Log.d("coordinates",p1.latitude+""+p1.longitude);

        } catch (Exception ex) {
            Log.d("Location Exception", "error converting address");
            ex.printStackTrace();
        }

        return p1;
    }




}
