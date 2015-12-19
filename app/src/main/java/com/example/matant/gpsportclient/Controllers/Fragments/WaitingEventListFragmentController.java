package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.example.matant.gpsportclient.Utilities.WaitingEventUserAdapter;
import com.example.matant.gpsportclient.Utilities.WaitingEventUserRow;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matant on 12/16/2015.
 */
public class WaitingEventListFragmentController extends Fragment implements AsyncResponse {

    private ListView WaitingEventList;
    private SessionManager sm;
    private DBcontroller dbController;
    private ProgressDialog progress;
    List<WaitingEventUserRow> rowWaitingEventsList;
    private WaitingEventUserAdapter invitationsArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_waiting_event_list_fragment_controller, container, false);
        getActivity().setTitle("Events WishList");
        WaitingEventList = (ListView) v.findViewById(R.id.listViewWaitingEvents);
        sm = SessionManager.getInstance(getActivity());
        sendDataToDBController();
        return v;

    }
    @Override
    public void handleResponse(String resStr) {
        progress.dismiss();

        Log.d("get waiting list handleResponse", resStr);
        if (resStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);


                    switch (flg) {
                        case Constants.TAG_REQUEST_SUCCEED: {
                            int no_of_rows = Integer.valueOf(jsonObj.getString("numofrows"));
                            if(no_of_rows < 1){
                                Toast.makeText(getActivity(),jsonObj.getString("msg").toString(),Toast.LENGTH_LONG).show();
                            }else{
                                JSONArray jsonarr = jsonObj.getJSONArray("events");
                                Log.d("array", jsonarr.toString());
                                String title, date, Loc, participants, event_time, event_id;

                                rowWaitingEventsList = new ArrayList<WaitingEventUserRow>();
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
                                    switch (title){
                                        case Constants.TAG_SOCCER:
                                            sportType = R.drawable.soccer_32;
                                            break;
                                        case Constants.TAG_BASKETBALL:
                                            sportType = R.drawable.basketball_32;
                                            break;
                                        case Constants.TAG_RUNNING:
                                            sportType = R.drawable.running_32;
                                            break;
                                        case Constants.TAG_BICYCLE:
                                            sportType = R.drawable.biking_32;
                                            break;
                                    }
                                    Log.d("my parameters:",Loc+","+event_time+","+date+","+participants+","+event_id+","+sportType);
                                    WaitingEventUserRow rowInvite = new WaitingEventUserRow(Loc,event_time,date,participants,event_id,"1",sportType);
                                    rowWaitingEventsList.add(rowInvite);

                                }
                                Log.d("invite array adapter",rowWaitingEventsList.toString());
                                invitationsArrayAdapter = new WaitingEventUserAdapter(getActivity(),R.layout.waiting_events_lisview_row,rowWaitingEventsList);
                                WaitingEventList.setAdapter(invitationsArrayAdapter);
                            }


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
        BasicNameValuePair listType = new BasicNameValuePair("list_type", "waiting");
        BasicNameValuePair user_id = new BasicNameValuePair(Constants.TAG_USERID,sm.getUserDetails().get(Constants.TAG_USERID) );

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(listType);
        nameValuePairList.add(user_id);
        dbController = new DBcontroller(getActivity(),this);
        dbController.execute(nameValuePairList);
    }

    @Override
    public void preProcess() {
        this.progress = ProgressDialog.show(getActivity(), "Retrieve Events",
                "Loading...", true);
    }
}
