package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.ImageConvertor;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.example.matant.gpsportclient.Utilities.ViewEventArrayAdapter;
import com.example.matant.gpsportclient.Utilities.ViewEventListRow;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nirb on 11/23/2015.
 */
public class ViewEventFragmentController extends Fragment implements View.OnClickListener, AsyncResponse {

    private TextView kindOfSportText,startTimeText,addressText;
    private ImageView kindOfSportImage;
    private Button minAgeButton, privilegeButton, attendingButton, genderButton, participateButton, playingListViewHeadlineButton, waitingListViewHeadlineButton, invitedListViewHeadlineButton;
    private ListView listViewPlayingList, listViewWaitingList, listViewInvitedList;
    private JSONObject eventIdJsonObj, eventDetailsJsonObj;
    private List<ViewEventListRow> playingList = null, invitedList = null, waitingList = null;
    private DBcontroller dbController;
    private String eventId, currentUserId, eventPrivilege, publicEventError, eventGender, eventMinAge;
    private LinearLayout playingLinearLayout, waitingLinearLayout, invitedLinearLayout;
    private SessionManager sm;
    private boolean isCurrentUserIsTheCurrentEventManager;
    private List<NameValuePair> nameValuePairList;

    public ViewEventFragmentController(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_view_event_fragment_controller, container, false);
        sm = SessionManager.getInstance(getActivity());
        isCurrentUserIsTheCurrentEventManager = false;

        String eventDetailsJsonStr = (String) getArguments().getString("event");
        try {
            eventIdJsonObj = new JSONObject(eventDetailsJsonStr);
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
        getEventDetailsFromDB();

    return v;
    }

private void getEventDetailsFromDB() {
    Log.d("getUsersFromDB", "sendDataToDBController");
    try {
        eventId = eventIdJsonObj.getString(Constants.TAG_EVENT_ID);
    }catch (JSONException e) {
        e.printStackTrace();
    }

    BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, Constants.TAG_GET_EVENT_USERS);
    BasicNameValuePair tageventid = new BasicNameValuePair(Constants.TAG_EVENT_ID, eventId);

    List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
    nameValuePairList.add(tagreq);
    nameValuePairList.add(tageventid);

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

            eventGender = eventDetailsJsonObj.getString(Constants.TAG_GEN);
            if (eventGender.equals("Unisex"))
                genderButton.setText("UNISEX");
            else
                genderButton.setText(eventGender + " ONLY");

            eventMinAge = eventDetailsJsonObj.getString(Constants.TAG_MIN_AGE);
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
        JSONObject resManager = null;

        Log.d("ViewEveFragController", "initAdapter");
        try {
            resUsers = new JSONArray(users);
            resManager = new JSONObject(manager);
            String managerId = resManager.getString("id");
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
                    String status = resUsers.getJSONObject(i).getString("status");
                    Log.d("iteration " + String.valueOf(i),status);
                    addPlayerToPublicList(name, id, img, status, false);

                    if (id.equals(currentUserId))
                        participateButton.setText("LEAVE EVENT");
                }
                //add manager details to list
                String managerName = resManager.getString("fname");
                String managerImg = resUsers.getJSONObject(0).getString("image");
                addPlayerToPublicList(managerName, managerId, managerImg, mngStatus, true);

                if (managerId.equals(currentUserId)) {
                    isCurrentUserIsTheCurrentEventManager = true;
                    participateButton.setText("LEAVE EVENT");
                }
                initializeAdapterForPlayersList(listViewPlayingList, playingList, false);
                initializeAdapterForPlayersList(listViewWaitingList,waitingList, false);

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

                    addPlayerToPrivateList(name, id, img, status, false);
                    initParticipationTextButtonForPrivateEvent(id, status);

                }
                //add manager details to list
                String managerName = resManager.getString("fname");
                String managerImg = resUsers.getJSONObject(0).getString("image");
                addPlayerToPrivateList(managerName, managerId, managerImg, mngStatus, true);

                if (initParticipationTextButtonForPrivateEvent(managerId, mngStatus))
                    isCurrentUserIsTheCurrentEventManager = true;

                initializeAdapterForPlayersList(listViewInvitedList, invitedList, true);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeAdapterForPlayersList(ListView listViewPlayersList,List<ViewEventListRow> playersList, boolean isPrivate)
    {
        ViewEventArrayAdapter eventAdapterForPlayersList = new ViewEventArrayAdapter(getActivity(),R.layout.listview_item_row, playersList);
        listViewPlayersList.setAdapter(eventAdapterForPlayersList);
        eventAdapterForPlayersList.setAdapterListView(listViewPlayersList, isPrivate);
        eventAdapterForPlayersList.setListViewHeightBasedOnChildren();
        listViewPlayersList = eventAdapterForPlayersList.getAdapterListView();
    }

    private void addPlayerToPrivateList(String name, String id, String img, String status, boolean isManager)
    {
        ViewEventListRow invitedUser = new ViewEventListRow(name, id, ImageConvertor.decodeBase64(img), status.toUpperCase(), isManager);
        invitedList.add(invitedUser);

    }

    private void addPlayerToPublicList(String name, String id, String img, String status, boolean isManager)
    {
        if (status.equals("participate")) {
            ViewEventListRow playingUser = new ViewEventListRow(name, id, ImageConvertor.decodeBase64(img), status.toUpperCase(), isManager);
            playingList.add(playingUser);
            Log.d(playingUser.toString(), status);
        }
        else {
            ViewEventListRow waitingUser = new ViewEventListRow(name, id, ImageConvertor.decodeBase64(img), status.toUpperCase(), isManager);
            waitingList.add(waitingUser);
            Log.d(waitingUser.toString(), status);
        }
    }

private boolean initParticipationTextButtonForPrivateEvent (String id, String status) {
    if (id.equals(currentUserId)) {
        participateButton.setEnabled(true);
        if (status.equals("awaiting Reply") || status.equals("not attend"))
            participateButton.setText("ATTENDING");
        else
            participateButton.setText("NOT ATTENDING");
        return true;
    }
    return false;
}

    @Override
    public void onClick(View v) {
        Log.d("onClick","onClick");
        switch(v.getId()) {
            case R.id.participate_button:
                if (eventPrivilege.equals("false")) {
                    Log.d("participate_button","public_event");
                    if (participateButton.getText().equals("LEAVE EVENT")) {
                        Log.d("participate_button","participate wants to leave");
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Leave Event")
                                .setMessage("Are you sure you want to leave the event?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        deleteOrLeaveUserFromEvent();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIconAttribute(android.R.attr.alertDialogIcon)
                                .show();
                    }
                    else
                    {
                        participateOrAttendUserInEvent();
                        //checkIfUserCanPerformThis -> sendDataToInsertUser -> serverHandleAndRespond -> responseMakeFragmentReload

                    }
                }
                    else//private event
                {
                    if (participateButton.getText().equals("NOT ATTENDING")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Change Participation Status On Event")
                                .setMessage("Are you sure you want to reject the invitation?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        deleteOrLeaveUserFromEvent();
                                        //serverHandleAndRespond -> responseMakeFragmentReload

                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIconAttribute(android.R.attr.alertDialogIcon)
                                .show();

                    } else {
                        participateOrAttendUserInEvent();
                    }
                }

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
            default:
                Log.d("defualt",String.valueOf(v.getId()));
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
                        String eventManagerStatus = jsonObj.getString("mng_status");
                        String eventDetails = jsonObj.getString("event_details");
                        try {
                            eventDetailsJsonObj = new JSONObject(eventDetails);
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("event_manager, event_status", eventManager + " " + eventManagerStatus);
                        Log.d("event_users", eventUsers);
                        Log.d("event_details", eventDetailsJsonObj.toString());
                        initView();
                        initAdapter(eventUsers , eventManager, eventManagerStatus);
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

    private void participateOrAttendUserInEvent ()
    {
        nameValuePairList = new ArrayList<NameValuePair>();
        BasicNameValuePair tagreq = null;
        BasicNameValuePair userid = new BasicNameValuePair(Constants.TAG_USERID,currentUserId);

        if(eventPrivilege.equals("true")){
            Log.d("attend in private",eventPrivilege);
            tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, Constants.TAG_RES_INV_USR);
        }else {
            if (userCanParticipateInPublicEvent()) {
                Log.d("participant in public", eventPrivilege);
                tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, Constants.TAG_PART_USR);
            }
            else
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Can't Perform ")
                        .setMessage("You can't participate in this event:\n"+ publicEventError)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .show();
                return;
            }
        }
        nameValuePairList.add(userid);
        nameValuePairList.add(tagreq);

        sendDataToDBController();

    }

    private boolean userCanParticipateInPublicEvent ()
    {
        HashMap<String,String> userDetails = sm.getUserDetails();

        if(!eventGender.equals("Unisex"))
            if(!userDetails.get(Constants.TAG_GEN).equals(eventGender))
            {
            publicEventError = "This event is for " + eventGender +"only";
            return false;
            }

        if (Integer.valueOf(userDetails.get(Constants.TAG_AGE)) < Integer.valueOf(eventMinAge))
        {
            publicEventError = "This event is for age " + eventMinAge +"and above";;
            return false;
        }

        return true;
    }


    private void deleteOrLeaveUserFromEvent()
    {
        nameValuePairList = new ArrayList<NameValuePair>();
        BasicNameValuePair tagreq = null;
        BasicNameValuePair userid = new BasicNameValuePair(Constants.TAG_USERID,currentUserId);

        if(isCurrentUserIsTheCurrentEventManager){
            Log.d("delete manager",String.valueOf(isCurrentUserIsTheCurrentEventManager));
            tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "remove_event_manager");
        }else{
            Log.d("delete participant",String.valueOf(isCurrentUserIsTheCurrentEventManager));
            tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "remove_participant");
        }

        nameValuePairList.add(userid);
        nameValuePairList.add(tagreq);

    sendDataToDBController();
    }



    @Override
    public void sendDataToDBController() {
        dbController = new DBcontroller(getActivity(),this);
        dbController.execute(nameValuePairList);
    }

    @Override
    public void preProcess() {

    }
}
