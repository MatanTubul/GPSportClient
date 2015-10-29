package com.example.matant.gpsportclient.GoogleCloudNotifications;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.matant.gpsportclient.R;

public class GCMMessageView extends AppCompatActivity {
    private TextView message,date,time,user;
    private String intentUser,intentDate,intentTime,intentMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcmmessage_view);
        message = (TextView) findViewById(R.id.message);
        date = (TextView) findViewById(R.id.textViewDate);
        time = (TextView) findViewById(R.id.textViewtime);
        user = (TextView) findViewById(R.id.textViewUser);

        Intent i = getIntent();
        intentUser = i.getExtras().getString("inviter");
        intentMessage = i.getExtras().getString("message");
        intentDate = i.getExtras().getString("date");
        intentTime = i.getExtras().getString("s_time") + " "+ "to"+" " + i.getExtras().getString("e_time");
        user.setText(intentUser);
        message.setText(intentMessage);
        date.setText(intentDate);
        time.setText(intentTime);
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
}
