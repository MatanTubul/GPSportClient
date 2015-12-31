package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.DialogFragment;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnCompleteListener;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnLocationChangedListener;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.DateAndTimeFunctions;
import com.example.matant.gpsportclient.Utilities.DatePicker;
import com.example.matant.gpsportclient.Utilities.LocationTool;
import com.example.matant.gpsportclient.Adapters.MyAdapter;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.example.matant.gpsportclient.Utilities.TimePicker;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        getActivity().setTitle("Search Events");
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
        pos = 1;
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
                Log.d("radio pos is",String.valueOf(pos));
                switch (pos) {
                    case 1: {
                        streetAddress.setEnabled(true);
                        streetAddress.setText("");
                        if (myLocManager.getmGoogleApiClient().isConnected()) {
                            stopLocationUpdates();
                            myLocManager.getmGoogleApiClient().disconnect();
                        }
                        break;
                    }
                    case 2: {
                        streetAddress.setEnabled(false);
                        if (myLocManager.getmGoogleApiClient() != null) {
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
        cbPrivate.setChecked(true);

        Bundle b = getArguments();
        if(b != null){
            JSONObject jsobj = null;
            Log.d("get bundle params",b.toString());
            try {
                jsobj = new JSONObject(b.getString(Constants.TAG_SEARCH_PARAMS_FROM_RECENT));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Log.d("init all fields",jsobj.toString());
                streetAddress.setText(jsobj.getString(Constants.TAG_REAL_ADDRESS));
                minimumAge.setText(jsobj.getString(Constants.TAG_MIN_AGE));
                radius.setText(jsobj.getString(Constants.TAG_RADIUS));
                spinnerKindOfSport.setSelection(getIndexSpinnerByValue(spinnerKindOfSport, jsobj.getString(Constants.TAG_KIND_OF_SPORT)));
                spinnerGender.setSelection(getIndexSpinnerByValue(spinnerGender, jsobj.getString(Constants.TAG_GEN)));
                boolean tmp = Boolean.valueOf(jsobj.getString(Constants.TAG_PRIVATE));
                if(tmp){
                    cbPrivate.setChecked(true);
                }
                tmp = Boolean.valueOf(jsobj.getString(Constants.TAG_PUBLIC));
                if(tmp)
                {
                    cbPublic.setChecked(true);
                }
                lon = Double.valueOf(jsobj.getString(Constants.TAG_LONG));
                lat = Double.valueOf(jsobj.getString(Constants.TAG_LAT));
                Date currDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date date1 = sdf.parse(jsobj.getString(Constants.TAG_START_DATE).toString());
                    Date date2 = sdf.parse(jsobj.getString(Constants.TAG_END_DATE).toString());
                    if (date1.after(currDate)){
                        dateFrom.setText(jsobj.getString(Constants.TAG_START_DATE).toString());
                    }
                    if(date2.after(currDate)){
                        dateTo.setText(jsobj.getString(Constants.TAG_END_DATE).toString());
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return rootView;
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
                        Bundle bun = new Bundle();
                        bun.putString(Constants.TAG_REQUEST,Constants.MODE_SEARCH_REQ);
                        bun.putString("json",jsonObj.toString());

                        bun.putString(Constants.TAG_LAT,String.valueOf(lat));
                        bun.putString(Constants.TAG_LONG,String.valueOf(lon));

                        Log.d("message from search",String.valueOf(lat)+ " " + String.valueOf(lat));

                        Fragment fragment = new GoogleMapFragmentController();
                        fragment.setArguments(bun);
                        FragmentManager fragmentManager =  getActivity().getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
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

    @Override
    public void sendDataToDBController() {
        JSONObject jobjRecentSearch = new JSONObject();

        if(pos == 1){
            LatLng loc = myLocManager.getLocationFromAddress(streetAddress.getText().toString());
            lat = loc.latitude;
            lon = loc.longitude;
            if(loc == null){
                streetAddress.setError("Location was not found");
                return;
            }
        }
        BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST,"search_events");
        BasicNameValuePair search = new BasicNameValuePair(Constants.TAG_SEARCH,"search_by_frag");
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
        nameValuePairList.add(search);
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
        Log.d("my nameValuePairList", nameValuePairList.toString());

        try {
            if(sm.getRecentSearchesStatus() == null)
                sm.StoreUserSession("not_empty",Constants.TAG_HM_STATUS);
            for(int i= 0;i < nameValuePairList.size();i++){
                Log.d("loop index is",String.valueOf(i));
                jobjRecentSearch.put(nameValuePairList.get(i).getName(),nameValuePairList.get(i).getValue());
            }
            jobjRecentSearch.put("real_address",streetAddress.getText().toString());
            Log.d("this is the real address",streetAddress.getText().toString());
            Log.d("json 1", jobjRecentSearch.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sm.getSearchIndex() >= 0)
        {
            Log.d("search index is", String.valueOf(sm.getSearchIndex()));
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
        if ((streetAddress.getText().toString().equals("")) && (pos == 1 || pos == 2)){
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
        if(dateTo.getText().toString().equals(dateFrom.getText())){
            if(timeFrom.getText().toString().equals(timeTo.getText().toString())){
                valid = false;
                Toast.makeText(getActivity(),"Time is not valid",Toast.LENGTH_LONG).show();
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
        String equ_type;
        String dialog_type ="";
        Log.d("dates",string_date + " "+ end_date);
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
            df.show(getFragmentManager(), dialog_type);
        }
        if(SET_TIME)
        {
            SET_TIME = false;
            if(tp == null)
                tp = new TimePicker();
            tp.setArguments(bundle);
            tp.setTargetFragment(this,0);
            tp.show(getFragmentManager(), dialog_type);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if( pos == 2 ){
            Log.d("return from settings", "updating");
            myLocManager.checkPlayServices();
            if(!(myLocManager.getmGoogleApiClient().isConnected()))
            {
                myLocManager.buildGoogleApiClientAndCreateLocationRequest();
                myLocManager.getmGoogleApiClient().connect();
            }
            if (myLocManager.getmGoogleApiClient().isConnected() && !myLocManager.ismRequestingLocationUpdates()) {
                Log.d("my location", "getmGoogleApiClient() true");
                startLocationUpdates();
            }
        }
    }

    /**
     * function which handling the all callbacks from DatePicker and TimePicker classes
     * @param flag
     * @param res
     */
    @Override
    public void onComplete(String flag, String res) {
        settime = 0;
        switch(flag) {
            case "start_time": {
                timeFrom.setText(res);
                Log.d("TimeFrom", res);
                if (settime == 0)
                    timeTo.setText(res);
                settime = 0;
                break;
            }
            case "end_time": {
                timeTo.setText(res);
                Log.d("TimeTo", res);
                settime = 1;
                break;
            }
            case "start_date":
                dateFrom.setText(res);
                dateTo.setText(res);
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

    /**
     * function which updating the location of the Device and represent
     * it as a real address on the UI.
     */
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
        if(pos == 2){
            myLocManager.checkPlayServices();
            if (myLocManager.getmGoogleApiClient().isConnected() && !myLocManager.ismRequestingLocationUpdates()) {
                startLocationUpdates();
            }
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        if(pos ==  2){
            if (myLocManager.getmGoogleApiClient().isConnected()) {
                myLocManager.getmGoogleApiClient().disconnect();
            }
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
    private int getIndexSpinnerByValue(Spinner sp, String val) {
        int index = 0;
        for(int i=0; i < sp.getCount();i++) {
            if (sp.getItemAtPosition(i).toString().equals(val)) {
                index = i;
                return  index;
            }
        }
        return  index;
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
                        String frag_name = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName();

                        Fragment fragment = null;
                        if(frag_name != null){
                            fragment = new RecentSearchesFragmentController();
                        }else{
                            fragment = new GoogleMapFragmentController();
                        }

                        if (fragment != null) {
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
