package com.example.matant.gpsportclient.DataClasses;

import android.util.Log;

import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;

import java.util.HashMap;

/**
 * Created by nirb on 12/24/2015.
 */
public class SportsHash {

    private static HashMap<String,Sport> sportsHash;
    private static SportsHash sportHashClass = null;

    public static HashMap getSportsHash()
    {
        if (sportHashClass == null) //checking the class variable
        {
            Log.d("sportHashClass", "isEmpty");
            sportHashClass = new SportsHash();
            Log.d("sportHashClass", "and now it's not");

        }
        return sportsHash; //but returning hash map of the class variable
    }

    public SportsHash ()
    {
        sportsHash = new HashMap<String,Sport>();
        Log.d("sportHash", "puts");
        sportsHash.put(Constants.TAG_BASKETBALL, new Sport(Constants.TAG_BASKETBALL, 0xFFE4852D, R.drawable.round_basketball_event_view_img, R.drawable.basketball_32,R.drawable.basketball_marker_icon));
        sportsHash.put(Constants.TAG_BICYCLE, new Sport(Constants.TAG_BICYCLE,0xFF84817E,R.drawable.bycicle_event_view_img,R.drawable.biking_32,R.drawable.bicycle_marker_icon));
        sportsHash.put(Constants.TAG_SOCCER, new Sport(Constants.TAG_SOCCER,0xFF2EC62C,R.drawable.round_soccer_event_view_img,R.drawable.soccer_32, R.drawable.soccer_marker_icon));
        sportsHash.put(Constants.TAG_RUNNING, new Sport(Constants.TAG_RUNNING,0xFFFBEA04,R.drawable.running_event_view_img,R.drawable.running_32,R.drawable.running_marker_icon));
    }

    public class Sport {

        public String getSportName() {
            return sportName;
        }

        private String sportName;

        public int getSportViewEventBackColour() {
            return sportViewEventBackColour;
        }

        private int sportViewEventBackColour;

        public int getSportViewEventPicId() {
            return sportViewEventPicId;
        }

        private int sportViewEventPicId;

        public int getSportViewEventIconForListsId() {
            return sportViewEventIconForListsId;
        }

        private int sportViewEventIconForListsId;

        public int getSportMapMarkerId() {
            return sportMapMarkerId;
        }

        private int sportMapMarkerId;

    public Sport (String name,int colour, int resourceForPicInEvent, int resourceForIconInLists, int resourceForMapMarkerIcon)
    {
        sportName = name;
        sportViewEventBackColour = colour;
        sportViewEventPicId= resourceForPicInEvent;
        sportViewEventIconForListsId= resourceForIconInLists;
        sportMapMarkerId = resourceForMapMarkerIcon;
    }


    }



}
