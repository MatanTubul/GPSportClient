package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.matant.gpsportclient.R;

/**
 * Created by matant on 12/27/2015.
 */
public class ContactUsFragmentController extends Fragment implements View.OnClickListener {
    private EditText nameEdittext,subjectEdittext,messageEdittext;
    private Button submitButton;

    public ContactUsFragmentController(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.contact_us_fragment_controller, container, false);

        nameEdittext = (EditText) v.findViewById(R.id.editTextContactusName);
        subjectEdittext = (EditText) v.findViewById(R.id.editTextContactusSubject);
        messageEdittext = (EditText) v.findViewById(R.id.editTextContactusMessage);
        submitButton = (Button) v.findViewById(R.id.buttonContactusSubmit);
        submitButton.setOnClickListener(this);
        getActivity().setTitle("Contact Us");
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonContactusSubmit:
                if(validateInputs())
                {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"GPSport.braude@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, subjectEdittext.getText().toString());
                    i.putExtra(Intent.EXTRA_TEXT   , messageEdittext.getText().toString());
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }

    }
    private boolean validateInputs() {
        Boolean valid = true;
        if(subjectEdittext.getText().toString().equals(""))
        {
            subjectEdittext.setError("Subject can not be empty!");
            valid = false;
        }
        if(nameEdittext.getText().toString().equals("")){
            nameEdittext.setError("Name can not be empty!");
            valid = false;
        }
        return  valid;
    }
}
