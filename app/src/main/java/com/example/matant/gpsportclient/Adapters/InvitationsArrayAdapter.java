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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matant.gpsportclient.Controllers.Fragments.ViewEventFragmentController;
import com.example.matant.gpsportclient.DataClasses.InvitationsRowModel;
import com.example.matant.gpsportclient.R;

import java.util.List;

/**
 * Class that holding the view of how the invitations list should be view
 * holding a list of items which presenting a row data related to invitations list.
 * Created by matant on 12/10/2015.
 */
public class InvitationsArrayAdapter extends ArrayAdapter<InvitationsRowModel> {
    Context context;
    List<InvitationsRowModel> invitationsList;
    private Fragment fragment = null;
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
            holder.txtTime = (TextView) convertView.findViewById(R.id.textViewWaitingTimeVal);
            holder.txtDate = (TextView) convertView.findViewById(R.id.textViewWaitingDateVal);
            holder.txtParticipants = (TextView) convertView.findViewById(R.id.textViewWaitingInvParticipantVal);
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
                Bundle args = new Bundle();
                args.putString("event", rowItem.getEventRecord().toString());
                final  Activity activity = (Activity) context;
                Fragment fragment = new ViewEventFragmentController();
                fragment.setArguments(args);
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "back_to_invitations").addToBackStack("back_to_invitations").commit();
            }
        });
        return convertView;
    }
}

