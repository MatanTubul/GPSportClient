package com.example.matant.gpsportclient.Controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.InviteUsersListRow;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InviteUsersActivity extends AppCompatActivity implements AsyncResponse, View.OnClickListener {
    private EditText editTextSearch;
    private Button btnSave,btnDiscard;
    private ListView usersListView;
    private List<InviteUsersListRow> rowUser;
    private DBcontroller dbController;
    public static final String EXTRA_USERS  = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_users);

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        btnSave = (Button) findViewById(R.id.ButtonSave);
        btnDiscard = (Button)findViewById(R.id.ButtonDiscard);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendDataToDBController();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnDiscard.setOnClickListener(this);

    }

    @Override
    public void handleResponse(String resStr) {
        Log.d("invite_Response", resStr);
        if (resStr != null) {
            try {
               /* JSONArray jsonarr = new JSONArray(resStr);
                Log.d("check",jsonarr.getJSONObject(0).toString());
                JSONObject jsonObj = jsonarr.getJSONObject(0);
                String flg = jsonObj.getString("flag");*/
                JSONObject json = new JSONObject(resStr);
                String flg = json.getString("flag");

                Log.d("flag",flg);
                switch (flg){

                    case "user found":{
                        JSONArray jsonarr = json.getJSONArray("users");
                        Log.d("array",jsonarr.toString());
                        for(int i = 0; i < jsonarr.length();i++){
                            Log.d("user is",jsonarr.getJSONObject(i).toString());
                        }
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else
             Log.d("ServiceHandler", "Couldn't get any data from the url");
    }

    @Override
    public void sendDataToDBController() {

        String username = editTextSearch.getText().toString();
        BasicNameValuePair tagreq = new BasicNameValuePair("tag", "search_user");
        BasicNameValuePair name = new BasicNameValuePair("name", username);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(name);
        dbController = new DBcontroller(this,this);
        dbController.execute(nameValuePairList);

    }

    @Override
    public void preProcess() {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.ButtonDiscard:
            {
                Intent i = new Intent();
                setResult(RESULT_CANCELED,i);;
                finish();
            }
        }

    }
}
