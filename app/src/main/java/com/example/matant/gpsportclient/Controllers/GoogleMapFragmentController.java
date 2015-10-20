package com.example.matant.gpsportclient.Controllers;

import android.app.Fragment;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.AddressFetcher;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Thia Fragment handle the Home screen and loading the events that close to the current user location
 * Created by matant on 8/24/2015.
 */
public class GoogleMapFragmentController extends Fragment implements AsyncResponse,com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    MapView mMapView;
    private GoogleMap googleMap;
    private MarkerOptions currentMarkerOption;
    private Marker currentMarker;
    private SessionManager sm;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected Location mLastLocation;
    private boolean mRequestingLocationUpdates = true;
    private LatLng iniLoc;
    private boolean firstLocation = true;
    private AddressResultReceiver mResultReceiver;
    private boolean mAddressRequested = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_google_map_fragment_controller, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();//    display map immediately
        sm = new SessionManager(getActivity());

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        createLocationRequest();
        googleMap = mMapView.getMap();
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        return v;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (mGoogleApiClient!=null)
            Log.d("mGoogleApiClient!=null", "mGoogleApiClient!=null");

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void handleResponse(String resStr) {

    }

    @Override
    public void sendDataToDBController() {

    }

    @Override
    public void preProcess() {

    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
// Gets the best and most recent location currently available,
            // which may be null in rare cases when a location is not available.
        Log.d("onConnected", "onConnected");
            if (mRequestingLocationUpdates) {
                startLocationUpdates();
                Log.d("startLocationUpdates", "startLocationUpdates");
            }

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                Log.d("mLastLocation != null", "mLastLocation != null");

                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Log.d("no geocoder", "no geocoder");
                    Toast.makeText(this.getActivity(), "no gecoder", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mAddressRequested)
                    startIntentService();
                updateUI();
            }

        }

        @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {

        double latitude = mLastLocation.getLatitude(),
               longitude =  mLastLocation.getLongitude();

        if (googleMap != null) {
                Log.d("googleMap", "googleMap");
                if (!firstLocation)
                    currentMarker.setVisible(false);
                else
                    firstLocation = false;
                iniLoc = new LatLng(latitude, longitude);
                CameraPosition cp = new CameraPosition.Builder().target(iniLoc).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                currentMarkerOption = new MarkerOptions();
                currentMarkerOption.position(iniLoc);
                currentMarker = googleMap.addMarker(currentMarkerOption);
                currentMarker.setIcon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                currentMarker.setTitle(sm.getUserDetails().get("name") + " " + latitude + " " + longitude);
                currentMarker.showInfoWindow();
                currentMarker.setVisible(true);

            }
        //mLastUpdateTimeTextView.setText(mLastUpdateTime);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        //Display the result address from the addFetcher
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            //displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast("address found");
            }

        }
    }


    public void fetchAddressButtonHandler(View view) {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        //updateUIWidgets();
    }

    protected void startIntentService() {
        Intent intent = new Intent(this.getActivity(), AddressFetcher.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }
}
