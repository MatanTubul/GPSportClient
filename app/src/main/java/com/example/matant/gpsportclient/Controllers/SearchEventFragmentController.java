package com.example.matant.gpsportclient.Controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.R;


public class SearchEventFragmentController extends Fragment implements AsyncResponse {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_event_fragment_controller, container, false);
        return rootView;
    }


    @Override
    public void handleResponse(String resStr) {

    }

    @Override
    public void sendDataToDBController() {

    }

    @Override
    public void preProcess() {

    }
}
