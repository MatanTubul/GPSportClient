package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.AddressFetcher;
import com.example.matant.gpsportclient.Utilities.MapMarker;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Thia Fragment handle the Home screen and loading the events that close to the current user location
 * Created by matant on 8/24/2015.
 */
public class GoogleMapFragmentController extends Fragment implements AsyncResponse,com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    MapView mMapView;
    private GoogleMap googleMap;
    private MarkerOptions currentMarkerOption;
    private Marker currentMarker;
    private List<MapMarker> eventsMarkers;
    private HashMap<Marker, MapMarker> mMarkersHashMap;
    private SessionManager sm;
    private LocationRequest mLocationRequest;
    protected Location mLastLocation;
    private boolean mRequestingLocationUpdates = true;
    private LatLng iniLoc;
    private AddressResultReceiver mResultReceiver;
    private boolean mAddressRequested = false;
    private static final String TAG = GoogleMapFragmentController.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private DBcontroller dbController;
    private ProgressDialog progress;
    private LayoutInflater inflater;
    private Fragment fragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        this.inflater = inflater;
        mMarkersHashMap = new HashMap<Marker, MapMarker>();
        View v = inflater.inflate(R.layout.fragment_google_map_fragment_controller, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();//    display map immediately
        sm = SessionManager.getInstance(getActivity());

        if (mMapView!=null)
            Log.d("mMapView!=null", "mMapView!=null");

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mMapView.getMap();
        if (googleMap != null)
        {
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
                @Override
                public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
                {
                    marker.showInfoWindow();
                    return true;
                }
            });

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    MapMarker mapMarker = mMarkersHashMap.get(marker);
                    if (mapMarker != null) { //if marker == null this marker isn't in the hash => current location marker
                        fragment = new ViewEventFragmentController();
                        Bundle args = new Bundle();
                        args.putString("event",mapMarker.getmJsonObject().toString());
                        fragment.setArguments(args);
                        if (fragment != null) {
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        } else {
                            Log.e("GoogleMap Fragment", "Error in creating fragment");
                        }
                    }
                }
            });

        }
        else
            Toast.makeText(this.getActivity(), "Unable to create Maps", Toast.LENGTH_SHORT).show();

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }
        return v;
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

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(Constants.DISPLACEMENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        checkPlayServices();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if (mGoogleApiClient.isConnected())
            stopLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);}

    protected void stopLocationUpdates() {
         LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);}

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void handleResponse(String resStr) {
        progress.dismiss();

        Log.d("handleResponse events", resStr);
        if (resStr != null){
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);
                switch (flg){
                    case Constants.TAG_REQUEST_SUCCEED:
                    {
                        JSONArray jsonarr = jsonObj.getJSONArray("events");
                        Log.d("creating list",jsonarr.toString());
                        Log.d("array", jsonarr.toString());
                        drawEventsMarkersOnMap(jsonarr);
                        break;
                    }

                    case Constants.TAG_REQUEST_FAILED:
                    {
                        Log.d("message from server",jsonObj.getString("msg"));
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void drawEventsMarkersOnMap(JSONArray jsonarr) {
        if (googleMap != null && jsonarr.length() != 0)
        {
            if (eventsMarkers != null)
                deletePreviousMarkers();
            eventsMarkers = new ArrayList<MapMarker>();
            Log.d("drawEventsMarkersOnMap", "before for loop");
            for (int i = 0; i < jsonarr.length(); i++) {
                try {
                //adding marker to the arraylist eventsmarkers which every object is a class MapMarker variable
                //marker info is saved upon MapMarker constructor
                    eventsMarkers.add(new MapMarker(jsonarr.getJSONObject(i)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }//for
            plotMarkers();
        }//if
    }
    private void deletePreviousMarkers()
    {
        int eventsMarkersSize = eventsMarkers.size();
        Log.d("deletePreviousMarkers", "before for loop");
        for (int i = 0; i < eventsMarkersSize; i++) {
            Log.d("deletePreviousMarkers", "remove marker: " + String.valueOf(i));
            mMarkersHashMap.remove(eventsMarkers.get(i).getmMarker()); //remove MapMarker from hash
            eventsMarkers.get(i).getmMarker().remove();                //remove Marker from MapMarker
        }
        eventsMarkers.clear();                                          //remove all MapMarkers from list
        Log.d("deletePreviousMarkers", "markers removed");

    }
    private void plotMarkers()
    {
        if(eventsMarkers.size() > 0)
        {   Log.d("plotMarkers","eventsMarkers.size() > 0");
            for (MapMarker mapMarker : eventsMarkers)
            {
                // Create event marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(mapMarker.getmLatitude(), mapMarker.getmLongitude()));
                markerOption.icon(mapMarker.getmIcon());
                markerOption.title(mapMarker.getmLabel() + String.valueOf(mapMarker.getmLatitude()) + String.valueOf(mapMarker.getmLongitude()));
                // Save Marker pointer for updating later
                currentMarker = googleMap.addMarker(markerOption);
                mapMarker.setmMarker(currentMarker);
                // Set Info for this marker and marker to hashmap for later use
                mMarkersHashMap.put(currentMarker, mapMarker);
                googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
            }
            Log.d("plotMarkers","after for loop");
        }

    }
    @Override
    public void sendDataToDBController() {
        Log.d("sendDataToDBController", "sendDataToDBController");

        BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "search_events");
        BasicNameValuePair radius = new BasicNameValuePair(Constants.TAG_RADIUS, String.valueOf(Constants.DEFAULT_RADIUS));
        BasicNameValuePair lat = new BasicNameValuePair(Constants.TAG_LONG,String.valueOf(mLastLocation.getLongitude()));
        BasicNameValuePair lon = new BasicNameValuePair(Constants.TAG_LAT,String.valueOf(mLastLocation.getLatitude()));

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(radius);
        nameValuePairList.add(lat);
        nameValuePairList.add(lon);

        dbController = new DBcontroller(getActivity().getApplicationContext(),this);
        dbController.execute(nameValuePairList);
    }

    @Override
    public void preProcess() {
        progress = ProgressDialog.show(this.getActivity(), "Search events", "Updating map with events near by ", true);

    }

     /**
      * method which handling the requests for  back button in the  device
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

            updateUI();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
            mLastLocation = location;
            updateUI();
    }

    private void updateUI() {

        double latitude = mLastLocation.getLatitude(),
               longitude =  mLastLocation.getLongitude();

        Log.d("lat and long", latitude+" "+longitude );


        if (googleMap != null) {
            Log.d("googleMap", "googleMap");
            if (currentMarker != null)
                currentMarker.remove();
            iniLoc = new LatLng(latitude, longitude);
            CameraPosition cp = new CameraPosition.Builder().target(iniLoc).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            currentMarkerOption = new MarkerOptions();
            currentMarkerOption.position(iniLoc);
            currentMarker = googleMap.addMarker(currentMarkerOption);
            currentMarker.setIcon((BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker_icon)));
            currentMarker.setTitle(sm.getUserDetails().get("name") + " " + latitude + " " + longitude);
            currentMarker.showInfoWindow();
            currentMarker.setVisible(true);
            sendDataToDBController();
        }
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        public MarkerInfoWindowAdapter()
        {}

        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(Marker markerMapMarkerKey)
        {
            View v  = inflater.inflate(R.layout.infowindow_layout, null);
            MapMarker marker = mMarkersHashMap.get(markerMapMarkerKey);
            if (marker != null) { //if marker == null this marker isn't in the hash => current location marker
                ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);
                TextView markerLabel = (TextView) v.findViewById(R.id.marker_label);
                markerIcon.setImageResource(marker.getmBitmap());
                markerLabel.setText(marker.getmLabel());
            }
            return v;
        }
        }

    //---------------------------------

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
