package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.ManageEventArrayAdapter;
import com.example.matant.gpsportclient.Utilities.ManageEventListRow;
import com.example.matant.gpsportclient.Utilities.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matant on 11/29/2015.
 */
public class AttendingListFragmentController extends Fragment implements View.OnClickListener,AsyncResponse {

    private ListView ListViewAttendingList;
    private SessionManager sm;
    private DBcontroller dbController;
    private ProgressDialog progress;
    List<ManageEventListRow> rowEvents;
    private ManageEventArrayAdapter ManageEventAdapter = null;

    public AttendingListFragmentController()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_manage_event_fragment_controller, container, false);

        ListViewAttendingList = (ListView) v.findViewById(R.id.listViewManageEvent);
        ListViewAttendingList.setItemsCanFocus(true);
        sm = SessionManager.getInstance(getActivity());
        sendDataToDBController();
        getActivity().setTitle("Participation Events");
        return v;
    }

    @Override
    public void handleResponse(String resStr) {
        progress.dismiss();

        Log.d("attending event handleResponse", resStr);
        if (resStr != null){
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);
                switch (flg){
                    case Constants.TAG_REQUEST_SUCCEED:
                    {
                        JSONArray jsonarr = jsonObj.getJSONArray("events");
                        Log.d("creating list",jsonarr.toString());
                        Log.d("array",jsonarr.toString());
                        String title,date,Loc,participants,event_time,event_id,mng_id;
                        boolean isManager =false;
                        rowEvents = new ArrayList<ManageEventListRow>();
                        int sportType = -1;
                        for(int i = 0; i< jsonarr.length();i++){
                            JSONObject eventObj = jsonarr.getJSONObject(i);
                            title = jsonarr.getJSONObject(i).getString("kind_of_sport");
                            date = jsonarr.getJSONObject(i).getString("event_date");
                            Loc = jsonarr.getJSONObject(i).getString("address");
                            mng_id = jsonarr.getJSONObject(i).getString("manager_id");
                            if(mng_id.equals(sm.getUserDetails().get(Constants.TAG_USERID)))
                                isManager = true;
                            event_time = jsonarr.getJSONObject(i).getString("formatted_start_time")+" "+"-"+" "+jsonarr.getJSONObject(i).getString("formatted_end_time");;
                            event_id = jsonarr.getJSONObject(i).getString("event_id");
                            eventObj.put("start_time",jsonarr.getJSONObject(i).getString("formatted_start_time"));
                            eventObj.put("end_time",jsonarr.getJSONObject(i).getString("formatted_end_time"));


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
                            ManageEventListRow rowEvent = new ManageEventListRow(sportType,title,Loc,date,participants,event_id,event_time,eventObj,isManager);
                            rowEvents.add(rowEvent);

                        }
                        if(ManageEventAdapter == null)
                        {
                            ManageEventAdapter = new ManageEventArrayAdapter(getActivity(),R.layout.manage_event_listview_event,rowEvents,"view");
                            ManageEventAdapter.setUser_id(sm.getUserDetails().get(Constants.TAG_USERID));
                        }
                        else {
                            ManageEventAdapter.setData(rowEvents);
                            ManageEventAdapter.notifyDataSetChanged();
                            ManageEventAdapter.setUser_id(sm.getUserDetails().get(Constants.TAG_USERID));
                        }
                        ListViewAttendingList.setAdapter(ManageEventAdapter);
                        break;
                    }

                    case Constants.TAG_REQUEST_FAILED:
                    {
                        Log.d("message from server",jsonObj.getString("msg"));
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void sendDataToDBController() {
        BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "get_participating_event_list");
        BasicNameValuePair user_id = new BasicNameValuePair(Constants.TAG_USERID,sm.getUserDetails().get(Constants.TAG_USERID) );
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(user_id);
        dbController = new DBcontroller(getActivity(),this);
        dbController.execute(nameValuePairList);
    }

    @Override
    public void preProcess() {
        this.progress = ProgressDialog.show(getActivity(), "Retrieve Events",
                "Loading...", true);
    }

    @Override
    public void onClick(View v) {

    }
}