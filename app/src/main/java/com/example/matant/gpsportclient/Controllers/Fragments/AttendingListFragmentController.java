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
import android.widget.Toast;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.DataClasses.SportsHash;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Adapters.ManageEventArrayAdapter;
import com.example.matant.gpsportclient.DataClasses.ManageEventListRow;
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
 * Fragment that presenting all the event that the user is part of them
 * the user have the ability to leave event by entering into the event or directly from the list.
 * Created by matant on 11/29/2015.
 */
public class AttendingListFragmentController extends Fragment implements View.OnClickListener,AsyncResponse {

    private ListView ListViewAttendingList;
    private SessionManager sm;
    private DBcontroller dbController;
    private ProgressDialog progress;
    List<ManageEventListRow> rowEvents;
    private ManageEventArrayAdapter ManageEventAdapter = null;
    private HashMap sh;
    private boolean isResumed =false;

    public AttendingListFragmentController()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_manage_event_fragment_controller, container, false);
        ListViewAttendingList = (ListView) v.findViewById(R.id.listViewManageEvent);
        ListViewAttendingList.setItemsCanFocus(true);
        sm = SessionManager.getInstance(getActivity());
        sh = SportsHash.getSportsHash();
        sendDataToDBController();
        getActivity().setTitle("Participation Events");
        return v;
    }

    @Override
    public void handleResponse(String resStr) {
        progress.dismiss();
        if (resStr != null){
            Log.d("attending event handleResponse", resStr);
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);
                switch (flg){
                    case Constants.TAG_REQUEST_SUCCEED:
                    {
                        int no_of_rows = Integer.valueOf(jsonObj.getString("numofrows"));
                        if(no_of_rows < 1){
                            Toast.makeText(getActivity(), jsonObj.getString("msg").toString(), Toast.LENGTH_LONG).show();
                        }else{

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
                                else
                                    isManager = false;
                                event_time = jsonarr.getJSONObject(i).getString("formatted_start_time")+" "+"-"+" "+jsonarr.getJSONObject(i).getString("formatted_end_time");;
                                event_id = jsonarr.getJSONObject(i).getString("event_id");
                                eventObj.put("start_time",jsonarr.getJSONObject(i).getString("formatted_start_time"));
                                eventObj.put("end_time",jsonarr.getJSONObject(i).getString("formatted_end_time"));

                                participants = jsonarr.getJSONObject(i).getString("current_participants");

                                SportsHash.Sport sport=(SportsHash.Sport) sh.get(title);
                                sportType = sport.getSportMapMarkerId();

                                ManageEventListRow rowEvent = new ManageEventListRow(sportType,title,Loc,date,participants,event_id,event_time,eventObj,isManager);
                                rowEvents.add(rowEvent);

                            }
                            if(ManageEventAdapter == null)
                            {
                                Log.d("Resume to Attending","create new adapter");
                                ManageEventAdapter = new ManageEventArrayAdapter(getActivity(),R.layout.manage_event_listview_event,rowEvents,"view");
                                ManageEventAdapter.setUser_id(sm.getUserDetails().get(Constants.TAG_USERID));
                            }
                            else {
                                Log.d("Resume to Attending","notify data changed");
                                ManageEventAdapter.setData(rowEvents);
                                ManageEventAdapter.notifyDataSetChanged();
                                ManageEventAdapter.setUser_id(sm.getUserDetails().get(Constants.TAG_USERID));
                            }
                            ListViewAttendingList.setAdapter(ManageEventAdapter);
                            break;
                        }

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

    @Override
    public void onStop() {
        super.onStop();
        if(this.progress != null)
            this.progress.dismiss();
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
