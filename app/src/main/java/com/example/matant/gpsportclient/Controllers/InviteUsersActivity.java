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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
    private InviteUsersArrayAdapter Useradapter;
    private ImageConvertor imgConvert;

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

                                InviteUsersListRow rowUser = new InviteUsersListRow(R.drawable.camera, R.drawable.add_user_50, name, mobile,profileImage);
                                rowUsers.add(rowUser);
                            }

                             Useradapter = new InviteUsersArrayAdapter(this,R.layout.invite_users_listview_row,rowUsers);
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
            }
            case R.id.ButtonSave:
            {
                if(Useradapter.getUsers().size() > 0)
                {
                    Intent i = getIntent();

                    JSONObject jsonObj = new JSONObject();
                    JSONArray jsonArr = new JSONArray();
                    for(int j = 0 ;j < Useradapter.getUsers().size();j++)
                    {

                        try {
                            Log.d("name",Useradapter.getUsers().get(j).getTitle());
                            Log.d("mobile",Useradapter.getUsers().get(j).getDesc());

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

                }


            }
            break;

        }

    }
}
