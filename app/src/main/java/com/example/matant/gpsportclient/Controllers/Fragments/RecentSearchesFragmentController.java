package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.RecentSearchRowModel;
import com.example.matant.gpsportclient.Utilities.RecentSearchesArrayAdapter;
import com.example.matant.gpsportclient.Utilities.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by matant on 12/21/2015.
 */
public class RecentSearchesFragmentController extends Fragment {
    private ListView rsListView;
    private SessionManager sm;
    List<RecentSearchRowModel> rsTotalList;
    private RecentSearchesArrayAdapter rsAdapter;
    public RecentSearchesFragmentController(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_recent_searches_fragment_controller, container, false);

        rsListView = (ListView) v.findViewById(R.id.listViewRecentSearches);
        sm = SessionManager.getInstance(getActivity());
        getActivity().setTitle("Recent Searches");
        HashMap<String,String> rs =sm.getRecentSearches();
        if(rs.size() < 1)
            Toast.makeText(getActivity(),"Recent Searches does not exist!",Toast.LENGTH_LONG).show();
        else{
            rsTotalList = new ArrayList<RecentSearchRowModel>();
            for(int i=rs.size()-1; i >= 0;i--){
                RecentSearchRowModel rsRow = new RecentSearchRowModel(rs.get(String.valueOf(i+1)));
                rsTotalList.add(rsRow);
            }
            rsAdapter = new RecentSearchesArrayAdapter(getActivity(),R.layout.listview_recent_searches_row,rsTotalList);
            rsListView.setAdapter(rsAdapter);
        }

        return v;

    }
}