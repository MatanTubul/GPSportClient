package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.matant.gpsportclient.Controllers.Activities.SignUp;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.Utilities.SessionManager;

public class ProfileFragmentController extends Fragment {

    private SessionManager sm;
    Intent updateProfile = null;

    public ProfileFragmentController() {
        // Required empty public constructor
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE) {
            Log.d("REQUEST_CODE","This fragment expected the result" );
            // Make sure the request was successful
            //if (resultCode == Activity.RESULT_OK) {
                Log.d("RESULT_OK","This fragment got what he has expected" );
                Constants.reloadApp(getActivity(), MainScreen.class);

            //}
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {//initialize once
            sm = SessionManager.getInstance(getActivity());
            updateProfile = new Intent(getActivity(), SignUp.class);
        }
        // Restore value of members from saved state
        updateProfile.putExtra("USER_DETAILS", sm.getUserDetails());
        startActivityForResult(updateProfile, Constants.REQUEST_CODE);

    }

    @Override //saving activity last state
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }


}
