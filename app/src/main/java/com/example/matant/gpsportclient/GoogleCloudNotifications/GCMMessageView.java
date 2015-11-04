package com.example.matant.gpsportclient.GoogleCloudNotifications;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GCMMessageView extends AppCompatActivity implements AsyncResponse, View.OnClickListener {
    private TextView message,date,time,user;
    private String intentUser,intentDate,intentTime,intentMessage,EventId = "";
    private DBcontroller dbController;
    private SessionManager sm;
    private String UserId = "";
    private ProgressDialog progress;
    private Constants cons;
    private Button btnJoin,btnDeny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcmmessage_view);
        message = (TextView) findViewById(R.id.message);
        date = (TextView) findViewById(R.id.textViewDate);
        time = (TextView) findViewById(R.id.textViewtime);
        user = (TextView) findViewById(R.id.textViewUser);
        btnJoin = (Button)findViewById(R.id.ButtonJoin);
        btnDeny = (Button)findViewById(R.id.ButtonDeny);


        Intent i = getIntent();
        intentUser = i.getExtras().getString("inviter");
        intentMessage = i.getExtras().getString("message");
        intentDate = i.getExtras().getString("date");
        intentTime = i.getExtras().getString("s_time") + " "+ "to"+" " + i.getExtras().getString("e_time");
        EventId = i.getExtras().getString("event_id");
        user.setText(intentUser);
        message.setText(intentMessage);
        date.setText(intentDate);
        time.setText(intentTime);


        sm = SessionManager.getInstance(this);
         UserId = sm.getUserDetails().get(sm.KEY_USERID);

        btnJoin.setOnClickListener(this);
        btnDeny.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gcmmessage_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleResponse(String resStr) {

        progress.dismiss();

        Log.d("handleResponse", resStr);
        if (resStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);

                switch (flg){
                    case "inserted":
                        break;
                    case "failed":

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendDataToDBController() {

        BasicNameValuePair tagreq = new BasicNameValuePair("tag", "response_invited_user");
        BasicNameValuePair event_Id = new BasicNameValuePair("event_id", EventId);
        BasicNameValuePair user_Id = new BasicNameValuePair("event_id", UserId);

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(event_Id);
        nameValuePairList.add(user_Id);
        dbController = new DBcontroller(this, this);
        dbController.execute(nameValuePairList);
    }

    @Override
    public void preProcess() {
        this.progress = ProgressDialog.show(this, "Join to Event",
                "Updating...", true);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.ButtonJoin:
                break;


            case R.id.ButtonDeny:
                break;
        }
    }
}
