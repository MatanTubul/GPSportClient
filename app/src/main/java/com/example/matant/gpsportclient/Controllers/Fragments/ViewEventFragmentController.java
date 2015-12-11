package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.CreateInviteUsersRow;
import com.example.matant.gpsportclient.Utilities.CreateInvitedUsersAdapter;
import com.example.matant.gpsportclient.Utilities.MapMarker;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.example.matant.gpsportclient.Utilities.ViewEventArrayAdapter;
import com.example.matant.gpsportclient.Utilities.ViewEventListRow;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nirb on 11/23/2015.
 */
public class ViewEventFragmentController extends Fragment implements View.OnClickListener, AsyncResponse {

    private TextView kindOfSportText;
    private ImageView kindOfSportImage;
    private TextView startTimeText;
    private Button minAgeButton, privilegeButton, attendingButton, genderButton, participateButton, playingListViewHeadlineButton, waitingListViewHeadlineButton, invitedListViewHeadlineButton;
    private TextView addressText;
    private ListView listViewPlayingList, listViewWaitingList, listViewInvitedList;
    private JSONObject eventDetailsJsonObj;
    private ViewEventArrayAdapter viewEventAdapterForPlayingList;
    private ViewEventArrayAdapter viewEventAdapterForWaitingList;
    private ViewEventArrayAdapter viewEventAdapterForInvitedList;
    private List<ViewEventListRow> playingList = null;
    private List<ViewEventListRow> invitedList = null;
    private List<ViewEventListRow> waitingList = null;
    private String eventPrivilege;
    private DBcontroller dbController;
    private String eventId , currentUserId;
    private LinearLayout playingLinearLayout, waitingLinearLayout, invitedLinearLayout;
    private SessionManager sm;
    private View v;

    public ViewEventFragmentController(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_view_event_fragment_controller, container, false);
        sm = SessionManager.getInstance(getActivity());

        String eventDetailsJsonStr = (String) getArguments().getString("event");
        try {
            eventDetailsJsonObj = new JSONObject(eventDetailsJsonStr);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        //initialize the widgets

        kindOfSportText = (TextView) v.findViewById(R.id.kind_of_sport_text);
        kindOfSportImage = (ImageView) v.findViewById(R.id.kind_of_sport_img);
        startTimeText = (TextView) v.findViewById(R.id.time_text);
        addressText = (TextView) v.findViewById(R.id.address_text);
        playingLinearLayout = (LinearLayout) v.findViewById(R.id.playing_list_layout);
        waitingLinearLayout = (LinearLayout) v.findViewById(R.id.waiting_list_layout);
        invitedLinearLayout = (LinearLayout) v.findViewById(R.id.invited_list_layout);

        //initialize disabled buttons for viewing the events details
        //using this for just simple design issues
        genderButton = (Button) v.findViewById(R.id.gender_button);
        minAgeButton = (Button) v.findViewById(R.id.min_age_button);
        privilegeButton = (Button) v.findViewById(R.id.privilege_button);
        attendingButton = (Button) v.findViewById(R.id.attending_button);
        playingListViewHeadlineButton = (Button) v.findViewById(R.id.play_button);
        waitingListViewHeadlineButton = (Button) v.findViewById(R.id.wait_button);
        invitedListViewHeadlineButton = (Button) v.findViewById(R.id.invited_button);

        //enabled button for user to attend on the event
        participateButton = (Button) v.findViewById(R.id.participate_button);
        participateButton.setOnClickListener(this);

        //list view initializing
        listViewPlayingList = (ListView) v.findViewById(R.id.playing_list_view);
        listViewPlayingList.setItemsCanFocus(true);
        listViewWaitingList = (ListView) v.findViewById(R.id.waiting_list_view);
        listViewWaitingList.setItemsCanFocus(true);
        listViewInvitedList = (ListView) v.findViewById(R.id.invited_list_view);
        listViewInvitedList.setItemsCanFocus(true);

        //put the widgets off and wait to identify event privilege
        playingLinearLayout.setVisibility(View.GONE);
        waitingLinearLayout.setVisibility(View.GONE);
        invitedLinearLayout.setVisibility(View.GONE);

        getActivity().setTitle("View Event Details");
        //put cardentials according to the event marker
        initView();
        getUsersFromDB();

    return v;
    }

private void getUsersFromDB() {
    Log.d("getUsersFromDB", "sendDataToDBController");

    BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, Constants.TAG_GET_EVENT_USERS);
    BasicNameValuePair tageventid = new BasicNameValuePair(Constants.TAG_EVENT_ID, eventId);
    BasicNameValuePair tageventprivilege = new BasicNameValuePair(Constants.TAG_IS_PRIVATE, eventPrivilege);


    List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
    nameValuePairList.add(tagreq);
    nameValuePairList.add(tageventid);
    nameValuePairList.add(tageventprivilege);

    dbController = new DBcontroller(getActivity().getApplicationContext(), this);
    dbController.execute(nameValuePairList);
}

    private void initView()
    {

        kindOfSportImage.setImageResource(R.drawable.round_soccer_event_view_img);
        Log.d("ViewEveFragController", "initView");

        try{

            kindOfSportText.setText(eventDetailsJsonObj.getString(Constants.TAG_KIND_OF_SPORT));
            startTimeText.setText(eventDetailsJsonObj.getString(Constants.TAG_START_TIME));
            addressText.setText(eventDetailsJsonObj.getString(Constants.TAG_ADDRESS));

            eventId = eventDetailsJsonObj.getString(Constants.TAG_EVENT_ID);
            String eventGender = eventDetailsJsonObj.getString(Constants.TAG_GEN);
            if (eventGender.equals("Unisex"))
                genderButton.setText("UNISEX");
            else
                genderButton.setText(eventGender + " ONLY");

            String eventMinAge = eventDetailsJsonObj.getString(Constants.TAG_MIN_AGE);
            minAgeButton.setText(eventMinAge + "+");

            eventPrivilege = eventDetailsJsonObj.getString(Constants.TAG_IS_PRIVATE);
            if (eventPrivilege.equals("true")){
                privilegeButton.setText("PRIVATE");
                //enable the UI for private event
                invitedLinearLayout.setVisibility(View.VISIBLE);
            }

            else {
                privilegeButton.setText("PUBLIC");
                //enable the UI for public event
                playingLinearLayout.setVisibility(View.VISIBLE);
                waitingLinearLayout.setVisibility(View.VISIBLE);

            }
            attendingButton.setText(eventDetailsJsonObj.getString(Constants.TAG_CURR_PRT) + " / " +
                    eventDetailsJsonObj.getString(Constants.TAG_MAX_PRT));

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initAdapter(String users, String manager, String mngStatus){
        JSONArray resUsers = null;
        JSONArray resManager = null;

        Log.d("ViewEveFragController", "initAdapter");
        try {
            resUsers = new JSONArray(users);
            resManager = new JSONArray(manager);
            String managerId = resManager.getJSONObject(0).getString("id");
            currentUserId = sm.getUserDetails().get(Constants.TAG_USERID);


            Log.d("res jsonarray", resUsers.toString());

            if (eventPrivilege.equals("false")) //if this is a public event there's is two viewlists we want to create:
            {
                participateButton.setText("JOIN EVENT");

                playingList = new ArrayList<ViewEventListRow>();//participate/playing list
                waitingList = new ArrayList<ViewEventListRow>();//waiting list

                for(int i=0 ;i< resUsers.length(); i++){
                    Log.d("iteration",String.valueOf(i));
                    String name = resUsers.getJSONObject(i).getString("fname");
                    Log.d("iteration " + String.valueOf(i) ,name);
                    String img = resUsers.getJSONObject(i).getString("image");
                    String id = resUsers.getJSONObject(i).getString("id");
                    Log.d("iteration " + String.valueOf(i),id);
                    boolean isManager = false;
                    if (id.equals(managerId))
                        isManager = true;
                    String status = resUsers.getJSONObject(i).getString("status");
                    Log.d("iteration " + String.valueOf(i),status);

                    //ViewEventListRow playingUser = new ViewEventListRow(name,decompressImage(img),id);
                    if (status.equals("participate")) {
                        ViewEventListRow playingUser = new ViewEventListRow(name, id,status, isManager);
                        playingList.add(playingUser);
                        Log.d(playingUser.toString(), status);
                    }
                    else {
                        ViewEventListRow waitingUser = new ViewEventListRow(name, id, status, isManager);
                        waitingList.add(waitingUser);
                        Log.d(waitingUser.toString(), status);
                    }
                    if (id.equals(currentUserId))
                        participateButton.setText("LEAVE EVENT");
                }

                viewEventAdapterForPlayingList = new ViewEventArrayAdapter(getActivity(),R.layout.listview_item_row, playingList);
                listViewPlayingList.setAdapter(viewEventAdapterForPlayingList);
                viewEventAdapterForPlayingList.setAdapterListView(listViewPlayingList);
                viewEventAdapterForPlayingList.setListViewHeightBasedOnChildren();
                listViewPlayingList = viewEventAdapterForPlayingList.getAdapterListView();

                viewEventAdapterForWaitingList = new ViewEventArrayAdapter(getActivity(),R.layout.listview_item_row, waitingList);
                listViewWaitingList.setAdapter(viewEventAdapterForWaitingList);
                viewEventAdapterForWaitingList.setAdapterListView(listViewWaitingList);
                viewEventAdapterForWaitingList.setListViewHeightBasedOnChildren();
                listViewWaitingList = viewEventAdapterForWaitingList.getAdapterListView();

            }
            else // or else and it's a private event we need only one viewlist
            {
                invitedList = new ArrayList<ViewEventListRow>();//invited users list
                participateButton.setEnabled(false);
                participateButton.setText("THIS IS A PRIVATE EVENT");

                for(int i=0 ;i< resUsers.length(); i++){
                    Log.d("iteration",String.valueOf(i));
                    String name = resUsers.getJSONObject(i).getString("fname");
                    String img = resUsers.getJSONObject(i).getString("image");
                    String id = resUsers.getJSONObject(i).getString("id");
                    String status = resUsers.getJSONObject(i).getString("status");
                    //ViewEventListRow invitedUser = new ViewEventListRow(name,decompressImage(img),id);

                    ViewEventListRow invitedUser = new ViewEventListRow(name, id, status, false);
                    invitedList.add(invitedUser);
                    initParticipationTextButtonForPrivateEvent(id, status);

                }
                String managerName = resManager.getJSONObject(0).getString("fname");
                String managerImg = resUsers.getJSONObject(0).getString("image");
                ViewEventListRow invitedUser = new ViewEventListRow(managerName, managerId, mngStatus, true);
                invitedList.add(invitedUser);

                initParticipationTextButtonForPrivateEvent(managerId, mngStatus);

                viewEventAdapterForInvitedList = new ViewEventArrayAdapter(getActivity(),R.layout.listview_item_row, invitedList);
                listViewInvitedList.setAdapter(viewEventAdapterForInvitedList);
                viewEventAdapterForInvitedList.setAdapterListView(listViewInvitedList);
                viewEventAdapterForInvitedList.setListViewHeightBasedOnChildren();
                listViewInvitedList = viewEventAdapterForInvitedList.getAdapterListView();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

private void initParticipationTextButtonForPrivateEvent (String id, String status) {
    if (id.equals(currentUserId)) {
        participateButton.setEnabled(true);
        if (status.equals("awaiting Reply") || status.equals("not attend"))
            participateButton.setText("ATTENDING");
        else
            participateButton.setText("NOT ATTENDING");

    }

}

    private Bitmap decompressImage (String imageString)
    {
        if (imageString.equals("nofile")) {
            Log.d("setOnUI", "nofile");
            return null;
        }
        else
            return decodeBase64(imageString);

    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    @Override
    public void onClick(View view) {
        switch(v.getId()) {
            case R.id.participate_button:
                //public

                //if he wants to leave -> waiting list or playing list
                //delete from attending table
                //if he was the manager -> //delete from attending table, delete manager
                //in both cases check for a player in waiting list to add to playing list

                //if he wants to participate ->
                //checks: gender, min_age, max num participants
                //if gender and min_age is good and there is a place in playing list -> insert raw on database with playing
                //if there is place in waiting list ->insert raw on database with playing
                // if there is no place or gender/min_age don't fit pop uo a msg

                //or
                //private

                //if he wants not to attend -> invited list
                //change status in attending table
                //if he was the manager -> //delete from attending table, delete/change manager

                //if he wants to attend ->
                //checks: gender, min_age, max num participants this check should be on invite users..
                //-> change status in attending table
                //if this is the first user after the manager ->insert manager function


                break;
        }
    }

    @Override
    public void handleResponse(String resStr) {
        //progress.dismiss();

        if (resStr != null){
            Log.d("ViewEveFragController", resStr);
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);
                switch (flg){
                    case Constants.TAG_REQUEST_SUCCEED:
                    {
                        String eventUsers = jsonObj.getString("event_users");
                        String eventManager = jsonObj.getString("mng_details");
                        String eventStatus = jsonObj.getString("mng_status");
                        Log.d("event_manager, event_status", eventManager + " " + eventStatus);
                        Log.d("event_users", eventUsers);
                        initAdapter(eventUsers , eventManager, eventStatus);
                        Log.d("ViewEveFragController", "sending users to lists");
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

    }

    @Override
    public void preProcess() {

    }
}
