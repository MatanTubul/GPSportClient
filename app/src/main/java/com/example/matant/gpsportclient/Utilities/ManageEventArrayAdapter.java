package com.example.matant.gpsportclient.Utilities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.Controllers.Fragments.CreateEventFragmentController;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * this class define tha adapter of the event management and handling
 * all the event delete requests.
 * Created by matant on 11/4/2015.
 */
public class ManageEventArrayAdapter extends ArrayAdapter<ManageEventListRow> implements AsyncResponse {
    Context context;
    List<ManageEventListRow> mngEvents;
    private DBcontroller dbController;
    private ManageEventListRow rowEvent;
    private ProgressDialog progress;

    public ManageEventArrayAdapter(Context ctx,int resourceId, List<ManageEventListRow> items){
        super(ctx, resourceId, items);
        this.context = ctx;
        this.mngEvents = items;
    }
    /*private view holder class*/
    private class ViewHolder {
        ImageView imageViewType;
        TextView txtSport;
        TextView txtDate;
        TextView txtLoc;
        TextView txtTime;
        TextView txtParticipants;
        ImageButton imgDelete;
    }
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        //final  View vi;

        final ManageEventListRow rowItem = getItem(position);
        rowEvent = rowItem;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.manage_event_listview_event, null);
            holder = new ViewHolder();
            holder.txtSport =  (TextView) convertView.findViewById(R.id.textViewTitle);
            holder.txtDate = (TextView) convertView.findViewById(R.id.textViewDateValue);
            holder.txtLoc = (TextView) convertView.findViewById(R.id.textViewLocationval);
            holder.txtTime = (TextView) convertView.findViewById(R.id.textViewtimeval);
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
        holder.txtTime.setText(rowItem.getEtime());
        holder.txtParticipants.setText(rowItem.getParticipants());
        holder.imageViewType.setImageResource(rowItem.getSportImage());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Fragment fragment = new CreateEventFragmentController();
                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();*/


                Bundle args = new Bundle();
                args.putString("Tag", "edit_event");
                args.putString("json", rowItem.getEventRecord().toString());

            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Event Id is:", rowItem.getEventId());
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Event")
                        .setMessage("Are you sure you want to delete this event?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendDataToDBController();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .show();

            }
        });

        return convertView;
    }



    public void setData(List<ManageEventListRow> list){
        this.mngEvents = list;
    }

    @Override
    public void handleResponse(String resStr) {
        progress.dismiss();
        Log.d("Delete Event handleResponse", resStr);
        if (resStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);
                switch (flg){
                    case Constants.TAG_REQUEST_FAILED:
                    {
                        break;
                    }
                    case Constants.TAG_REQUEST_SUCCEED:
                    {
                        mngEvents.remove(rowEvent);
                        notifyDataSetChanged();
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void sendDataToDBController() {
        String id = rowEvent.getEventId();
        Log.d("event id to delete:",id);
        BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "delete_event");
        BasicNameValuePair eventparam = new BasicNameValuePair("event_id", id);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(eventparam);
        dbController = new DBcontroller(getContext(),this);
        dbController.execute(nameValuePairList);

    }

    @Override
    public void preProcess() {
        this.progress = ProgressDialog.show(getContext(), "Delete Event!",
                "Deleting event...", true);

    }
}
