package com.example.matant.gpsportclient.Controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.ImageConvertor;
import com.example.matant.gpsportclient.Utilities.InviteUsersArrayAdapter;
import com.example.matant.gpsportclient.Utilities.InviteUsersListRow;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;

public class InviteUsersActivity extends AppCompatActivity implements AsyncResponse, View.OnClickListener{
    private EditText editTextSearch;
    private Button btnSave,btnDiscard;
    private ListView usersListView;
    private List<InviteUsersListRow> rowUser;
    private DBcontroller dbController;
    public static final String EXTRA_USERS  = "";
    ListView listViewUsers;
    List<InviteUsersListRow> rowUsers;
    private InviteUsersArrayAdapter Useradapter = null;
    int  imgStatus = -1;


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
                if(editTextSearch.getText().length() > 0) {
                    Log.d("send request","searching...");
                    sendDataToDBController();
                }
                else{
                    Log.d("do nothing","doing nothing");
                   if(Useradapter != null) {
                       Useradapter.clear();
                       Useradapter.notifyDataSetChanged();
                   }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnDiscard.setOnClickListener(this);
        listViewUsers = (ListView) findViewById(R.id.listViewusers);
        listViewUsers.setItemsCanFocus(true);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void handleResponse(String resStr) {
        Log.d("invite_Response", resStr);
        if (resStr != null) {
            try {

                JSONObject json = new JSONObject(resStr);
                String flg = json.getString("flag");

                Log.d("flag",flg);
                switch (flg){

                    case "user found":{
                        JSONArray jsonarr = json.getJSONArray("users");
                        Log.d("array",jsonarr.toString());
                        rowUsers = new ArrayList<InviteUsersListRow>();
                        for(int i = 0; i < jsonarr.length();i++){
                            {
                                Log.d("user is", jsonarr.getJSONObject(i).toString());
                                String name = jsonarr.getJSONObject(i).getString("name");
                                String mobile = jsonarr.getJSONObject(i).getString("mobile");
                                Bitmap profileImage = ImageConvertor.decodeBase64(jsonarr.getJSONObject(i).getString("image"));

                                if(Useradapter != null){
                                    for(int j = 0 ;j<Useradapter.getUsers().size();j++)
                                    {
                                        if(Useradapter.getUsers().get(j).getDesc().equals(mobile))
                                        {
                                             imgStatus = R.drawable.remove_user_50;
                                            Log.d("set status2",String.valueOf(imgStatus));
                                        }/*else{
                                            imgStatus = R.drawable.add_user_50;
                                            Log.d("set status2",String.valueOf(imgStatus));
                                        }*/
                                    }
                                }else{

                                    imgStatus = R.drawable.add_user_50;
                                    Log.d("set status1",String.valueOf(imgStatus));
                                }
                                InviteUsersListRow rowUser = new InviteUsersListRow(imgStatus, name, mobile,profileImage);
                                rowUsers.add(rowUser);
                            }
                            if(Useradapter == null)
                                Useradapter = new InviteUsersArrayAdapter(this,R.layout.invite_users_listview_row,rowUsers);
                            else {
                                {
                                    Useradapter.setData(rowUsers);
                                    Useradapter.notifyDataSetChanged();
                                }
                            }
                            listViewUsers.setAdapter(Useradapter);

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
                break;
            }

            case R.id.ButtonSave:
            {

                if(Useradapter != null )
                {
                    if(Useradapter.getUsers().size() > 0)
                    {
                        Log.d("adapter size:",String.valueOf(Useradapter.getUsers().size()));
                        Intent i = getIntent();


                        JSONArray jsonArr = new JSONArray();
                        for(int j = 0 ;j < Useradapter.getUsers().size();j++)
                        {

                            try {
                                JSONObject jsonObj = new JSONObject();
                                jsonObj.put("name",Useradapter.getUsers().get(j).getTitle());
                                jsonObj.put("mobile",Useradapter.getUsers().get(j).getDesc());
                                jsonArr.put(jsonObj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        Log.d("json users",jsonArr.toString());
                        i.putExtra("userList", jsonArr.toString());
                        setResult(RESULT_OK, i);
                        finish();
                    }
                    else{
                        Log.d("adapter size <0", "search is empty");
                        Toast.makeText(this, "Please select user!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }

    }
}