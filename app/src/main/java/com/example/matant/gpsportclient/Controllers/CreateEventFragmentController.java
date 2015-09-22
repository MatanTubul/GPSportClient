package com.example.matant.gpsportclient.Controllers;

import android.app.Activity;
import android.app.DialogFragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.Spinner;
import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.OnCompleteListener;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.DatePicker;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.Utilities.MyAdapter;
import com.example.matant.gpsportclient.Utilities.TimePicker;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;


public class CreateEventFragmentController extends Fragment implements View.OnClickListener,OnCompleteListener,AsyncResponse {

    private Button btnStartdate,btnstartTime,btnendTime,btninviteUsers,btnEndDate,btnSave,btnCancel;
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
        btnCancel = (Button) v.findViewById(R.id.ButtonCancel);

        cal = Calendar.getInstance();


        btnstartTime.setText(getCorrentTime());
        btnendTime.setText(getCorrentTime());

        btnStartdate.setText(getCurrentDate());
        btnEndDate.setText(getCurrentDate());


        maxParticipantsEdittext = (EditText)v.findViewById(R.id.editTextMaxPaticipants);
        minAgeEditText = (EditText)v.findViewById(R.id.editTextMinAge);
        addressEditText = (EditText)v.findViewById(R.id.editTextLocation);

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
                if(privateEventCbox.isChecked())
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
              /*  Log.d("SavePressed","press on button save");
                LatLng lonlat = getLocationFromAddress(addressEditText.getText().toString());
                Log.d("Cordinates", "latitude = " + lonlat.latitude + "longtitude=" + lonlat.longitude);*/
                if(validateFields())
                    sendDataToDBController();

                break;



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
     * return the corrent time as string
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
                    case "success":
                        Log.d("event created","success to create event");
                        break;
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

        BasicNameValuePair participants = new BasicNameValuePair("max_participants",maxParticipantsEdittext.getText().toString());
        BasicNameValuePair scheduled = new BasicNameValuePair("scheduled",String.valueOf(reccuringEventCbox.isChecked()));

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(sport);
        nameValuePairList.add(date);
        nameValuePairList.add(startTime);
        nameValuePairList.add(endTime);
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

    public boolean validateFields()
    {
        Boolean valid = true;
       /* ErrorHandler eh = new ErrorHandler();
        ArrayList<EditText> et = new ArrayList<EditText>() ;
        et.add(addressEditText);
        et.add(maxParticipantsEdittext);
        et.add(minAgeEditText);
        valid = eh.fieldIsEmpty(et,"Field cannot be empty");*/
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
            minAgeEditText.setError("Please insert minimal age of participant");
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
}
