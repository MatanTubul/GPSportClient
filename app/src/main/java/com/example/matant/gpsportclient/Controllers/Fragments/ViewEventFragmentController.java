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
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.CreateInviteUsersRow;
import com.example.matant.gpsportclient.Utilities.CreateInvitedUsersAdapter;
import com.example.matant.gpsportclient.Utilities.MapMarker;
import com.example.matant.gpsportclient.Utilities.ViewEventArrayAdapter;
import com.example.matant.gpsportclient.Utilities.ViewEventListRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nirb on 11/23/2015.
 */
public class ViewEventFragmentController extends Fragment implements View.OnClickListener,AsyncResponse {

    private TextView kindOfSportText;
    private ImageView kindOfSportImage;
    private TextView startTimeText;
    private Button minAgeButton;
    private Button privilegeButton;
    private Button attendingButton;
    private Button genderButton;
    private Button participateButton;
    private TextView addressText;
    private ListView listViewPlayingList;
    private MapMarker mapmarker;
    private ScrollView sv;
    private JSONObject eventDetailsJsonObj;
    private ViewEventArrayAdapter viewEventAdapter;
    private List<ViewEventListRow> playingList = null;

    public ViewEventFragmentController(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_view_event_fragment_controller, container, false);

        mapmarker = (MapMarker) getArguments().getSerializable("mapmarker");
        eventDetailsJsonObj = mapmarker.getmJsonObject();

        //initialize the widgets
        kindOfSportText = (TextView) v.findViewById(R.id.kind_of_sport_text);
        kindOfSportImage = (ImageView) v.findViewById(R.id.kind_of_sport_img);
        startTimeText = (TextView) v.findViewById(R.id.time_text);
        addressText = (TextView) v.findViewById(R.id.address_text);

        //disabled buttons for viewing the events details
        //using this for just simple design issues
        genderButton = (Button) v.findViewById(R.id.gender_button);
        minAgeButton = (Button) v.findViewById(R.id.min_age_button);
        privilegeButton = (Button) v.findViewById(R.id.privilege_button);
        attendingButton = (Button) v.findViewById(R.id.attending_button);

        //enabled button for user to attend on the event
        participateButton = (Button) v.findViewById(R.id.participate_button);
        participateButton.setOnClickListener(this);

        //list view initializing
        listViewPlayingList = (ListView) v.findViewById(R.id.playing_list_view);
        listViewPlayingList.setItemsCanFocus(true);

        getActivity().setTitle("View Event Details");
        sv = (ScrollView) v.findViewById(R.id.scrollView);

        //put cardentials according to the event marker
        initView();

    return v;
    }


    private void initView ()
    {
        kindOfSportText.setText(mapmarker.getmLabel());
        kindOfSportImage.setImageResource(R.drawable.round_soccer_event_view_img);
        Log.d("ViewEveFragController", "initView");

        try{
            startTimeText.setText(eventDetailsJsonObj.getString("start_time"));
            addressText.setText(eventDetailsJsonObj.getString("address"));

            String eventGender = eventDetailsJsonObj.getString("gender");
            if (eventGender.equals("Unisex"))
                genderButton.setText("UNISEX");
            else
                genderButton.setText(eventGender + " ONLY");

            String eventMinAge = eventDetailsJsonObj.getString("min_age");
            minAgeButton.setText(eventMinAge + "+");

            if (eventDetailsJsonObj.getString("private").equals("true"))
                privilegeButton.setText("PRIVATE");
            else
                privilegeButton.setText("PUBLIC");

            attendingButton.setText(eventDetailsJsonObj.getString("current_participants") + " / " +
                    eventDetailsJsonObj.getString("max_participants"));

        }catch (JSONException e) {
            e.printStackTrace();
        }

        initListsView();
    }

    private void initListsView()
    {
        try {
            String event_id = eventDetailsJsonObj.getString("event_id").toString();
            JSONArray jsonarr = new JSONArray(eventDetailsJsonObj.getString("event_users"));
            initAdapter(jsonarr.toString());
            Log.d("ViewEveFragController", "initListsView");

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void initAdapter(String params){
        JSONArray res = null;
        try {
            res = new JSONArray(params);
            Log.d("ViewEveFragController", "initAdapter");
            Log.d("res jsonarray", res.toString());
            playingList = new ArrayList<ViewEventListRow>();
            for(int i=0 ;i< res.length(); i++){
                Log.d("iteration",String.valueOf(i));
                String name = res.getJSONObject(i).getString("fname");
                String img = res.getJSONObject(i).getString("image");
                String id = res.getJSONObject(i).getString("id");
                //ViewEventListRow playingUser = new ViewEventListRow(name,decompressImage(img),id);
                ViewEventListRow playingUser = new ViewEventListRow(name,id);
                playingList.add(playingUser);
            }
            viewEventAdapter = new ViewEventArrayAdapter(getActivity(),R.layout.listview_item_row, playingList);
            listViewPlayingList.setAdapter(viewEventAdapter);
            viewEventAdapter.setAdapterListView(listViewPlayingList);
            viewEventAdapter.setListViewHeightBasedOnChildren();
            listViewPlayingList = viewEventAdapter.getAdapterListView();
        } catch (JSONException e) {
            e.printStackTrace();
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
