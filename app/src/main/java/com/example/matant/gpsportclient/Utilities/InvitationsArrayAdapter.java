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
 * Created by matant on 12/10/2015.
 */
public class InvitationsArrayAdapter extends ArrayAdapter<InvitationsRowModel> {
    Context context;
    List<InvitationsRowModel> invitationsList;
    public InvitationsArrayAdapter(Context ctx,int resourceId, List<InvitationsRowModel> items){
        super(ctx,resourceId, items);
        this.context = ctx;
        this.invitationsList = items;
    }

    private class ViewHolder {
        TextView txtDate;
        TextView txtLoc;
        TextView txtTime;
        TextView txtParticipants;
        ImageView sportType;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //final  View vi;
        final InvitationsRowModel rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.invatations_list_view_row, null);
            holder = new ViewHolder();
            holder.txtLoc = (TextView) convertView.findViewById(R.id.textViewLocVal);
            holder.txtTime = (TextView) convertView.findViewById(R.id.textViewTimeVal);
            holder.txtDate = (TextView) convertView.findViewById(R.id.textViewDateVal);
            holder.txtParticipants = (TextView) convertView.findViewById(R.id.textViewInvParticipantVal);
            holder.sportType = (ImageView) convertView.findViewById(R.id.ImageViewInvSportType);
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

