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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matant.gpsportclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nirb on 11/25/2015.
 */
public class ViewEventArrayAdapter extends ArrayAdapter<ViewEventListRow> {


    private Context context;
    private List<ViewEventListRow> rowUsers;
    private ListView adapterListView;
    private ViewEventListRow rowEvent;
    private int index;

    public ViewEventArrayAdapter(Context context, int resourceId, List<ViewEventListRow> items) {
        super(context, resourceId, items);
        this.context = context;
        this.rowUsers = items;
        index = -1;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtTitle;
        ImageView imageView;

    }

    public void setAdapterListView(ListView adapterListView) {
        this.adapterListView = adapterListView;
    }

    public ListView getAdapterListView() {
        return adapterListView;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //final  View vi;

        final ViewEventListRow rowItem = getItem(position);
        rowEvent = rowItem;


        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.invite_users_listview_row, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtTitle.setTextSize(20);

            convertView.findViewById(R.id.desc).setVisibility(convertView.INVISIBLE);
            convertView.findViewById(R.id.imageButtonInviteUser).setVisibility(convertView.INVISIBLE);
            holder.imageView = (ImageView) convertView.findViewById(R.id.profileImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtTitle.setText(rowItem.getPlayerName());
        holder.imageView.setImageBitmap(rowItem.getPlayerImg());

        //this func can be later implement for manager only
        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("data changed", "changing data");

                if (rowItem.getImageStatus() == R.drawable.add_user_50) {
                    rowItem.setImagestatus(R.drawable.remove_user_50);
                    index++;
                    checkedUsers.add(index, rowItem);
                    Toast.makeText(getContext(), "user added", Toast.LENGTH_SHORT).show();
                } else {
                    rowItem.setImagestatus(R.drawable.add_user_50);
                    checkedUsers.remove(index);
                    index--;
                    Toast.makeText(getContext(), "user removed", Toast.LENGTH_SHORT).show();
                }
                notifyDataSetChanged();

            }
        });*/

        return convertView;
    }


    /**
     * updatig the height of the listview
     */
    public void setListViewHeightBasedOnChildren() {
        ListAdapter listAdapter = this.getAdapterListView().getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, this.getAdapterListView());
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = this.getAdapterListView().getLayoutParams();
        params.height = totalHeight
                + (this.getAdapterListView().getDividerHeight() * (listAdapter.getCount() - 1));
        this.getAdapterListView().setLayoutParams(params);
    }



    @Override
    public ViewEventListRow getItem(int position) {
        return rowUsers.get(position);
    }

}
