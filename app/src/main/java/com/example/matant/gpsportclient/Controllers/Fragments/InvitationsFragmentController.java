package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.DataClasses.SportsHash;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Adapters.InvitationsArrayAdapter;
import com.example.matant.gpsportclient.DataClasses.InvitationsRowModel;
import com.example.matant.gpsportclient.Utilities.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment that provide the user to manage all is events invitations and changing his decision any time
 * the user can approve/reject participation in the event via this fragment.
 * Created by matant on 12/10/2015.
 */
public class InvitationsFragmentController extends Fragment implements AsyncResponse {
    private ListView invitiationsList;
    private SessionManager sm;
    private DBcontroller dbController;
    private ProgressDialog progress;
    List<InvitationsRowModel> rowInvitations;
    private InvitationsArrayAdapter invitationsArrayAdapter;
    private HashMap sh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_invitations_fragment_controller, container, false);
        getActivity().setTitle("Invitations");
        invitiationsList = (ListView) v.findViewById(R.id.listViewInvitationsList);
        sm = SessionManager.getInstance(getActivity());
        sh = SportsHash.getSportsHash();
        sendDataToDBController();
        return v;

    }
    @Override
    public void handleResponse(String resStr) {
        progress.dismiss();

        Log.d("get invitations handleResponse", resStr);
        if (resStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);
                switch (flg) {
                    case Constants.TAG_REQUEST_SUCCEED: {

                        JSONArray jsonarr = jsonObj.getJSONArray("events");
                        Log.d("creating list", jsonarr.toString());
                        Log.d("array", jsonarr.toString());
                        String title, date, Loc, participants, event_time, event_id;

                        rowInvitations = new ArrayList<InvitationsRowModel>();
                        int sportType = -1;
                        for (int i = 0; i < jsonarr.length(); i++) {
                            Log.d("iteration",String.valueOf(i));
                            JSONObject eventObj = jsonarr.getJSONObject(i);
                            title = jsonarr.getJSONObject(i).getString("kind_of_sport");
                            date = jsonarr.getJSONObject(i).getString("event_date");
                            Loc = jsonarr.getJSONObject(i).getString("address");
                            event_time = jsonarr.getJSONObject(i).getString("formatted_start_time") + " " + "-" + " " + jsonarr.getJSONObject(i).getString("formatted_end_time");
                            event_id = jsonarr.getJSONObject(i).getString("event_id");
                            eventObj.put("start_time", jsonarr.getJSONObject(i).getString("formatted_start_time"));
                            eventObj.put("end_time", jsonarr.getJSONObject(i).getString("formatted_end_time"));
                            participants = jsonarr.getJSONObject(i).getString("current_participants");

                            SportsHash.Sport sport=(SportsHash.Sport) sh.get(title);
                            sportType = sport.getSportMapMarkerId();

                            Log.d("my parameters:",Loc+","+event_time+","+date+","+participants+","+event_id+","+sportType);
                            InvitationsRowModel rowInvite = new InvitationsRowModel(Loc,event_time,date,participants,event_id,sportType,eventObj);
                            rowInvitations.add(rowInvite);

                        }
                        Log.d("invite array adapter",rowInvitations.toString());
                        invitationsArrayAdapter = new InvitationsArrayAdapter(getActivity(),R.layout.invatations_list_view_row,rowInvitations);
                        invitiationsList.setAdapter(invitationsArrayAdapter);
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendDataToDBController() {
        BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "retrieve_invitations");
        BasicNameValuePair user_id = new BasicNameValuePair(Constants.TAG_USERID,sm.getUserDetails().get(Constants.TAG_USERID) );

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(user_id);
        dbController = new DBcontroller(getActivity(),this);
        dbController.execute(nameValuePairList);
    }

    @Override
    public void preProcess() {
        this.progress = ProgressDialog.show(getActivity(), "Retrieve Invitations",
                "Loading...", true);
    }

    /**
     * method which handling the requests for  back button in the  device
     * @param //savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Fragment fragment = null;
                        fragment = new GoogleMapFragmentController();
                        if (fragment != null) {
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
