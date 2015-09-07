package com.example.matant.gpsportclient.Controllers;

import android.app.Activity;
import android.app.DialogFragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.TimePicker;


public class CreateEventFragmentController extends Fragment implements View.OnClickListener,TimePicker.OnCompleteListener {

    private Button btnStartdate,btnstartTime,btnendTime,btninviteUsers,btnEndDate,btnSave,btnCancel;
    private EditText addressEditText,maxParticipantsEdittext,minAgeEditText;
    private CheckBox privateEventCbox,reccuringEventCbox,specificAddressCbox;
    private Spinner sportSpinner,genderSpinner,radiusSpinner;


    public CreateEventFragmentController() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_event_fragment_controller, container, false);


        //initialize the widgets
        btnStartdate = (Button) v.findViewById(R.id.buttonFromDate);
        btnstartTime = (Button) v.findViewById(R.id.buttonFromTime);
        btnEndDate = (Button) v.findViewById(R.id.buttonEndDate);
        btnendTime = (Button) v.findViewById(R.id.buttonEndTime);
        btninviteUsers = (Button) v.findViewById(R.id.buttonInviteUsers);
        btnSave = (Button) v.findViewById(R.id.ButtonSave);
        btnCancel = (Button) v.findViewById(R.id.ButtonCancel);





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
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(v.getContext(), R.array.eventgender, android.R.layout.simple_spinner_item);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderSpinner.setAdapter(genderAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });//gender spinner



        //Sport Spinner
        ArrayAdapter<CharSequence> sportAdapter = ArrayAdapter.createFromResource(v.getContext(), R.array.kind_of_sport, android.R.layout.simple_spinner_item);

        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sportSpinner.setAdapter(sportAdapter);

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
        ArrayAdapter<CharSequence> radiusAdapter = ArrayAdapter.createFromResource(v.getContext(), R.array.radius_range, android.R.layout.simple_spinner_item);

        radiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        radiusSpinner.setAdapter(radiusAdapter);

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
                    addressEditText.setVisibility(v.VISIBLE);
                else
                    addressEditText.setVisibility(v.GONE);
            }
        });//create event from specific location check box listener

        btnstartTime.setOnClickListener(this);
        btnendTime.setOnClickListener(this);
        btnStartdate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);

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
        switch(v.getId()) {
            case R.id.buttonFromTime:
            {
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt("Time", 1);
                    DialogFragment TP = new TimePicker();
                    TP.setArguments(bundle);
                    TP.setTargetFragment(this,0);
                    TP.show(getFragmentManager(),"TimePicker");
                }
            }
                break;
            case R.id.buttonEndTime:
                {
                    {
                        Bundle bundle = new Bundle();
                        bundle.putInt("Time",2);
                        DialogFragment TP = new TimePicker();
                        TP.setArguments(bundle);
                        TP.setTargetFragment(this,0);
                        TP.show(getFragmentManager(),"TimePicker");
                    }
                    break;
                }



        }
    }

    @Override
    public void onComplete(String flag,String time) {

         switch(flag)
        {
            case "start_time":
                btnstartTime.setText(time);
                break;
            case "end_time":
                    btnendTime.setText(time);
                break;
        }

    }
}
