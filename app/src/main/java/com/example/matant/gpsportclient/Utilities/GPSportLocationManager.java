package com.example.matant.gpsportclient.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnLocationFoundListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by matant on 12/17/2015.
 */
public class GPSportLocationManager {

    private boolean gpsEnabled,networkEnabled,isLocationUpdating,tryLocating;
    private LocationManager locationManager;
    private Location currentLoc=null;
    private Context context;
    private OnLocationFoundListener listener;
    private int networkLocCount=0,gpsLocCount=0;

    public GPSportLocationManager(Context ctx,OnLocationFoundListener mListener){
        this.context = ctx;
        this.listener = mListener;
    }

    /**
     * method which finding the device location.
     */
    public void getLocation(){

        try {
            tryLocating = true;
            locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!gpsEnabled && !networkEnabled){
                LocationAlertDialog();
            }else{
                listener.getLocationInProccess();
                if(gpsEnabled){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            Constants.GPS_MIN_TIME_BETWEEN_UPDATE,Constants.GPS_MIN_DISTANCE_CHANGE,gpsListener);

                }if(networkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            Constants.NETWORK_MIN_TIME_BETWEEN_UPDATE,Constants.NETWORK_MIN_DISTANCE_CHANGE,networkListener);
                }
            }
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(new Runnable() {
                @Override
                public void run() {
                    if(currentLoc == null){
                        if(gpsEnabled){
                            currentLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }else if(networkEnabled){
                            currentLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        Log.d("thread","outside");
                        if(currentLoc != null && listener !=null){
                            locationManager.removeUpdates(gpsListener);
                            locationManager.removeUpdates(networkListener);
                            Log.d("thread", "inside");
                            listener.onLocationFound(currentLoc);
                        }
                    }
                }
            },30, TimeUnit.SECONDS);
        }catch (Exception e){
            Toast.makeText(context,"Error while getting location"+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }



    /**
     * GPS location listener handle the callbacks.
     */
    private LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        if(gpsLocCount != 0 && !isLocationUpdating){
                isLocationUpdating = true;
            currentLoc = location;
            locationManager.removeUpdates(gpsListener);
            locationManager.removeUpdates(networkListener);
            isLocationUpdating = false;
            if(currentLoc != null && listener !=null){
                listener.onLocationFound(currentLoc);
                }
            }
            gpsLocCount++;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            getLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private  LocationListener networkListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(networkLocCount != 0 && !isLocationUpdating){
                isLocationUpdating = true;
                currentLoc = location;
                locationManager.removeUpdates(gpsListener);
                locationManager.removeUpdates(networkListener);
                isLocationUpdating = false;
                if(currentLoc != null && listener !=null){
                    listener.onLocationFound(currentLoc);
                }
            }
            networkLocCount++;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            getLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void LocationAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Location settings");
        alertDialog.setMessage("We cannot retrieve your location. Please click on Settings and make sure your GPS is enabled");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tryLocating = false;
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
        Geocoder coder = new Geocoder(context);
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

    public boolean isTryLocating() {
        return tryLocating;
    }
}
