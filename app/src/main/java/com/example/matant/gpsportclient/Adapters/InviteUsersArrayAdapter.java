package com.example.matant.gpsportclient.Adapters;

/**
 * class that holding all the users that invited to a private event
 * Created by matant on 9/24/2015.
 */


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

import com.example.matant.gpsportclient.DataClasses.InviteUsersListRow;
import com.example.matant.gpsportclient.R;

import java.util.ArrayList;
import java.util.List;

public class InviteUsersArrayAdapter extends ArrayAdapter<InviteUsersListRow> {

    Context context;
    List<InviteUsersListRow> rowUsers;
    private List<InviteUsersListRow> checkedUsers;
    int index;

    public InviteUsersArrayAdapter(Context context,int resourceId, List<InviteUsersListRow> items) {
        super(context, resourceId, items);
        this.context = context;
        this.rowUsers = items;
        checkedUsers = new ArrayList<InviteUsersListRow>();
        index = -1;
    }

    /*private view holder class*/
    private class ViewHolder {
       // ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
        ImageButton imgStatus;
        ImageView imgProf;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //final  View vi;

        final InviteUsersListRow rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.invite_users_listview_row, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);

            holder.imgProf = (ImageView) convertView.findViewById(R.id.profileImage);
            holder.imgStatus = (ImageButton) convertView.findViewById(R.id.imageButtonInviteUser);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtDesc.setText(rowItem.getDesc());
        holder.txtTitle.setText(rowItem.getTitle());
        holder.imgStatus.setImageResource(rowItem.getImageStatus());
        holder.imgProf.setImageBitmap(rowItem.getImgProfile());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("data changed", "changing data");

                if(rowItem.getImageStatus() == R.drawable.add_user_50){
                    rowItem.setImagestatus(R.drawable.remove_user_50);
                    index++;
                    checkedUsers.add(index, rowItem);
                    Toast.makeText(getContext(),"user added",Toast.LENGTH_SHORT).show();
                }
                else{
                    rowItem.setImagestatus(R.drawable.add_user_50);
                    checkedUsers.remove(index);
                    index--;
                    Toast.makeText(getContext(),"user removed",Toast.LENGTH_SHORT).show();
                }
                    notifyDataSetChanged();

            }
        });



        return convertView;
    }

    @Override
    public int getCount() {
        return rowUsers.size();
    }

    @Override
    public InviteUsersListRow getItem(int position) {
        return rowUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowUsers.indexOf(getItem(position));
    }
    public void setData(List<InviteUsersListRow> list){
        this.rowUsers = list;
    }
    public List<InviteUsersListRow> getUsers()
    {
        return checkedUsers;
    }
}
