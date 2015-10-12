package com.example.matant.gpsportclient.Utilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matant.gpsportclient.R;

import java.util.List;

/**
 * Created by matant on 10/12/2015.
 */
public class CreateInvitedUsersAdapter extends ArrayAdapter<CreateInviteUsersRow> {
    Context context;
    List<CreateInviteUsersRow> invitedUsers;

    public CreateInvitedUsersAdapter(Context context, int resource, List<CreateInviteUsersRow> objects) {
        super(context, resource, objects);
        this.context = context;
        this.invitedUsers = objects;
    }

    private class ViewHolder {

        TextView txtTitle;
        TextView txtDesc;
        ImageButton imgStatus;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //final  View vi;

        final CreateInviteUsersRow rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.create_users_invited_item, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.mobile);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.name);
            holder.imgStatus = (ImageButton) convertView.findViewById(R.id.imageButtonRemoveUser);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtDesc.setText(rowItem.getMobile());
        holder.txtTitle.setText(rowItem.getName());
        holder.imgStatus.setImageResource(rowItem.getStatus());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("data changed", "changing data");
                invitedUsers.remove(position);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "user Removed", Toast.LENGTH_SHORT).show();
            }
        });



        return convertView;
    }
}
