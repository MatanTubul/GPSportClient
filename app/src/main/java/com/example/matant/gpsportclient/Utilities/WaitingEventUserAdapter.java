package com.example.matant.gpsportclient.Utilities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matant.gpsportclient.R;

import java.util.List;

/**
 * Created by matant on 12/16/2015.
 */
public class WaitingEventUserAdapter extends ArrayAdapter<WaitingEventUserRow> {

    Context context;
    List<WaitingEventUserRow> waitingEventList;
    public WaitingEventUserAdapter(Context ctx,int resourceId, List<WaitingEventUserRow> items){
        super(ctx,resourceId, items);
        this.context = ctx;
        this.waitingEventList = items;
    }
    private class ViewHolder {
        TextView txtDate;
        TextView txtLoc;
        TextView txtTime;
        TextView txtParticipants;
        TextView placeInQueue;
        ImageView sportType;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //final  View vi;
        final WaitingEventUserRow rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.waiting_events_lisview_row, null);
            holder = new ViewHolder();
            holder.txtLoc = (TextView) convertView.findViewById(R.id.textViewWaitingLocVal);
            holder.txtTime = (TextView) convertView.findViewById(R.id.textViewWaitingTimeVal);
            holder.txtDate = (TextView) convertView.findViewById(R.id.textViewWaitingDateVal);
            holder.txtParticipants = (TextView) convertView.findViewById(R.id.textViewWaitingInvParticipantVal);
            holder.placeInQueue = (TextView) convertView.findViewById(R.id.textViewWaitingQueue);
            holder.sportType = (ImageView) convertView.findViewById(R.id.ImageViewWaitingSportType);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtLoc.setText(rowItem.getPlace());
        holder.txtTime.setText(rowItem.getTime());
        holder.txtDate.setText(rowItem.getEvent_date());
        holder.txtParticipants.setText(rowItem.getEvent_curr_participants());
        holder.sportType.setImageResource(rowItem.getSportImage());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin View event fragment
            }
        });
        return convertView;
    }
}

