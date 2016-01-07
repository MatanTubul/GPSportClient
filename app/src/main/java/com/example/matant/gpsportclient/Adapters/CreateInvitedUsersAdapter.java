package com.example.matant.gpsportclient.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.DataClasses.CreateInviteUsersRow;

import java.util.List;

/**
 * Custom adapter for the invited user list. this class presenting the list of all the users that was found
 * by searching a specific name before creating a private event.
 * Created by matant on 10/12/2015.
 */
public class CreateInvitedUsersAdapter extends ArrayAdapter<CreateInviteUsersRow> {
    Context context;
    List<CreateInviteUsersRow> invitedUsers;
    private ListView adapterListview;

    public CreateInvitedUsersAdapter(Context context, int resource, List<CreateInviteUsersRow> objects) {
        super(context, resource, objects);
        this.context = context;
        this.invitedUsers = objects;
    }

    public ListView getAdapterListview() {
        return adapterListview;
    }

    public void setAdapterListview(ListView adapterListview) {
        this.adapterListview = adapterListview;
    }

    private class ViewHolder {

        TextView txtTitle;
        TextView txtDesc;
        ImageButton imgStatus;
        ImageView userError;
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
            holder.userError = (ImageView) convertView.findViewById(R.id.imageViewUserError);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtDesc.setText(rowItem.getMobile());
        holder.txtTitle.setText(rowItem.getName());
        holder.imgStatus.setImageResource(rowItem.getStatus());
        holder.userError.setImageResource(rowItem.getImgViewUserError());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("data changed", "changing data");
                invitedUsers.remove(position);
                notifyDataSetChanged();
                setListViewHeightBasedOnChildren();
                Toast.makeText(getContext(), "user Removed", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
    /**
     * updatig the height of the listview
     */
    public void setListViewHeightBasedOnChildren() {
        ListAdapter listAdapter = this.getAdapterListview().getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, this.getAdapterListview());
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = this.getAdapterListview().getLayoutParams();
        params.height = totalHeight
                + (this.getAdapterListview().getDividerHeight() * (listAdapter.getCount() - 1));
        this.getAdapterListview().setLayoutParams(params);
    }
}
