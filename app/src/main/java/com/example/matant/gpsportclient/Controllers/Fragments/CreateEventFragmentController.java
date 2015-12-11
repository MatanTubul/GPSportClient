package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Activity;
import android.app.DialogFragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Fragment;
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

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.matant.gpsportclient.Controllers.Activities.SchedulePopUp;
import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.Controllers.Activities.InviteUsersActivity;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnCompleteListener;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.CreateInviteUsersRow;
import com.example.matant.gpsportclient.Utilities.CreateInvitedUsersAdapter;
import com.example.matant.gpsportclient.Utilities.DatePicker;
import com.example.matant.gpsportclient.Utilities.MyAdapter;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.example.matant.gpsportclient.Utilities.TimePicker;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CreateEventFragmentController extends Fragment implements View.OnClickListener,OnCompleteListener,AsyncResponse {

    private Button btnStartdate,btnstartTime,btnendTime,btninviteUsers,btnEndDate,btnSave;
    private EditText addressEditText,maxParticipantsEdittext,minAgeEditText;
    private CheckBox privateEventCbox,reccuringEventCbox;
    private Spinner sportSpinner,genderSpinner;
    private Calendar cal;
    private String current_time,current_date;
    private Boolean SET_TIME = false;
    DialogFragment tp = null;
    private DBcontroller dbController;
    private ProgressDialog progress= null;
    private int settime = 0;
    private static final int REQUEST_CODE_GET_USER_LIST = 1;

    private ListView listViewInvitedUsers;
    private List<CreateInviteUsersRow> invitedUsers = null;
    private CreateInvitedUsersAdapter invidedAdapter;
    private SessionManager sm;
    private AlertDialog.Builder alert;
    private AlertDialog alertdialog = null;
    private ScrollView sv;
    private String mode;
    private String event_id ="";
    private JSONObject sched_res = null;
    private String currMaxParticipants;


    public CreateEventFragmentController() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_create_event_fragment_controller, container, false);


        //initialize the widgets
        btnStartdate = (Button) v.findViewById(R.id.buttonFromDate);
        getActivity().setTitle("Create Event");
        btnstartTime = (Button) v.findViewById(R.id.buttonFromTime);
        btnEndDate = (Button) v.findViewById(R.id.buttonEndDate);
        btnendTime = (Button) v.findViewById(R.id.buttonEndTime);
        btninviteUsers = (Button) v.findViewById(R.id.buttonInviteUsers);
        btnSave = (Button) v.findViewById(R.id.ButtonSchedSave);
        sv = (ScrollView) v.findViewById(R.id.scrollView);


        cal = Calendar.getInstance();

        btnstartTime.setText(getCorrentTime());
        btnendTime.setText(getCorrentTime());

        btnStartdate.setText(getCurrentDate());
        btnEndDate.setText(getCurrentDate());

        sm = SessionManager.getInstance(getActivity());


        maxParticipantsEdittext = (EditText) v.findViewById(R.id.editTextMaxPaticipants);
        minAgeEditText = (EditText) v.findViewById(R.id.editTextMinAge);
        addressEditText = (EditText) v.findViewById(R.id.editTextLocation);

        privateEventCbox = (CheckBox) v.findViewById(R.id.checkBoxPrivateEvent);
        reccuringEventCbox = (CheckBox) v.findViewById(R.id.checkBoxRecurring);


        sportSpinner = (Spinner) v.findViewById(R.id.spinnerSports);
        genderSpinner = (Spinner) v.findViewById(R.id.spinnerGender);

        //gender spinner
        genderSpinner.setAdapter(new MyAdapter(getActivity(), R.layout.custom_spinner, getResources().getStringArray(R.array.eventgender)));
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });//gender spinner

        //Sport Spinner

        sportSpinner.setAdapter(new MyAdapter(getActivity(), R.layout.custom_spinner, getResources().getStringArray(R.array.kind_of_sport)));

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });//Sport Spinner

        btninviteUsers.setVisibility(v.GONE);


        //private event check box listener
        privateEventCbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (privateEventCbox.isChecked())
                    btninviteUsers.setVisibility(v.VISIBLE);
                else
                    btninviteUsers.setVisibility(v.GONE);

            }
        });//private event check box listener

        reccuringEventCbox.setOnClickListener(this);


        btnstartTime.setOnClickListener(this);
        btnendTime.setOnClickListener(this);
        btnStartdate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btninviteUsers.setOnClickListener(this);

        listViewInvitedUsers = (ListView) v.findViewById(R.id.listViewInvitedusers);


        listViewInvitedUsers.setItemsCanFocus(true);
        mode = Constants.MODE_CREATE;
        Bundle b = getArguments();
        if(b != null)
        {
             mode = b.getString(Constants.TAG_REQUEST);
             if(mode.equals(Constants.MODE_UPDATE)){
                 getActivity().setTitle("Edit Event");
                try {
                    JSONObject json = new JSONObject(b.getString("json"));
                    Log.d("event details", json.toString());
                    addressEditText.setText(json.getString("address"));
                    btnStartdate.setText(json.getString("event_date"));
                    btnEndDate.setText(json.getString("event_date"));
                    btnstartTime.setText(json.getString("start_time"));
                    btnendTime.setText(json.getString("end_time"));
                    String sched = json.getString("scheduled");
                    if (sched.equals("1")){
                        reccuringEventCbox.setChecked(true);
                    }
                    sportSpinner.setSelection(getIndexSpinnerByValue(sportSpinner, json.getString("kind_of_sport")));
                    genderSpinner.setSelection(getIndexSpinnerByValue(genderSpinner, json.getString("gender")));
                    currMaxParticipants = json.getString("max_participants");
                    maxParticipantsEdittext.setText(json.getString("max_participants"));
                    minAgeEditText.setText(json.getString("min_age"));
                    String event_mode = json.getString("private");
                    minAgeEditText.setEnabled(false);
                    genderSpinner.setEnabled(false);
                    privateEventCbox.setEnabled(false);
                    if(event_mode.equals("true")){
                        privateEventCbox.setChecked(true);
                        btninviteUsers.setVisibility(v.VISIBLE);
                    }
                    event_id = json.getString("event_id").toString();
                    btnSave.setText("Update Event");
                    Log.d("users in update", b.getString("users"));
                    JSONArray jsonarr = new JSONArray(b.getString("users"));
                    initAdapter(jsonarr.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        return v;
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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onClick(View v) {
        DialogFragment df = null;
        Bundle bundle = null;
        String string_date = btnStartdate.getText().toString();
        String dialog_type ="";
        switch(v.getId()) {
            case R.id.buttonFromTime:
            {
                {
                    bundle = new Bundle();
                    dialog_type = "Time";
                    bundle.putInt(dialog_type, 1);
                    bundle.putString("date",string_date);
                    SET_TIME =true;
                }

            }
                break;
            case R.id.buttonEndTime:
                {
                    {
                        bundle = new Bundle();
                        dialog_type = "Time";
                        bundle.putInt(dialog_type, 2);
                        bundle.putString("date",string_date);
                        SET_TIME =true;
                    }
                    break;
                }
            case R.id.buttonFromDate:
            {
                {
                    bundle = new Bundle();
                    dialog_type = "Date";
                    bundle.putInt(dialog_type, 1);
                    df = new DatePicker();
                }
                break;
            }
            case R.id.ButtonSchedSave:
                Log.d("send1","send1");
                if(validateFields())
                {
                    Log.d("send","send");
                    if(invitedUsers != null)
                    {

                        if(checkInvitedUsers(invitedUsers)){

                            sendDataToDBController();
                        }
                    }else{
                        sendDataToDBController();
                    }

                }


                break;
            case R.id.buttonInviteUsers: {
                Intent i = new Intent(getActivity(),InviteUsersActivity.class);
                startActivityForResult(i,REQUEST_CODE_GET_USER_LIST);
                break;
            }
            case R.id.checkBoxRecurring:
            {
                if (reccuringEventCbox.isChecked())
                {
                    Intent i = new Intent(getActivity(),SchedulePopUp.class);
                    startActivityForResult(i,Constants.REQUEST_CODE_SET_SCHEDULE);
                }
                break;
            }



        }
        if(df!=null && bundle!=null)
        {

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
    public void onComplete(String flag,String res) {
          settime = 0;
         switch(flag) {
             case "start_time": {
                 btnstartTime.setText(res);
                 if (settime == 0)
                     btnendTime.setText(res);
                 settime = 0;
                 break;
             }
             case "end_time": {
                 btnendTime.setText(res);
                 settime = 1;
                 break;
             }
             case "date":
                 btnStartdate.setText(res);
                 btnEndDate.setText(res);
                 break;
             case "incorrect_time": {
                 new AlertDialog.Builder(getActivity())
                         .setTitle("Select Time Error")
                         .setMessage(res)
                         .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 btnstartTime.setText(getCorrentTime());
                                 btnendTime.setText(getCorrentTime());
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
                                 btnStartdate.setText(getCurrentDate());
                                 btnEndDate.setText(getCurrentDate());
                             }
                         })
                         .setIcon(R.drawable.error_32)
                         .show();
                 break;
             }
         }

    }

    /**
     * return the current time as string
     * @return -current time
     */
    public String getCorrentTime()
    {
        String min = "";

        if(cal.get(Calendar.MINUTE)<10)
            min = "0"+String.valueOf(cal.get(Calendar.MINUTE));
        else
            min = String.valueOf(cal.get(Calendar.MINUTE));

        current_time = cal.get(Calendar.HOUR_OF_DAY)+":"+min;
        return current_time;
    }

    /**
     * return the current date
     * @return current date
     */
    public  String getCurrentDate()
    {

        current_date = cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
        return current_date;
    }

    /**
     * this function convert real address to geographical coordinates.
     * @param strAddress -real address
     * @return LatLng object which contain the coordinates
     */
    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getActivity());
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
            Log.d("Location Exception","error converting address");
            ex.printStackTrace();
        }

        return p1;
    }

    @Override
    public void handleResponse(String resStr) {
        progress.dismiss();

        if (resStr != null) {
            Log.d("create handleResponse", resStr);
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString("flag");
                switch (flg)
                {
                    case "success": {
                        Log.d("event created", "success to create event");
                        alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Event");
                        alert.setMessage("Event was created successfully");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Constants.reloadApp(getActivity(), MainScreen.class);
                                getActivity().finish();
                            }

                        });
                        alert.setIcon(R.drawable.ok_32);
                         alertdialog = alert.show();
                        break;
                    }
                    case "failed":
                    {
                        Log.d("Created failed","failed to create event");
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Create event failed!")
                                .setMessage(jsonObj.getString("msg"))
                                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        btnstartTime.setText(getCorrentTime());
                                        btnendTime.setText(getCorrentTime());
                                    }
                                })
                                .setIcon(R.drawable.error_32)
                                .show();
                        break;
                    }

                    case "select failed": {
                        Log.d("select query", "failed to find events");
                        break;
                    }
                    case "update_failed":
                    {
                        Log.d("update res failed",resStr);
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Update Failed!")
                                .setMessage(jsonObj.getString("msg"))
                                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(R.drawable.error_32)
                                .show();
                        break;
                    }
                    case "update_success":{
                        Log.d("event created", "success to create event");
                        alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Event");
                        alert.setMessage("Event was updated successfully");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Constants.reloadApp(getActivity(), MainScreen.class);
                                getActivity().finish();
                            }

                        });
                        alert.setIcon(R.drawable.ok_32);
                        alertdialog = alert.show();
                        Log.d("update res success", resStr);
                        break;
                    }
                }
            }catch (JSONException e){
                Log.d("json exception",e.getMessage());
            }
        }
    }

    @Override
    public void sendDataToDBController() {
        BasicNameValuePair mode_req;
        LatLng lonlat = getLocationFromAddress(addressEditText.getText().toString());
        if(lonlat == null)
        {
            Log.d("location is:","location not found");
            sv.scrollTo(0, 0);
            addressEditText.setError("Location was not found!");
            return;
        }
        Log.d("found location",lonlat.latitude+""+lonlat.longitude);
        BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST,"create_event");
        Log.d("event mode",mode);

        if(mode.equals(Constants.MODE_CREATE))
        {
            Log.d("event mode","create");
             mode_req = new BasicNameValuePair(Constants.TAG_MODE,Constants.MODE_CREATE);
        }
        else {
            Log.d("event mode","update");
             mode_req = new BasicNameValuePair(Constants.TAG_MODE, Constants.MODE_UPDATE);
        }


        BasicNameValuePair address = new BasicNameValuePair("address",addressEditText.getText().toString());
        BasicNameValuePair sport = new BasicNameValuePair("sport_type",sportSpinner.getSelectedItem().toString());
        Log.d("sport_type",sportSpinner.getSelectedItem().toString());

        BasicNameValuePair date = new BasicNameValuePair("date",btnStartdate.getText().toString());
        BasicNameValuePair startTime = new BasicNameValuePair("s_time",btnstartTime.getText().toString());
        BasicNameValuePair endTime = new BasicNameValuePair("e_time",btnendTime.getText().toString());
        BasicNameValuePair longtitude = new BasicNameValuePair(Constants.TAG_LONG,String.valueOf(lonlat.longitude));
        BasicNameValuePair latitude = new BasicNameValuePair(Constants.TAG_LAT,String.valueOf(lonlat.latitude));
        BasicNameValuePair event_type = new BasicNameValuePair("event_type",String.valueOf(privateEventCbox.isChecked()));
        BasicNameValuePair gender = new BasicNameValuePair(Constants.TAG_GEN,String.valueOf(genderSpinner.getSelectedItem().toString()));
        BasicNameValuePair min_age = new BasicNameValuePair("minAge",String.valueOf(minAgeEditText.getText()));
        BasicNameValuePair participants = new BasicNameValuePair("max_participants",maxParticipantsEdittext.getText().toString());
        BasicNameValuePair scheduled = new BasicNameValuePair("scheduled",String.valueOf(reccuringEventCbox.isChecked()));
        BasicNameValuePair mob_manager = new BasicNameValuePair("manager",sm.getUserDetails().get(Constants.TAG_USERID));
        BasicNameValuePair manager_name = new BasicNameValuePair("manager_name",sm.getUserDetails().get(Constants.TAG_NAME));


        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

        if(mode.equals(Constants.MODE_UPDATE)){
            BasicNameValuePair eventId = new BasicNameValuePair("event_id",event_id);
            nameValuePairList.add(eventId);
        }

        if(invitedUsers != null)
        {
            if(invitedUsers.size() > 0)
            {

                String[] users = new String[invitedUsers.size()];
                JSONArray invited = new JSONArray();

                for(int i=0 ; i < invitedUsers.size(); i++)
                {
                    if(mode.equals(Constants.MODE_CREATE))
                        users[i]= invitedUsers.get(i).getMobile();
                    else
                        users[i]= invitedUsers.get(i).getId();
                    invited.put(users[i]);
                }
                String json = invited.toString();
                Log.d("string array", Arrays.toString(users));
                BasicNameValuePair invitedusers = new BasicNameValuePair("invitedUsers",Arrays.toString(users));
                BasicNameValuePair jsonInvited = new BasicNameValuePair("jsoninvited",json);
                nameValuePairList.add(invitedusers);
                nameValuePairList.add(jsonInvited);
            }
        }
        if(sched_res != null && reccuringEventCbox.isChecked() == true){

            String repeatval ="";
            String duration ="";
            String tag = "";
            String val = "";
            BasicNameValuePair sched_val = null;
            try {
                 repeatval = sched_res.getString("repeat");
                 duration = sched_res.getString("duration");
                JSONArray jsonarr = new JSONArray(sched_res.getString("radio_group"));
                tag = jsonarr.getJSONObject(0).getString(Constants.TAG_REQUEST);
                sched_val = new BasicNameValuePair("value",jsonarr.getJSONObject(0).getString("val"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BasicNameValuePair sched_repeat = new BasicNameValuePair("repeat",repeatval);
            BasicNameValuePair sched_duration = new BasicNameValuePair("duration",duration);
            BasicNameValuePair sched_tag = new BasicNameValuePair("sched_tag",tag);
            nameValuePairList.add(sched_repeat);
            nameValuePairList.add(sched_duration);
            nameValuePairList.add(sched_tag);
            if(sched_val != null)
                nameValuePairList.add(sched_val);

        }

        nameValuePairList.add(manager_name);
        nameValuePairList.add(mob_manager);
        nameValuePairList.add(tagreq);
        nameValuePairList.add(mode_req);
        nameValuePairList.add(sport);
        nameValuePairList.add(date);
        nameValuePairList.add(address);
        nameValuePairList.add(startTime);
        nameValuePairList.add(endTime);
        nameValuePairList.add(min_age);
        nameValuePairList.add(longtitude);
        nameValuePairList.add(latitude);
        nameValuePairList.add(event_type);
        nameValuePairList.add(participants);
        nameValuePairList.add(scheduled);
        nameValuePairList.add(gender);
        dbController = new DBcontroller(getActivity().getApplicationContext(),this);
        dbController.execute(nameValuePairList);
    }

    @Override
    public void preProcess() {
        if(mode.equals(Constants.MODE_CREATE))
        {
            this.progress = ProgressDialog.show(getActivity(), "Create Event",
                    "Building Event...", true);
        }else{
            this.progress = ProgressDialog.show(getActivity(), "Update Event",
                    "Updating Event...", true);
        }

    }
    public boolean checkInvitedUsers(List<CreateInviteUsersRow> list){
        String age,gender;
        boolean valid = true;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int diff = 0;
        for(int i=0;i<list.size();i++){
            age = list.get(i).getAge();
            gender = list.get(i).getGender();
            if(!(gender.equals(genderSpinner.getSelectedItem().toString()))){
                list.get(i).setImgViewUserError(R.drawable.user_warning);
                invidedAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(),"One or more users does not fit gender type!",Toast.LENGTH_LONG).show();
                valid =  false;
            }
            diff = year - (Integer.valueOf(age));
            if( diff < (Integer.valueOf(minAgeEditText.getText().toString()))){
                list.get(i).setImgViewUserError(R.drawable.user_warning);
                invidedAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(),"One or more users does not fit Minimum age!",Toast.LENGTH_LONG).show();
                valid =  false;
            }
        }

        return valid;
    }



    /**
     * check the input of the user.
     * @return
     */
    public boolean validateFields()
    {
        Boolean valid = true;
        if(addressEditText.getText().toString().equals(""))
        {
            addressEditText.setError("Location field cannot be empty!");
            valid =  false;
        }
        if(maxParticipantsEdittext.getText().toString().equals(""))
        {
          maxParticipantsEdittext.setError("Please insert the max Participants in the event");
            valid = false;
        } if(minAgeEditText.getText().toString().equals("") == true || (Integer.valueOf(minAgeEditText.getText().toString()) < 14 || Integer.valueOf(minAgeEditText.getText().toString()) > 40 ))
        {
            minAgeEditText.setError("Minimal age should be at least 14!");
            valid = false;
        } if(btnendTime.getText().toString().equals(btnstartTime.getText().toString()))
        {
            onComplete("incorrect_time", "Please Provide end time of the event ");
            valid = false;
        }
        if(currMaxParticipants!=null){
            if((Integer.valueOf(currMaxParticipants)) > (Integer.valueOf(maxParticipantsEdittext.getText().toString()))){
                maxParticipantsEdittext.setError("Maximum number of participants minor then the original.");
                valid = false;
            }
        }
        if(!(minAgeEditText.getText().toString().equals("")));
        {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int diff =  year - (Integer.valueOf(sm.getUserDetails().get(Constants.TAG_AGE)));
            if((Integer.valueOf(minAgeEditText.getText().toString()))> diff ){
                minAgeEditText.setError("Your age is smaller than the Minimal!");
                valid = false;
            }

        }
        if(!(maxParticipantsEdittext.getText().toString().equals("")))
        {
            if((Integer.valueOf(maxParticipantsEdittext.getText().toString())) < 1 || (Integer.valueOf(maxParticipantsEdittext.getText().toString())) > 30){
                maxParticipantsEdittext.setError("Number of participants out of range!");
            }
        }



        return valid;
    }
    public void onStop(){
        super.onStop();
        if(alert != null){
            alertdialog.dismiss();
        }
    }

    /**
     * connect with the InviteUserActivity and getting the result from her.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("inside OnActivityResult", "result");

        if(REQUEST_CODE_GET_USER_LIST == requestCode){

            if(Activity.RESULT_OK == resultCode){
                if(mode.equals(Constants.MODE_CREATE)){
                    invitedUsers = new ArrayList<CreateInviteUsersRow>();
                }
                JSONArray res = null;
                try {
                    res = new JSONArray(data.getStringExtra("userList"));
                    for(int i=0 ;i< res.length(); i++){
                        String name = res.getJSONObject(i).getString("name");
                        String mobile = res.getJSONObject(i).getString("mobile");
                        String id = res.getJSONObject(i).getString("id");
                        String gen = res.getJSONObject(i).getString("gender");
                        String age = res.getJSONObject(i).getString("age");
                        CreateInviteUsersRow invitedUserRow = new CreateInviteUsersRow(name,mobile,R.drawable.remove_user_50,id,gen,age);
                        invitedUsers.add(invitedUserRow);
                    }
                    invidedAdapter = new CreateInvitedUsersAdapter(getActivity(),R.layout.create_users_invited_item,invitedUsers);
                    listViewInvitedUsers.setAdapter(invidedAdapter);
                    invidedAdapter.setAdapterListview(listViewInvitedUsers);
                    invidedAdapter.setListViewHeightBasedOnChildren();
                    listViewInvitedUsers = invidedAdapter.getAdapterListview();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.d("Users are:",res.toString());

            }
            if(resultCode == Activity.RESULT_CANCELED){
                Log.d("Acttivity canceled","canceled");

            }
        }
        if(Constants.REQUEST_CODE_SET_SCHEDULE == requestCode){
            if(Activity.RESULT_OK == resultCode){
                try {
                     sched_res = new JSONObject(data.getStringExtra("sched_prop"));
                    Log.d("sched data",sched_res.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else{
                    reccuringEventCbox.setChecked(false);
            }//request canceled
        }

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
                        //Toast.makeText(getActivity(), "Please navigate via the menu", Toast.LENGTH_SHORT).show();
                        Constants.reloadApp(getActivity(), MainScreen.class);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    /**
     * create and initialise the ListView of all the users that join to the event and
     * show that in a ListView under Edit mode
     * @param params
     */
    public void  initAdapter(String params){
        JSONArray res = null;
        try {
            res = new JSONArray(params);
            Log.d("res jsonarray",res.toString());
            invitedUsers = new ArrayList<CreateInviteUsersRow>();
            for(int i=0 ;i< res.length(); i++){
                Log.d("iteration",String.valueOf(i));
                String name = res.getJSONObject(i).getString("fname");
                String mobile = res.getJSONObject(i).getString("mobile");
                String id = res.getJSONObject(i).getString("id");
                String gen = res.getJSONObject(i).getString("gender");
                String age = res.getJSONObject(i).getString("age");
                CreateInviteUsersRow invitedUserRow = new CreateInviteUsersRow(name,mobile,R.drawable.remove_user_50,id,gen,age);
                invitedUsers.add(invitedUserRow);
            }
            invidedAdapter = new CreateInvitedUsersAdapter(getActivity(),R.layout.create_users_invited_item,invitedUsers);
            listViewInvitedUsers.setAdapter(invidedAdapter);

            invidedAdapter.setAdapterListview(listViewInvitedUsers);
            invidedAdapter.setListViewHeightBasedOnChildren();
            listViewInvitedUsers = invidedAdapter.getAdapterListview();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
