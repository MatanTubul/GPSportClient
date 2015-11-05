package com.example.matant.gpsportclient.Utilities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matant.gpsportclient.R;

import java.util.List;

/**
 * Created by matant on 11/4/2015.
 */
public class ManageEventArrayAdapter extends ArrayAdapter<ManageEventListRow> {
    Context context;
    List<ManageEventListRow> mngEvents;

    public ManageEventArrayAdapter(Context ctx,int resourceId, List<ManageEventListRow> items){
        super(ctx,resourceId,items);
        this.context = ctx;
        this.mngEvents = items;
    }
    /*private view holder class*/
    private class ViewHolder {
        ImageView imageViewType;
        TextView txtSport;
        TextView txtDate;
        TextView txtLoc;
        TextView txtParticipants;
        ImageButton imgDelete;
    }
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        //final  View vi;

        final ManageEventListRow rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.manage_event_listview_event, null);
            holder = new ViewHolder();
            holder.txtSport =  (TextView) convertView.findViewById(R.id.textViewTitle);
            holder.txtDate = (TextView) convertView.findViewById(R.id.textViewDateValue);
            holder.txtLoc = (TextView) convertView.findViewById(R.id.textViewLocationval);
            holder.txtParticipants = (TextView) convertView.findViewById(R.id.textViewparticipantsVal);
            holder.imgDelete = (ImageButton) convertView.findViewById(R.id.imageViewDeleteEvent);
            holder.imageViewType = (ImageView) convertView.findViewById(R.id.imageViewSporttype);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtSport.setText(rowItem.getSportType());
        holder.txtDate.setText(rowItem.getDate());
        holder.txtLoc.setText(rowItem.getEventLocation());
        holder.txtParticipants.setText(rowItem.getParticipants());
        holder.imageViewType.setImageResource(rowItem.getSportImage());

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }
    public void setData(List<ManageEventListRow> list){
        this.mngEvents = list;
    }
}
