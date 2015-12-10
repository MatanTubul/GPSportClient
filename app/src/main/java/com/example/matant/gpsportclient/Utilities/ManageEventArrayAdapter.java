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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private String mode;
    public String mananger_name = null;
    private  String user_id = null;


    public ManageEventArrayAdapter(Context ctx,int resourceId, List<ManageEventListRow> items,String mymode){
        super(ctx, resourceId, items);
        this.context = ctx;
        this.mngEvents = items;
        this.mode = mymode;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
        ImageView mngImageview;
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
            holder.mngImageview = (ImageView) convertView.findViewById(R.id.imageViewManager);

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
        if(rowItem.isManager())
            holder.mngImageview.setImageResource(R.drawable.manager_32);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowEvent = rowItem;
                if(mode.equals("manage")){
                    Bundle bun = new Bundle();
                    bun.putString(Constants.TAG_REQUEST,Constants.MODE_UPDATE);
                    bun.putString("json",rowItem.getEventRecord().toString());
                    try {
                        bun.putString("users",rowItem.getEventRecord().getString("event_users"));
                        Log.d("users is:",rowItem.getEventRecord().getString("event_users"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final  Activity activity = (Activity) context;
                    Fragment fragment = new CreateEventFragmentController();
                    fragment.setArguments(bun);
                    FragmentManager fragmentManager =  activity.getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }
                else{
                    //need to start ViewEventFragmentController
                    Log.d("mode is:",mode );
                }


            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowEvent = rowItem;
                if(mode.equals("manage")){
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
                }else{
                    Log.d("mode is:", mode);
                    new AlertDialog.Builder(getContext())
                            .setTitle("Leave Event")
                            .setMessage("Are you sure you want to Leave the event?")
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
                        Toast.makeText(this.context, "Delete failed", Toast.LENGTH_SHORT).show();
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
    public void sendDataToDBController()   {
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        String event_id  = null,current_participant = null;
        try {
            event_id = rowEvent.getEventRecord().getString("event_id").toString();
            current_participant = rowEvent.getEventRecord().getString("current_participants").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BasicNameValuePair eventId = new BasicNameValuePair("event_id",event_id );
        nameValuePairList.add(eventId);

        if(mode.equals("manage")){
            BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "delete_event");
            BasicNameValuePair event_details = new BasicNameValuePair("event_details",rowEvent.getEventRecord().toString());
            BasicNameValuePair event_date = new BasicNameValuePair("event_date", rowEvent.getDate());
            BasicNameValuePair mng_name = new BasicNameValuePair("manager_name", this.mananger_name);

            String sport_t = null ,place = null ,s_time = null ,e_time = null;

            try {
                sport_t = rowEvent.getEventRecord().getString("kind_of_sport").toString();
                place =  rowEvent.getEventRecord().getString("address").toString();
                s_time = rowEvent.getEventRecord().getString("start_time").toString();
                e_time = rowEvent.getEventRecord().getString("end_time").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BasicNameValuePair sport_type = new BasicNameValuePair("kind_of_sport",sport_t );
            BasicNameValuePair event_place = new BasicNameValuePair("address",place );
            BasicNameValuePair event_s_time = new BasicNameValuePair("start_time",s_time );
            BasicNameValuePair event_e_time = new BasicNameValuePair("end_time",e_time );
            BasicNameValuePair curr_partici = new BasicNameValuePair("current_participants",current_participant);
           nameValuePairList.add(tagreq);
            nameValuePairList.add(event_date);
            nameValuePairList.add(event_details);
            nameValuePairList.add(mng_name);
            nameValuePairList.add(sport_type);
            nameValuePairList.add(event_place);
            nameValuePairList.add(event_s_time);
            nameValuePairList.add(event_e_time);
            nameValuePairList.add(curr_partici);
        }else{
            String event_type = null;
            BasicNameValuePair tagreq;
            try {
                 event_type  = rowEvent.getEventRecord().getString("private");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(rowEvent.isManager()){
                Log.d("delete manager",String.valueOf(rowEvent.isManager()));
                 tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "remove_event_manager");
            }else{
                Log.d("delete participant",String.valueOf(rowEvent.isManager()));
                 tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, "remove_participant");
            }
            BasicNameValuePair type = new BasicNameValuePair("event_is_private", event_type);
            BasicNameValuePair curr_partici = new BasicNameValuePair("current_participants",current_participant);
            if (user_id != null)
            {
                BasicNameValuePair userid = new BasicNameValuePair(Constants.TAG_USERID,user_id);
                nameValuePairList.add(userid);
            }
            nameValuePairList.add(curr_partici);
            nameValuePairList.add(tagreq);
            nameValuePairList.add(type);
        }
        dbController = new DBcontroller(getContext(),this);
        dbController.execute(nameValuePairList);

    }

    @Override
    public void preProcess() {
        this.progress = ProgressDialog.show(getContext(), "Delete Event!",
                "Deleting event...", true);

    }
}
