package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.DataClasses.SportsHash;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.ImageConvertor;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.example.matant.gpsportclient.Adapters.ViewEventArrayAdapter;
import com.example.matant.gpsportclient.DataClasses.ViewEventListRow;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nirb on 11/23/2015.
 */
public class ViewEventFragmentController extends Fragment implements View.OnClickListener, AsyncResponse {

    private TextView kindOfSportText,startTimeText,addressText;
    private ImageView kindOfSportImage;
    private Button minAgeButton, privilegeButton, attendingButton, genderButton, participateButton, playingListViewHeadlineButton, waitingListViewHeadlineButton, invitedListViewHeadlineButton;
    private ImageButton wazeButton;
    private ListView listViewPlayingList, listViewWaitingList, listViewInvitedList;
    private JSONObject eventIdJsonObj, eventDetailsJsonObj;
    private List<ViewEventListRow> playingList = null, invitedList = null, waitingList = null;
    private DBcontroller dbController;
    private String eventId, currentUserId, userStatusChoise, eventPrivilege, publicEventError, eventGender, eventMinAge, eventDetailsJsonStr;
    private LinearLayout playingLinearLayout, waitingLinearLayout, invitedLinearLayout;
    private SessionManager sm;
    private boolean isCurrentUserIsTheCurrentEventManager;
    private List<NameValuePair> nameValuePairList;
    private double lati, longi;
    private HashMap sh;
    private FrameLayout frameLayout;
    public ViewEventFragmentController(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_view_event_fragment_controller, container, false);
        sm = SessionManager.getInstance(getActivity());
        sh = SportsHash.getSportsHash();
        isCurrentUserIsTheCurrentEventManager = false;
        userStatusChoise = null;
        eventDetailsJsonStr = (String) getArguments().getString("event");
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
        frameLayout = (FrameLayout) v.findViewById(R.id.background);

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

        //waze button
        wazeButton = (ImageButton) v.findViewById(R.id.wazeButton);
        wazeButton.setOnClickListener(this);

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
        Log.d("ViewEveFragController", "initView");

        try{
            String sportKind = eventDetailsJsonObj.getString(Constants.TAG_KIND_OF_SPORT);
            Log.d("sportHash to string", sh.toString());
            SportsHash.Sport sport=(SportsHash.Sport) sh.get(sportKind);

            kindOfSportImage.setImageResource(sport.getSportViewEventPicId());
            frameLayout.setBackgroundColor(sport.getSportViewEventBackColour());

            lati = Double.valueOf(eventDetailsJsonObj.getString(Constants.LAT));
            longi = Double.valueOf(eventDetailsJsonObj.getString(Constants.LONG));
            kindOfSportText.setText(eventDetailsJsonObj.getString(Constants.TAG_KIND_OF_SPORT));
            String StartTimeByDB = eventDetailsJsonObj.getString(Constants.TAG_START_TIME);
            startTimeText.setText(StartTimeByDB.substring(0, StartTimeByDB.length() - 3));
            addressText.setText(eventDetailsJsonObj.getString(Constants.TAG_ADDRESS));

            eventGender = eventDetailsJsonObj.getString(Constants.TAG_GEN);
            if (eventGender.equals("Unisex"))
                genderButton.setText("UNISEX");
            else
                genderButton.setText(eventGender + " ONLY");

            eventMinAge = eventDetailsJsonObj.getString(Constants.TAG_MIN_AGE);
            minAgeButton.setText(eventMinAge + "+");

            eventPrivilege = eventDetailsJsonObj.getString(Constants.TAG_PRIVATE);
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
            Log.d("ViewEveFragController", currentUserId);

            if (eventPrivilege.equals("false")) //if this is a public event there are two viewlists we want to create:
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
                String managerImg = resManager.getString("image");
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
                String managerImg = resManager.getString("image");
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
        if (status.equals("awaiting reply") || status.equals("not attend")) {
            participateButton.setText("ATTENDING");
            userStatusChoise = "attend";
        }
        else {
            participateButton.setText("NOT ATTENDING");
            userStatusChoise = "not attend";
        }
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
                break;
            case R.id.wazeButton:
            {
                final String url = String.format("waze://?ll=%f,%f&navigate=yes", lati, longi);
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
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
                    case Constants.TAG_REQUEST_VIEW_SUCCEED:
                    {

                        Log.d("TAG_REQUEST_VIEW_SUCCEED","TAG_REQUEST_VIEW_SUCCEED");
                        Fragment fragment = new ViewEventFragmentController();
                        Bundle args = new Bundle();
                        args.putString("event",eventDetailsJsonStr);
                        fragment.setArguments(args);
                        if (fragment != null) {
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                            fragTransaction.remove(this);
                            fragTransaction.replace(R.id.content_frame, fragment).commit();
                        } else {
                            Log.e("GoogleMap Fragment", "Error in creating fragment");
                        }
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
        BasicNameValuePair tagreq = null;

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
                        .setTitle("Can't Perform Action")
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
        executeCommonFields(tagreq);
    }

    private void executeCommonFields(BasicNameValuePair tagreq){

        nameValuePairList = new ArrayList<NameValuePair>();
        BasicNameValuePair fragid = new BasicNameValuePair(Constants.TAG_VIEW,Constants.TAG_VIEW);
        BasicNameValuePair userid = new BasicNameValuePair(Constants.TAG_USERID,currentUserId);
        BasicNameValuePair eventid = new BasicNameValuePair(Constants.TAG_EVENT_ID,eventId);
        BasicNameValuePair status = new BasicNameValuePair(Constants.TAG_USER_STAT,userStatusChoise);
        BasicNameValuePair eventisprivate = new BasicNameValuePair(Constants.TAG_IS_PRIVATE,eventPrivilege);

        nameValuePairList.add(fragid);
        nameValuePairList.add(eventid);
        nameValuePairList.add(userid);
        nameValuePairList.add(tagreq);
        nameValuePairList.add(status);
        nameValuePairList.add(eventisprivate);

        Log.d("executeCommonFields",tagreq.toString() + " " + eventid.toString()+ " " + userid.toString() + " " + status.toString());
        sendDataToDBController();
    }



    private boolean userCanParticipateInPublicEvent ()
    {
        HashMap<String,String> userDetails = sm.getUserDetails();

        if(!eventGender.equals("Unisex"))
            if(!userDetails.get(Constants.TAG_GEN).equals(eventGender))
            {
                Log.d("userCanParticipateInPublicEvent",userDetails.get(Constants.TAG_GEN) + " " + eventGender );
            publicEventError = "This event is for " + eventGender +" only";
            return false;
            }

        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);

        Log.d("userCanParticipateInPublicEvent",userDetails.get(Constants.TAG_AGE) + " " + eventMinAge );

        if (thisYear - Integer.valueOf(userDetails.get(Constants.TAG_AGE)) < Integer.valueOf(eventMinAge))
        {
            Log.d("userCanParticipateInPublicEvent",userDetails.get(Constants.TAG_AGE) + " " + eventMinAge );
            publicEventError = "This event is for " + eventMinAge +" year old and above";;
            return false;
        }

        return true;
    }


    private void deleteOrLeaveUserFromEvent()
    {
        BasicNameValuePair tagreq = null;

        if(isCurrentUserIsTheCurrentEventManager){
            Log.d("delete manager",String.valueOf(isCurrentUserIsTheCurrentEventManager));
            tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "remove_event_manager");
        }else{
            Log.d("delete participant",String.valueOf(isCurrentUserIsTheCurrentEventManager));
            tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "remove_participant");
        }

        executeCommonFields(tagreq);
    }



    @Override
    public void sendDataToDBController() {
        dbController = new DBcontroller(getActivity(),this);
        dbController.execute(nameValuePairList);
    }

    @Override
    public void preProcess() {
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

                        String frag_name = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName();
                        Log.d("fragment stack",frag_name);
                        Fragment fragment = null;
                        switch (frag_name){

                            case "attending":
                                fragment = new AttendingListFragmentController();
                                break;
                            case "invitations":
                                fragment = new InvitationsFragmentController();
                                break;
                            case "waiting_list":
                                fragment = new WaitingEventListFragmentController();
                                break;
                            case "recent_searches":
                                fragment = new RecentSearchesFragmentController();
                                break;
                            default:
                                break;
                        }
                        if(fragment != null){
                            getFragmentManager().popBackStackImmediate();
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment,frag_name).addToBackStack(frag_name).commit();
                        }

                    }
                }
                return false;
            }
        });
    }



}
