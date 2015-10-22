package com.example.matant.gpsportclient.Controllers;

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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.OnCompleteListener;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.CreateInviteUsersRow;
import com.example.matant.gpsportclient.Utilities.CreateInvitedUsersAdapter;
import com.example.matant.gpsportclient.Utilities.DatePicker;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.Utilities.MyAdapter;
import com.example.matant.gpsportclient.Utilities.TimePicker;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import java.util.HashMap;
import java.util.List;


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


    public CreateEventFragmentController() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_create_event_fragment_controller, container, false);


        //initialize the widgets
        btnStartdate = (Button) v.findViewById(R.id.buttonFromDate);
        btnstartTime = (Button) v.findViewById(R.id.buttonFromTime);
        btnEndDate = (Button) v.findViewById(R.id.buttonEndDate);
        btnendTime = (Button) v.findViewById(R.id.buttonEndTime);
        btninviteUsers = (Button) v.findViewById(R.id.buttonInviteUsers);
        btnSave = (Button) v.findViewById(R.id.ButtonSave);


        cal = Calendar.getInstance();


        btnstartTime.setText(getCorrentTime());
        btnendTime.setText(getCorrentTime());

        btnStartdate.setText(getCurrentDate());
        btnEndDate.setText(getCurrentDate());


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


        btnstartTime.setOnClickListener(this);
        btnendTime.setOnClickListener(this);
        btnStartdate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btninviteUsers.setOnClickListener(this);

        listViewInvitedUsers = (ListView) v.findViewById(R.id.listViewInvitedusers);


        listViewInvitedUsers.setItemsCanFocus(true);

        return v;
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
        String dialog_type ="";
        switch(v.getId()) {
            case R.id.buttonFromTime:
            {
                {
                    bundle = new Bundle();
                    dialog_type = "Time";
                    bundle.putInt(dialog_type, 1);
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
            case R.id.ButtonSave:
                if(validateFields())
                    sendDataToDBController();

                break;
            case R.id.buttonInviteUsers: {
                Intent i = new Intent(getActivity(),InviteUsersActivity.class);
                startActivityForResult(i,REQUEST_CODE_GET_USER_LIST);
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
                         .setIconAttribute(android.R.attr.alertDialogIcon)
                         .show();
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
        Log.d("handleResponse", resStr);
        if (resStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString("flag");
                switch (flg)
                {
                    case "success": {
                        Log.d("event created", "success to create event");
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Event")
                                .setMessage("Event was created successfully")
                                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(getActivity(), MainScreen.class);
                                        startActivity(i);
                                        getActivity().finish();
                                    }
                                })
                                .setIconAttribute(android.R.attr.alertDialogIcon)
                                .show();
                        break;

                    }
                    case "failed":
                        Log.d("Created failed","failed to create event");
                    {
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
                                .setIconAttribute(android.R.attr.alertDialogIcon)
                                .show();
                        break;
                    }

                    case "select failed":
                        Log.d("select query","failed to find events");
                }
            }catch (JSONException e){
                Log.d("json exception",e.getMessage());
            }


        }

    }

    /**
     * send request to server
     */

    @Override
    public void sendDataToDBController() {

        LatLng lonlat = getLocationFromAddress(addressEditText.getText().toString());
        if(lonlat == null)
        {
            Log.d("location is:","location not found");
            addressEditText.setError("Location was not found!");
            return;
        }
        Log.d("found location",lonlat.latitude+""+lonlat.longitude);
        BasicNameValuePair tagreq = new BasicNameValuePair("tag","create_event");
        BasicNameValuePair sport = new BasicNameValuePair("sport_type",sportSpinner.getSelectedItem().toString());
        Log.d("sport_type",sportSpinner.getSelectedItem().toString());
        BasicNameValuePair date = new BasicNameValuePair("date",btnStartdate.getText().toString());
        BasicNameValuePair startTime = new BasicNameValuePair("s_time",btnstartTime.getText().toString());
        BasicNameValuePair endTime = new BasicNameValuePair("e_time",btnendTime.getText().toString());
        BasicNameValuePair longtitude = new BasicNameValuePair("lon",String.valueOf(lonlat.longitude));
        BasicNameValuePair latitude = new BasicNameValuePair("lat",String.valueOf(lonlat.latitude));
        BasicNameValuePair event_type = new BasicNameValuePair("event_type",String.valueOf(privateEventCbox.isChecked()));
        BasicNameValuePair gender = new BasicNameValuePair("gender",String.valueOf(genderSpinner.getSelectedItem().toString()));
        BasicNameValuePair min_age = new BasicNameValuePair("minAge",String.valueOf(minAgeEditText.getText()));
        BasicNameValuePair participants = new BasicNameValuePair("max_participants",maxParticipantsEdittext.getText().toString());
        BasicNameValuePair scheduled = new BasicNameValuePair("scheduled",String.valueOf(reccuringEventCbox.isChecked()));

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

        if(invitedUsers != null)
        {
            if(invitedUsers.size() > 0)
            {

                String[] users = new String[invitedUsers.size()];
                JSONArray invited = new JSONArray();
                for(int i=0 ; i < invitedUsers.size(); i++)
                {
                    users[i]= invitedUsers.get(i).getMobile();
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



        nameValuePairList.add(tagreq);
        nameValuePairList.add(sport);
        nameValuePairList.add(date);
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
        this.progress = ProgressDialog.show(getActivity(), "Create Event",
                "Building Event...", true);
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
            Log.d("max edittext", "max edit text is empty");
            valid =  false;
        }
        if(maxParticipantsEdittext.getText().toString().equals(""))
        {
          maxParticipantsEdittext.setError("Please insert the max Participants in the event");
            valid = false;
        } if(minAgeEditText.getText().toString().equals("") == true || Integer.valueOf(minAgeEditText.getText().toString()) < 14)
        {
            minAgeEditText.setError("Minimal age should be at least 14!");
            Log.d("min edittext", "min edit text is empty");
            valid = false;
        } if(btnendTime.getText().toString().equals(btnstartTime.getText().toString()))
        {
            onComplete("incorrect_time", "Please Provide end time of the event ");
            Log.d("equal time", "time is equal");
            valid = false;
        }
        return valid;
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
        Log.d("inside OnActivityResult","result");

        if(REQUEST_CODE_GET_USER_LIST == requestCode){

            if(Activity.RESULT_OK == resultCode){
                JSONArray res = null;
                try {
                    res = new JSONArray(data.getStringExtra("userList"));
                    invitedUsers = new ArrayList<CreateInviteUsersRow>();
                    for(int i=0 ;i< res.length(); i++){
                        String name = res.getJSONObject(i).getString("name");
                        String mobile = res.getJSONObject(i).getString("mobile");


                        CreateInviteUsersRow invitedUserRow = new CreateInviteUsersRow(name,mobile,R.drawable.remove_user_50);
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

    }
}
