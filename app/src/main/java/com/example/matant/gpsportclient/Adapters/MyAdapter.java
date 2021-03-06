package com.example.matant.gpsportclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.matant.gpsportclient.Controllers.Activities.SignUp;
import com.example.matant.gpsportclient.R;

/**
 * this class is a custom adapter for our spinner widgets
 * Created by matant on 9/10/2015.
 */
public class MyAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] values;

    public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
        super(ctx, txtViewResourceId, objects);
        context = ctx;
        values = objects;
    }
    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }
    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }
    public View getCustomView(int position, View convertView,ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View mySpinner = inflater.inflate(R.layout.custom_spinner, parent,
                false);
        TextView subSpinner = (TextView) mySpinner.findViewById(R.id.sub_text_seen);
        subSpinner.setText(values[position]);


        return mySpinner;
    }
}

