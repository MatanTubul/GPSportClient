package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.MapMarker;

/**
 * Created by nirb on 11/23/2015.
 */
public class ViewEventFragmentController extends Fragment {

    private TextView kindOfSporttext;
    private ImageView kindOfSportImage;


    public ViewEventFragmentController(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_view_event_fragment_controller, container, false);

        MapMarker mapmarker = (MapMarker) getArguments().getSerializable("mapmarker");

        //initialize the widgets
        kindOfSporttext = (TextView) v.findViewById(R.id.kind_of_sport_text);
        kindOfSportImage = (ImageView) v.findViewById(R.id.kind_of_sport_img);


        //put cardentials according to the event marker
        kindOfSporttext.setText(mapmarker.getmLabel());
        kindOfSportImage.setImageResource(R.drawable.round_soccer_event_view_img);
    return v;
    }


}
