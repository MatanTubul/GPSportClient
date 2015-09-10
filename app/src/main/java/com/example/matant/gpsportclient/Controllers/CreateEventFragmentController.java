package com.example.matant.gpsportclient.Controllers;

import android.app.Activity;
import android.app.DialogFragment;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


import com.example.matant.gpsportclient.OnCompleteListener;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.DatePicker;
import com.example.matant.gpsportclient.Utilities.MyAdapter;
import com.example.matant.gpsportclient.Utilities.TimePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class CreateEventFragmentController extends Fragment implements View.OnClickListener,OnCompleteListener {

    private Button btnStartdate,btnstartTime,btnendTime,btninviteUsers,btnEndDate,btnSave,btnCancel;
    private EditText addressEditText,maxParticipantsEdittext,minAgeEditText;
    private CheckBox privateEventCbox,reccuringEventCbox,specificAddressCbox;
    private Spinner sportSpinner,genderSpinner,radiusSpinner;
    private Calendar cal;
    private String current_time,current_date;
    private Boolean SET_TIME = false;
    DialogFragment tp = null;



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
        specificAddressCbox = (CheckBox) v.findViewById(R.id.checkBoxSpecifcAddress);

        sportSpinner = (Spinner) v.findViewById(R.id.spinnerSports);
        genderSpinner = (Spinner) v.findViewById(R.id.spinnerGender);
        radiusSpinner = (Spinner) v.findViewById(R.id.spinnerRadius);



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

        sportSpinner.setAdapter(new MyAdapter(getActivity(),R.layout.custom_spinner,getResources().getStringArray(R.array.kind_of_sport)));

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Sport Spinner

        //Radius Spinner
        radiusSpinner.setAdapter(new MyAdapter(getActivity(),R.layout.custom_spinner,getResources().getStringArray(R.array.radius_range)));

        radiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Radius Spinner



        addressEditText.setVisibility(v.GONE);
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

        //create event from specific location check box listener
        specificAddressCbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(specificAddressCbox.isChecked())
                {
                    addressEditText.setVisibility(v.VISIBLE);

                }
                else
                    addressEditText.setVisibility(v.GONE);
            }
        });//create event from specific location check box listener

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
                Log.d("SavePressed","press on button save");
                LatLng lonlat = getLocationFromAddress(addressEditText.getText().toString());
                Log.d("Cordinates", "latitude = " + lonlat.latitude + "longtitude=" + lonlat.longitude);
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

         switch(flag) {
             case "start_time":
                 btnstartTime.setText(res);
                 break;
             case "end_time":
                 btnendTime.setText(res);
                 break;
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
    public  String getCurrentDate()
    {

        current_date = cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
        return current_date;
    }
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

        } catch (Exception ex) {
            Log.d("Location Exception","error converting address");
            ex.printStackTrace();
        }

        return p1;
    }

}
