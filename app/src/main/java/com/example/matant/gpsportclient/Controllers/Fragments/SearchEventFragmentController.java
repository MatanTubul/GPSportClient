package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnCompleteListener;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnLocationChangedListener;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnLocationFoundListener;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.DateAndTimeFunctions;
import com.example.matant.gpsportclient.Utilities.DatePicker;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.Utilities.GPSportLocationManager;
import com.example.matant.gpsportclient.Utilities.LocationTool;
import com.example.matant.gpsportclient.Utilities.MyAdapter;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.example.matant.gpsportclient.Utilities.TimePicker;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchEventFragmentController extends Fragment implements AsyncResponse,View.OnClickListener,OnCompleteListener,OnLocationChangedListener,com.google.android.gms.location.LocationListener {
    private RadioGroup searchRdg;
    private EditText streetAddress,radius,minimumAge;
    private Button dateFrom,dateTo,timeFrom,timeTo,searchEvent;
    private Spinner spinnerKindOfSport,spinnerGender;
    private int pos;
    private SessionManager sm;
    private DBcontroller dbController;
    private LocationTool myLocManager;
    private Location currentLocation;
    private ProgressDialog progress;
    private CheckBox cbPublic,cbPrivate;
    private DateAndTimeFunctions dtFunctions;
    private int settime = 0;
   private boolean SET_DATE = false;
    private boolean SET_TIME =false;
    private DialogFragment tp = null;
    private  DialogFragment df = null;
    private double lon,lat;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       final View rootView = inflater.inflate(R.layout.fragment_search_event_fragment_controller, container, false);

        searchRdg = (RadioGroup) rootView.findViewById(R.id.radioGroupSearchFragment);
        streetAddress = (EditText) rootView.findViewById(R.id.editTextSearchAddress);
        radius = (EditText) rootView.findViewById(R.id.editTextSearchRadius);
        minimumAge = (EditText) rootView.findViewById(R.id.editTextSearchMinAge);
        dateFrom = (Button) rootView.findViewById(R.id.buttonSearchEventFrom);
        dateTo = (Button) rootView.findViewById(R.id.buttonSearchEventTo);
        timeFrom = (Button) rootView.findViewById(R.id.buttonSearchStartTime);
        timeTo = (Button) rootView.findViewById(R.id.buttonSearchEndTime);
        searchEvent = (Button) rootView.findViewById(R.id.buttonSearchEventExecute);
        spinnerGender = (Spinner) rootView.findViewById(R.id.spinnerSearchGender);
        spinnerKindOfSport = (Spinner) rootView.findViewById(R.id.spinnerSerachSportType);
        cbPrivate = (CheckBox) rootView.findViewById(R.id.checkBoxSearchPrivateEvents);
        cbPublic = (CheckBox) rootView.findViewById(R.id.checkBoxSearchPublicEvents);
        sm = SessionManager.getInstance(getActivity());
        pos = 0;
        dtFunctions = new DateAndTimeFunctions();
        myLocManager = new LocationTool(this,this);
        dateFrom.setText(dtFunctions.getCurrentDate());
        dateTo.setText(dtFunctions.getCurrentDate());
        timeFrom.setText(dtFunctions.getCorrentTime());
        timeTo.setText(dtFunctions.getCorrentTime());

        sm = SessionManager.getInstance(getActivity());

        //gender spinner
        spinnerGender.setAdapter(new MyAdapter(getActivity(), R.layout.custom_spinner, getResources().getStringArray(R.array.eventgender)));
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });//gender spinner

        spinnerKindOfSport.setAdapter(new MyAdapter(getActivity(), R.layout.custom_spinner, getResources().getStringArray(R.array.search_kind_of_sport)));
        spinnerKindOfSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });//Sport Spinner

        searchRdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                pos = searchRdg.indexOfChild(rootView.findViewById(checkedId));
                switch (pos) {
                    case 0: {
                        streetAddress.setEnabled(true);
                        break;
                    }
                    case 1: {
                        streetAddress.setEnabled(false);
                        if (myLocManager.getmGoogleApiClient() != null)
                        {
                            Log.d("check if getmGoogleApiClient()", "check location");
                            myLocManager.buildGoogleApiClientAndCreateLocationRequest();
                            myLocManager.getmGoogleApiClient().connect();

                        }

                        break;
                    }
                }
            }
        });
        cbPublic.setOnClickListener(this);
        cbPrivate.setOnClickListener(this);
        searchEvent.setOnClickListener(this);
        dateTo.setOnClickListener(this);
        dateFrom.setOnClickListener(this);
        timeFrom.setOnClickListener(this);
        timeTo.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void handleResponse(String resStr) {
            progress.dismiss();
    }

    @Override
    public void sendDataToDBController() {
        JSONObject jobjRecentSearch = new JSONObject();

        if(pos == 0){
            LatLng loc = myLocManager.getLocationFromAddress(streetAddress.getText().toString());
            lat = loc.latitude;
            lon = loc.longitude;
            if(loc == null){
                streetAddress.setError("Location was not found");
                return;
            }
        }else{

        }
        BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST,"search_event");
        BasicNameValuePair lat_cord = new BasicNameValuePair(Constants.TAG_LAT,String.valueOf(lat));
        BasicNameValuePair lon_cord = new BasicNameValuePair(Constants.TAG_LONG,String.valueOf(lon));
        BasicNameValuePair start_date = new BasicNameValuePair(Constants.TAG_START_DATE,dateFrom.getText().toString());
        BasicNameValuePair end_date = new BasicNameValuePair(Constants.TAG_END_DATE,dateTo.getText().toString());
        BasicNameValuePair start_time = new BasicNameValuePair(Constants.TAG_START_TIME,timeFrom.getText().toString());
        BasicNameValuePair end_time = new BasicNameValuePair(Constants.TAG_END_TIME,timeTo.getText().toString());
        BasicNameValuePair min_age = new BasicNameValuePair(Constants.TAG_MIN_AGE,minimumAge.getText().toString());
        BasicNameValuePair gender = new BasicNameValuePair(Constants.TAG_GEN,spinnerGender.getSelectedItem().toString());
        BasicNameValuePair event_radius = new BasicNameValuePair(Constants.TAG_RADIUS,radius.getText().toString());
        BasicNameValuePair typeOfSport = new BasicNameValuePair(Constants.TAG_KIND_OF_SPORT,spinnerKindOfSport.getSelectedItem().toString());


        boolean eventPrivate,eventPublic;
        eventPrivate = cbPrivate.isChecked();
        eventPublic = cbPublic.isChecked();
        BasicNameValuePair private_checkbox = new BasicNameValuePair(Constants.TAG_PRIVATE,String.valueOf(eventPrivate));
        BasicNameValuePair public_checkbox = new BasicNameValuePair(Constants.TAG_PUBLIC,String.valueOf(eventPublic));

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(lat_cord);
        nameValuePairList.add(lon_cord);
        nameValuePairList.add(start_date);
        nameValuePairList.add(end_date);
        nameValuePairList.add(start_time);
        nameValuePairList.add(end_time);
        nameValuePairList.add(min_age);
        nameValuePairList.add(gender);
        nameValuePairList.add(typeOfSport);
        nameValuePairList.add(event_radius);
        nameValuePairList.add(private_checkbox);
        nameValuePairList.add(public_checkbox);
        Log.d("my nameValuePairList",nameValuePairList.toString());

        try {
            for(int i= 0;i < nameValuePairList.size();i++){
                Log.d("loop index is",String.valueOf(i));
                jobjRecentSearch.put(nameValuePairList.get(i).getName(),nameValuePairList.get(i).getValue());
            }
            Log.d("json 1", jobjRecentSearch.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sm.getSearchIndex() >= 0)
        {
            Log.d("search index is",String.valueOf(sm.getSearchIndex()));
            int rsIndex = sm.getSearchIndex();
            if(rsIndex == 0)
                rsIndex = 5;
            if(rsIndex <= 5)
            {
                switch (rsIndex){
                    case 1:
                        sm.StoreUserSession(jobjRecentSearch.toString(),Constants.TAG_SEARCH1);
                        break;
                    case 2:
                        sm.StoreUserSession(jobjRecentSearch.toString(),Constants.TAG_SEARCH2);
                        break;
                    case 3:
                        sm.StoreUserSession(jobjRecentSearch.toString(),Constants.TAG_SEARCH3);
                        break;
                    case 4:
                        sm.StoreUserSession(jobjRecentSearch.toString(),Constants.TAG_SEARCH4);
                        break;
                    case 5:
                        sm.StoreUserSession(jobjRecentSearch.toString(),Constants.TAG_SEARCH5);
                        break;
                }
                sm.storeIndex((rsIndex - 1));
            }
        }
        dbController = new DBcontroller(getActivity().getApplicationContext(),this);
        dbController.execute(nameValuePairList);
    }

    @Override
    public void preProcess() {
        this.progress = ProgressDialog.show(getActivity(), "Searching Events",
                "Loading...", true);
    }
    private boolean isParamAreValid(){

            boolean valid = true;
        if ((streetAddress.getText().toString().equals("")) && (pos == 0 || pos == 1)){
                valid = false;
                streetAddress.setError("Street Address is missing!");
            }
        if ((minimumAge.getText().toString().equals(""))){
            valid = false;
            minimumAge.setError("Minimum age  is missing!");
        }
        if ((radius.getText().toString().equals(""))) {
            valid = false;
            radius.setError("Radius is missing!");
        }
        if(!(radius.getText().toString().equals(""))) {
            Log.d("field is not",radius.getText().toString());
            if(Integer.valueOf(radius.getText().toString()) < 1 || Integer.valueOf(radius.getText().toString()) > 30){
                valid = false;
                radius.setError("Radius is not in range");
            }

        }
        if(!(minimumAge.getText().toString().equals(""))) {
            Log.d("field is not","empty");
            if(Integer.valueOf(minimumAge.getText().toString()) < 14 || Integer.valueOf(minimumAge.getText().toString()) > 40){
                valid = false;
                minimumAge.setError("Age is not in range");
            }

        }
        return valid ;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = null;
        String string_date = dateFrom.getText().toString();
        String end_date = dateTo.getText().toString();
        String equ_type ="";
        String dialog_type ="";
        if(string_date.equals(end_date))
            equ_type = "1";
        else
            equ_type = "2";
        switch (v.getId()){
            case R.id.checkBoxSearchPrivateEvents:
                   if(!cbPrivate.isChecked())
                       cbPublic.setChecked(true);
                break;
            case R.id.checkBoxSearchPublicEvents:
                if(!cbPublic.isChecked())
                    cbPrivate.setChecked(true);
                break;
            case R.id.buttonSearchEventExecute:
                if(isParamAreValid())
                    sendDataToDBController();
                break;
            case R.id.buttonSearchEventFrom:
            {
                Log.d("this button","from");
                bundle = new Bundle();
                dialog_type = "date";
                bundle.putString(dialog_type, "search");
                bundle.putString("val","1");
                SET_DATE = true;
                break;
            }
            case R.id.buttonSearchEventTo:
            {
                Log.d("this button","to");
                bundle = new Bundle();
                dialog_type = "date";
                bundle.putString(dialog_type, "search");
                bundle.putString("val", "2");
                SET_DATE = true;
                break;
            }
            case R.id.buttonSearchStartTime:
            {
                bundle = new Bundle();
                dialog_type = "val";
                bundle.putString(dialog_type,"1");
                bundle.putString("date",string_date);
                bundle.putString("type","search");
                bundle.putString("equation",equ_type);
                SET_TIME =true;
                break;
            }

            case R.id.buttonSearchEndTime:
            {
                bundle = new Bundle();
                dialog_type = "val";
                bundle.putString(dialog_type,"2");
                bundle.putString("date",string_date);
                bundle.putString("type","search");
                bundle.putString("equation",equ_type);
                SET_TIME =true;
                break;
            }
        }
        if(SET_DATE){
            SET_DATE = false;
            if(df == null)
                df = new DatePicker();
            df.setArguments(bundle);
            df.setTargetFragment(this,0);
            df.show(getFragmentManager(),dialog_type);
        }
        if(SET_TIME)
        {
            SET_TIME = false;
            if(tp == null)
                tp = new TimePicker();
            tp.setArguments(bundle);
            tp.setTargetFragment(this,0);
            tp.show(getFragmentManager(),dialog_type);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if( pos == 1 ){
            Log.d("return from settings", "updating");
            myLocManager.checkPlayServices();
            if (myLocManager.getmGoogleApiClient().isConnected() && !myLocManager.ismRequestingLocationUpdates()) {
                Log.d("my location", "getmGoogleApiClient() true");
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onComplete(String flag, String res) {
        settime = 0;
        switch(flag) {
            case "start_time": {
                timeFrom.setText(res);
                if (settime == 0)
                    timeTo.setText(res);
                settime = 0;
                break;
            }
            case "end_time": {
                timeTo.setText(res);
                settime = 1;
                break;
            }
            case "start_date":
                dateFrom.setText(res);
                break;
            case "end_date":
                dateTo.setText(res);
                break;
            case "incorrect_time": {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Select Time Error")
                        .setMessage(res)
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                timeFrom.setText(dtFunctions.getCorrentTime());
                                timeTo.setText(dtFunctions.getCorrentTime());
                            }
                        })
                        .setIcon(R.drawable.error_32)
                        .show();
                break;
            }
            case "Date_not_valid":
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Date Error!")
                        .setMessage(res)
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dateFrom.setText(dtFunctions.getCurrentDate());
                                dateTo.setText(dtFunctions.getCurrentDate());
                            }
                        })
                        .setIcon(R.drawable.error_32)
                        .show();
                break;
            }
        }
    }

    @Override
    public void startLocationUpdates() {
        if(myLocManager.getmLocationRequest() != null)
        {
            Log.d("my location","startLocationUpdates location");
            LocationServices.FusedLocationApi.requestLocationUpdates(myLocManager.getmGoogleApiClient(), myLocManager.getmLocationRequest(), this);
        }

    }

    @Override
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(myLocManager.getmGoogleApiClient(), this);
    }

    @Override
    public void updateUI() {
        Log.d("my location","updateUI");
        String realAddress = myLocManager.getCompleteAddressString(myLocManager.getmLastLocation().getLatitude(),myLocManager.getmLastLocation().getLongitude(),this.getActivity().getApplicationContext());
        lon = myLocManager.getmLastLocation().getLongitude();
        lat = myLocManager.getmLastLocation().getLatitude();
        if(realAddress != null)
            streetAddress.setText(realAddress);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("my location","onLocationChanged");
        myLocManager.setmLastLocation(location);
        updateUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        myLocManager.checkPlayServices();
        if (myLocManager.getmGoogleApiClient().isConnected() && !myLocManager.ismRequestingLocationUpdates()) {
            startLocationUpdates();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (myLocManager.getmGoogleApiClient().isConnected()) {
            myLocManager.getmGoogleApiClient().disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myLocManager.getmGoogleApiClient().isConnected()) {
            stopLocationUpdates();
            myLocManager.getmGoogleApiClient().disconnect();
        }
    }


}
