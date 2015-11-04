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

    private static final int REQUEST_CODE = 1;
    private SessionManager sm;
    private static ProfileFragmentController profileFragmentControllerInstance = null;

    public ProfileFragmentController() {
        // Required empty public constructor
    }

    public static ProfileFragmentController getInstance(){
        if(profileFragmentControllerInstance == null)
        {
            profileFragmentControllerInstance = new ProfileFragmentController();
        }
        return profileFragmentControllerInstance;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
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
        super.onCreate(savedInstanceState); TODO:
        sm = SessionManager.getInstance(getActivity());
        Intent updateProfile = new Intent(getActivity(), SignUp.class);
        updateProfile.putExtra("USER_DETAILS", sm.getUserDetails());
        startActivityForResult(updateProfile, REQUEST_CODE);
    }




}
