package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.OnCompleteListener;
import com.example.matant.gpsportclient.R;

/**
 * Created by matant on 11/4/2015.
 */
public class ManageEventFragmentController extends Fragment implements View.OnClickListener,AsyncResponse {
    private ListView listViewMngEvents;

    public ManageEventFragmentController ()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_manage_event_fragment_controller, container, false);

        listViewMngEvents = (ListView) v.findViewById(R.id.listViewManageEvent);
        listViewMngEvents.setItemsCanFocus(true);

        return v;
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

    @Override
    public void onClick(View v) {

    }
}
