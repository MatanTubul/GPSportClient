package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnLocationChangedListener;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.LocationTool;
import com.example.matant.gpsportclient.DataClasses.MapMarker;
import com.example.matant.gpsportclient.Utilities.SessionManager;
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
public class GoogleMapFragmentController extends Fragment implements AsyncResponse, CompoundButton.OnCheckedChangeListener, OnLocationChangedListener, com.google.android.gms.location.LocationListener{

    MapView mMapView;
    private GoogleMap googleMap;
    private MarkerOptions currentMarkerOption;
    private Marker currentMarker;
    private List<MapMarker> eventsMarkers;
    private HashMap<Marker, MapMarker> mMarkersHashMap;
    private SessionManager sm;
    private LatLng iniLoc;
    private DBcontroller dbController;
    private ProgressDialog progress;
    private LayoutInflater inflater;
    private Fragment fragment = null;
    private LocationTool locationTool;
    private String mode;
    double latitude, longitude;
    private ImageButton goToLastLocation;
    private LatLng lastLocation = null;
    private Switch searchEventsSwitch;
    private BasicNameValuePair search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        this.inflater = inflater;
        mMarkersHashMap = new HashMap<Marker, MapMarker>();
        View v = inflater.inflate(R.layout.fragment_google_map_fragment_controller, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        goToLastLocation = (ImageButton) v.findViewById(R.id.googleMapCurrentlocButton);
        //mMapView.onResume();//    display map immediately
        sm = SessionManager.getInstance(this.getActivity());

        search = new BasicNameValuePair(Constants.TAG_SEARCH,"search_by_default");

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
                    if (!marker.getId().equals(currentMarker.getId()))
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

        searchEventsSwitch = (Switch) v.findViewById(R.id.searchEventsSwitch);
        searchEventsSwitch.setOnCheckedChangeListener(this);


        Log.d("googleMap", "googleMap");

        mode = Constants.MODE_SEARCH_DEF;
        Bundle b = getArguments();
        if(b != null) {
            mode = b.getString(Constants.TAG_REQUEST);
            latitude = Double.valueOf(b.getString(Constants.TAG_LAT));
            longitude = Double.valueOf(b.getString(Constants.TAG_LONG));
            Log.d("b!=null",b.getString(Constants.TAG_LAT) + " " + b.getString(Constants.TAG_LONG));
            Log.d("mode",mode);

            if (mode.equals(Constants.MODE_SEARCH_REQ)) {
                getActivity().setTitle("Search Results");
                searchEventsSwitch.setVisibility(View.GONE);
                Log.d("mode","==search by REQ");
                try {
                    JSONObject json = new JSONObject(b.getString("json"));
                    Log.d("events from search", json.toString());
                    GetEventsFromJSONToMapMarkers(json);
                    updateUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        goToLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastLocation != null){
                    float zoomLevel = (float) 16.0; //This goes up to 21
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, zoomLevel));
                }

            }
        });
        if (!(mode.equals(Constants.MODE_SEARCH_REQ)))
                 locationTool = new LocationTool(this, this);
        return v;
    }

    @Override
    public void onStart() {
        Log.d("this is", "start");
        super.onStart();
        if (mode.equals(Constants.MODE_SEARCH_DEF))
            if (locationTool.getmGoogleApiClient() != null)
                locationTool.getmGoogleApiClient().connect();
    }

    @Override
    public void onResume() {
        Log.d("this is map", "resume");
        super.onResume();
        mMapView.onResume();
        if(this.progress !=null)
            this.progress.dismiss();
        if (mode.equals(Constants.MODE_SEARCH_DEF)) {
            locationTool.checkPlayServices();
            if (locationTool.getmGoogleApiClient().isConnected() && !locationTool.ismRequestingLocationUpdates()) {
               startLocationUpdates();
             }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("this is", "pause");
        mMapView.onPause();
        if(this.progress !=null)
            this.progress.dismiss();
        if (mode.equals(Constants.MODE_SEARCH_DEF))
            if (locationTool.getmGoogleApiClient().isConnected())
                stopLocationUpdates();
    }

    @Override
    public void onStop() {
        Log.d("this is", "stop");
        super.onStop();
        if(this.progress !=null)
            this.progress.dismiss();
        if (mode.equals(Constants.MODE_SEARCH_DEF))
            if (locationTool.getmGoogleApiClient().isConnected())
                locationTool.getmGoogleApiClient().disconnect();

    }

    @Override
    public void onDestroy() {
        Log.d("this is", "destroy");
        super.onDestroy();
        mMapView.onDestroy();
        if (this.progress != null)
            this.progress.dismiss();
        if (mode.equals(Constants.MODE_SEARCH_DEF)) {
            if (locationTool.getmGoogleApiClient().isConnected()) {
                stopLocationUpdates();
                locationTool.getmGoogleApiClient().disconnect();
            }
        }
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void startLocationUpdates() {
        if (mode.equals(Constants.MODE_SEARCH_DEF)) {
            if (locationTool.getmLocationRequest() != null)
                LocationServices.FusedLocationApi.requestLocationUpdates(locationTool.getmGoogleApiClient(), locationTool.getmLocationRequest(), this);
        }
    }

    public void stopLocationUpdates() {
        if (mode.equals(Constants.MODE_SEARCH_DEF))
            LocationServices.FusedLocationApi.removeLocationUpdates(locationTool.getmGoogleApiClient(), this);
    }
    @Override
    public void handleResponse(String resStr) {
        this.progress.dismiss();

        if (resStr != null){
            Log.d("handleResponse events", resStr);
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);
                switch (flg){
                    case Constants.TAG_REQUEST_SUCCEED:
                    {
                        GetEventsFromJSONToMapMarkers(jsonObj);
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

    private void GetEventsFromJSONToMapMarkers (JSONObject jsonObj)
    {
        try {
            JSONArray jsonarr = jsonObj.getJSONArray("events");
            Log.d("creating list", jsonarr.toString());
            drawEventsMarkersOnMap(jsonarr);
        }catch (JSONException e) {
            e.printStackTrace();
    }

    }


    private void drawEventsMarkersOnMap(JSONArray jsonarr) {
        if (googleMap != null) {
            if (eventsMarkers != null) {
                deletePreviousMarkers();
                Log.d("eventsMarkers", "!= null");

            }
            if (jsonarr.length() != 0) {

                eventsMarkers = new ArrayList<MapMarker>();
                Log.d("drawEventsMarkersOnMap", "before for loop");

                for (int i = 0; i < jsonarr.length(); i++) {
                    try {
                        //adding marker to the arraylist eventsmarkers which every object is a class MapMarker variable
                        //marker info is saved upon MapMarker constructor
                        eventsMarkers.add(new MapMarker(jsonarr.getJSONObject(i)));
                        Log.d("drawEventsMarkersOnMap inside loop", String.valueOf(i));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }//for
                plotMarkers();
            }//if
            else {
                //no events has found
                String msg = "There are no ";
                if (mode.equals(Constants.MODE_SEARCH_REQ))
                    msg = msg + "results according to your search. Try again a different search.";
                else if (mode.equals(Constants.MODE_SEARCH_DEF))
                    msg = msg + "events nearby. Try search from a specific address or change your location.";
                else
                    msg = msg + "events on data base. Ask the app administrators";

                new AlertDialog.Builder(getActivity())
                        .setTitle("No events found")
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .show();
            }
        }
    }
    private void deletePreviousMarkers()
    {
        int eventsMarkersSize = eventsMarkers.size();
        Log.d("deletePreviousMarkers", "before for loop");
        for (int i = 0; i < eventsMarkersSize; i++) {
            Log.d("deletePreviousMarkers", "remove marker: " + String.valueOf(i));
            Marker thisMarker = eventsMarkers.get(i).getmMarker();

            mMarkersHashMap.remove(thisMarker); //remove MapMarker from hash
            thisMarker.remove();                //remove Marker from Map

        }
        eventsMarkers.clear();                                          //remove all MapMarkers from list
        Log.d("deletePreviousMarkers", "markers removed");

    }
    private void plotMarkers()
    {
        if(eventsMarkers.size() > 0)
        {   Log.d("plotMarkers", String.valueOf(eventsMarkers.size()));
            for (MapMarker mapMarker : eventsMarkers)
            {
                // Create event marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(mapMarker.getmLatitude(), mapMarker.getmLongitude()));
                markerOption.icon(mapMarker.getmIcon());
                markerOption.title(mapMarker.getmLabel() + String.valueOf(mapMarker.getmLatitude()) + String.valueOf(mapMarker.getmLongitude()));
                // Save Marker pointer for updating later
                Marker currMarker;
                currMarker = googleMap.addMarker(markerOption);
                mapMarker.setmMarker(currMarker);
                // Set Info for this marker and marker to hashmap for later use
                mMarkersHashMap.put(currMarker, mapMarker);
                googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
            }
            Log.d("plotMarkers","after for loop");
        }

    }
    @Override
    public void sendDataToDBController() {
        Log.d("sendDataToDBController", "sendDataToDBController");
        if (locationTool.getmLastLocation()!=null) {

            BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "search_events");
            BasicNameValuePair radius = new BasicNameValuePair(Constants.TAG_RADIUS, String.valueOf(Constants.DEFAULT_RADIUS));
            BasicNameValuePair lat = new BasicNameValuePair(Constants.TAG_LONG,String.valueOf(locationTool.getmLastLocation().getLongitude()));
            BasicNameValuePair lon = new BasicNameValuePair(Constants.TAG_LAT,String.valueOf(locationTool.getmLastLocation().getLatitude()));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(tagreq);
            nameValuePairList.add(search);
            nameValuePairList.add(radius);
            nameValuePairList.add(lat);
            nameValuePairList.add(lon);

            dbController = new DBcontroller(getActivity().getApplicationContext(),this);
            dbController.execute(nameValuePairList);
        }
        else
            new AlertDialog.Builder(getActivity())
                    .setTitle("Can't get current location")
                    .setMessage("Please enable location service.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .show();
    }

    @Override
    public void preProcess() {
        this.progress = ProgressDialog.show(this.getActivity(), "Search events", "Updating map with events nearby...",false,true);
    }

     /**
      * method which handling the requests for  back button in the  device
      * @param //savedInstanceState
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
                       getActivity().moveTaskToBack(true);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mode.equals(Constants.MODE_SEARCH_DEF)) {
            locationTool.setmLastLocation(location);
            if (eventsMarkers != null)
                deletePreviousMarkers();
            updateUI();
        }
    }

    public void updateUI() {
        if(this.progress!= null)
            this.progress.dismiss();

        if (mode.equals(Constants.MODE_SEARCH_DEF)) {
            latitude = locationTool.getmLastLocation().getLatitude();
            longitude =  locationTool.getmLastLocation().getLongitude();
        }


        Log.d("lat and long", latitude+" "+longitude );
        if (googleMap != null) {
            Log.d("googleMap", "googleMap");
            if (currentMarker != null)
                currentMarker.remove();
            iniLoc = new LatLng(latitude, longitude);
            lastLocation = iniLoc;
            CameraPosition cp = new CameraPosition.Builder().target(iniLoc).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            currentMarkerOption = new MarkerOptions();
            currentMarkerOption.position(iniLoc);
            currentMarker = googleMap.addMarker(currentMarkerOption);
            currentMarker.setIcon((BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker_icon)));
            String title = sm.getUserDetails().get("name") + ", you are here!";
            Log.d("test", title);
            currentMarker.setTitle(title);
            //currentMarker.showInfoWindow();
            if (mode.equals(Constants.MODE_SEARCH_DEF))
            {
                Log.d("this is","UpdateUI");
                sendDataToDBController();
            }


        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean userWantsEventsNearBy) {
        if (userWantsEventsNearBy == true) {
            search = new BasicNameValuePair(Constants.TAG_SEARCH, "search_by_default");
            Log.d("this is","search_by_default");
        }else{
            search = new BasicNameValuePair(Constants.TAG_SEARCH,"search_all_events");
            Log.d("this is","search_all_events");
        }

        sendDataToDBController();
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
                TextView markerDistanceFromAddOrCurrent = (TextView) v.findViewById(R.id.km_from_current);
                TextView markerDate = (TextView) v.findViewById(R.id.event_date);
                TextView markerLocation = (TextView) v.findViewById(R.id.event_time);
                TextView markerTime = (TextView) v.findViewById(R.id.event_location);

                markerIcon.setImageResource(marker.getmBitmap());
                markerLabel.setText(marker.getmLabel());
                markerDistanceFromAddOrCurrent.setText(getDistance(marker.getmLongitude(), marker.getmLatitude()));
                String start = marker.getmStartTime(), end = marker.getmStartTime();
                String startTime = start.substring(11), endTime = end.substring(11);
                markerDate.setText("Date: " + start.substring(0, start.length() - 9));


                markerTime.setText("Time: " +  startTime.substring(0, startTime.length() - 3)
                        + " - " + endTime.substring(0, endTime.length() - 3));
                markerLocation.setText("Address: " + marker.getmLocation());
            }
            return v;
        }
        }

    String getDistance (double eventLongitude,double eventLatitude)
    {
        Double distanceVal = Math.acos(Math.sin(eventLatitude * 0.0175) *
                Math.sin(latitude * 0.0175)
                + Math.cos(eventLatitude * 0.0175) * Math.cos(latitude * 0.0175) *
                Math.cos((longitude * 0.0175) - (eventLongitude * 0.0175))) * 6371;

        String distance = null;
        if (distanceVal < 1) {
            distanceVal *= 1000;
            distance= distanceVal.toString();
            distance = "Distance: " + distance.substring(0, distance.indexOf(".")) + " m";
        }
        else
        {
            distance= distanceVal.toString();
            distance = "Distance: " + distance.substring(0, distance.indexOf(".")) + " km";
        }
        return distance;

    }
}





