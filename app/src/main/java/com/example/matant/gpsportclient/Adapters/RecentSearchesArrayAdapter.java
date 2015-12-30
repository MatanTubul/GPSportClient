package com.example.matant.gpsportclient.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matant.gpsportclient.Controllers.Fragments.SearchEventFragmentController;
import com.example.matant.gpsportclient.DataClasses.SportsHash;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.DataClasses.RecentSearchRowModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by matant on 12/21/2015.
 */
public class RecentSearchesArrayAdapter extends ArrayAdapter<RecentSearchRowModel> {
    Context ctx;
    List<RecentSearchRowModel> rs;
    private HashMap sh;

    public RecentSearchesArrayAdapter(Context context, int resource, List<RecentSearchRowModel> objects) {
        super(context, resource, objects);
        this.ctx = context;
        this.rs = objects;
        sh = SportsHash.getSportsHash();

    }

    private class ViewHolder {
        TextView txtDate;
        TextView txtLoc;
        TextView txtTime;
        TextView txtKindOfSport;
        ImageButton loadSearch;
        ImageView searchImg;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //final  View vi;
        final RecentSearchRowModel rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listview_recent_searches_row, null);
            holder = new ViewHolder();
            holder.txtLoc = (TextView) convertView.findViewById(R.id.textViewRecentSplace);
            holder.txtTime = (TextView) convertView.findViewById(R.id.textViewRecentStime);
            holder.txtDate = (TextView) convertView.findViewById(R.id.textViewRSearchesDate);
            holder.txtKindOfSport = (TextView) convertView.findViewById(R.id.textViewRecentSKindOfSport);
            holder.loadSearch = (ImageButton) convertView.findViewById(R.id.ImageButtonRecentSsent);
            holder.searchImg = (ImageView) convertView.findViewById(R.id.ImageRecentSearch);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String address = rowItem.getValue(Constants.TAG_REAL_ADDRESS);
        address = "Address: " + address.replace("\n"," ");
        holder.txtLoc.setText(address);
        String sport = rowItem.getValue(Constants.TAG_KIND_OF_SPORT);
        if (sport.equals("General"))
            holder.searchImg.setImageResource(R.drawable.general_sport_search_marker);
            else{
            SportsHash.Sport sportHash=(SportsHash.Sport) sh.get(sport);
            holder.searchImg.setImageResource(sportHash.getSportMapMarkerId());
        }

        sport = "Search For Sport: " + sport;
        holder.txtKindOfSport.setText(sport);
        String timeRange = "Time Range: " + rowItem.getValue(Constants.TAG_START_TIME)+" "+"-"+" "+rowItem.getValue(Constants.TAG_END_TIME);
        holder.txtTime.setText(timeRange);
        String dateRange = "Date Range: " + rowItem.getValue(Constants.TAG_START_DATE)+" "+"-"+" "+rowItem.getValue(Constants.TAG_END_DATE);
        holder.txtDate.setText(dateRange);
        holder.loadSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load search fragment
                Bundle b = new Bundle();
                b.putString("search_params",rowItem.getJobjData().toString());
                final  Activity activity = (Activity) ctx;
                Fragment fragment = new SearchEventFragmentController();
                fragment.setArguments(b);
                FragmentManager fragmentManager =  activity.getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        return convertView;
    }
}
