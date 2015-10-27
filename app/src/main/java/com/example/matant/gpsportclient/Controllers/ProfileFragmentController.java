package com.example.matant.gpsportclient.Controllers;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.example.matant.gpsportclient.Utilities.SessionManager;

public class ProfileFragmentController extends Fragment {

    private static final int REQUEST_CODE = 1;
    private SessionManager sm;
    
    public ProfileFragmentController() {}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(getActivity());
        Intent i = new Intent(getActivity(), SignUp.class);
        i.putExtra("USER_DETAILS", sm.getUserDetails());
        startActivityForResult(i, REQUEST_CODE);
    }


}
