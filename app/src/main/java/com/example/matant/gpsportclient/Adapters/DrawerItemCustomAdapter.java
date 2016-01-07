package com.example.matant.gpsportclient.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.DataClasses.DrawerItem;

/**
 * Class that holding the view of the main menu including design and data.
 * Created by matant on 8/24/2015.
 */
public class DrawerItemCustomAdapter extends ArrayAdapter<DrawerItem> {

    Context mContext;
    int layoutResourceId;
    DrawerItem data[] = null;

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, DrawerItem[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);

        DrawerItem folder = data[position];


        imageViewIcon.setImageResource(folder.icon);
        textViewName.setText(folder.name);

        return listItem;
    }

}
