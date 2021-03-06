package com.example.matant.gpsportclient.GoogleCloudNotifications;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.matant.gpsportclient.Controllers.Activities.Login;
import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * class that create an instance of the push notification
 * with all the data that send by the GCM server
 */

public class GCMMessageView extends AppCompatActivity implements AsyncResponse, View.OnClickListener {
    private TextView message,date,time,user,location;
    private String intentUser,intentDate,intentTime,intentMessage,EventId = "",place,msg_type = null;
    private DBcontroller dbController;
    private SessionManager sm;
    private String UserId = "",user_status = null;
    private ProgressDialog progress;

    private Button btnJoin,btnDeny;
    private boolean is_coonected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcmmessage_view);
        message = (TextView) findViewById(R.id.message);
        date = (TextView) findViewById(R.id.textViewDate);
        time = (TextView) findViewById(R.id.textViewtime);
        user = (TextView) findViewById(R.id.textViewUser);
        location = (TextView) findViewById(R.id.textViewPlace);
        btnJoin = (Button)findViewById(R.id.ButtonJoin);
        btnDeny = (Button)findViewById(R.id.ButtonDeny);
        setTitle("GPSport Notification");
        sm = SessionManager.getInstance(this);
          is_coonected = sm.getUserDetails().get(Constants.TAG_CONNECTED).equals("false");


        Intent i = getIntent();
        intentUser = i.getExtras().getString("inviter");
        intentMessage = i.getExtras().getString("message");
        intentDate = i.getExtras().getString("date");
        intentTime = i.getExtras().getString("s_time") + " "+ "to"+" " + i.getExtras().getString("e_time");
        EventId = i.getExtras().getString("event_id");
        place = i.getExtras().getString("location");
        msg_type = i.getExtras().getString("msg_type");


        user.setText(intentUser+",");
        message.setText(intentMessage);
        date.setText(intentDate);
        time.setText(intentTime);
        location.setText(place);


        sm = SessionManager.getInstance(this);
         UserId = sm.getUserDetails().get(Constants.TAG_USERID);
        if(msg_type != null)
        {
            if(msg_type.equals("canceled")){
                btnJoin.setVisibility(View.GONE);
                btnDeny.setText("Close");
            }
        }


        btnJoin.setOnClickListener(this);
        btnDeny.setOnClickListener(this);


    }

    @Override
    public void handleResponse(String resStr) {

        progress.dismiss();

        Log.d("GCMMessageView handleResponse", resStr);
        if (resStr != null) {
            try {
                final boolean is_coonected = sm.getUserDetails().get(Constants.TAG_CONNECTED).equals("false");
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);

                switch (flg){
                    case "updated":{

                        Log.d("msg:",jsonObj.getString(Constants.TAG_MSG));
                        if(!is_coonected){
                            Constants.reloadApp(this,MainScreen.class);
                            finish();
                        }else{
                            Constants.reloadApp(this, Login.class);
                            finish();
                        }
                        break;
                    }

                    case "update_failed":
                        Log.d("msg:",jsonObj.getString(Constants.TAG_MSG));
                        break;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendDataToDBController() {

        BasicNameValuePair tagreq = new BasicNameValuePair(Constants.TAG_REQUEST, Constants.TAG_RES_INV_USR);
        BasicNameValuePair event_Id = new BasicNameValuePair(Constants.TAG_EVENT_ID, EventId);
        BasicNameValuePair user_Id = new BasicNameValuePair(Constants.TAG_USERID, UserId);
        BasicNameValuePair userstatus = new BasicNameValuePair(Constants.TAG_USER_STAT, user_status);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(event_Id);
        nameValuePairList.add(user_Id);
        nameValuePairList.add(userstatus);
        dbController = new DBcontroller(this, this);
        dbController.execute(nameValuePairList);
    }

    @Override
    public void preProcess() {
        this.progress = ProgressDialog.show(this, "Join to Event",
                "Updating...", true);

    }
    @Override
    public void onBackPressed(){

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.ButtonJoin:
                user_status = Constants.TAG_ATTEND;
                sendDataToDBController();
                break;


            case R.id.ButtonDeny:
                user_status = Constants.TAG_NOT_ATTEND;
                if(is_coonected) {
                    Intent i = new Intent(this, Login.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(this, MainScreen.class);
                    startActivity(i);
                }
                break;
        }
    }
}
