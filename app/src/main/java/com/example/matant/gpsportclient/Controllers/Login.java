package com.example.matant.gpsportclient.Controllers;

import android.content.ContentValues;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener,AsyncResponse {

    EditText userNameEditText, passwordEditText;
    Button loginB, signUpB;
    DBcontroller dbController;
    TextView forgotPasswordTV;
    boolean userCanLogIn;
    private static final String TAG_FLG = "flag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbController =   new DBcontroller();
        dbController.delegate = this;
        userCanLogIn = false;

        userNameEditText=(EditText)findViewById(R.id.userNameTF);
        passwordEditText=(EditText)findViewById(R.id.passwordTF);
        forgotPasswordTV=(TextView)findViewById(R.id.forgotPasswordTV);

        loginB=(Button)findViewById(R.id.loginB);
        signUpB=(Button)findViewById(R.id.signUpB);

        loginB.setOnClickListener(this);
        signUpB.setOnClickListener(this);
        forgotPasswordTV.setPaintFlags(forgotPasswordTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPasswordTV.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void onClick (View v) {
        Intent i = null;
        switch(v.getId()) {

            case R.id.loginB:
                final String checkUserName = userNameEditText.getText().toString();
                final String checkPassword = passwordEditText.getText().toString();
                if (validateLoginFields(checkUserName, checkPassword) == true)
                {
                    sendDataToDBController();
                    if (userCanLogIn)
                        i = new Intent(Login.this, MainScreen.class);
                }
                break;
            case R.id.signUpB:
                i = new Intent(Login.this, SignUp.class);
                break;
            case R.id.forgotPasswordTV:
                i = new Intent(Login.this, ForgotPassword.class);
                break;
        }
      if (i!=null)
      {
          userNameEditText.setText("");
          passwordEditText.setText("");
          startActivity(i);
      }
    }

    private boolean validateLoginFields(String userName,String password)
    {
        if(userName.length()==0)
        {
            userNameEditText.requestFocus();
            userNameEditText.setError("Field can't be empty");
            return false;
        }
        if (!userName.matches("[a-zA-Z ]+"))
        {
            userNameEditText.requestFocus();
            userNameEditText.setError("Enter only alphabetical characters");
            return false;
        }
        if(password.length()==0)
        {
            passwordEditText.requestFocus();
            passwordEditText.setError("Field can't be empty");
            return false;
        }
        if(!password.matches("^[a-zA-Z0-9]+$"))
        {
            passwordEditText.requestFocus();
            passwordEditText.setError("Enter only numbers or alphabetical characters");
            return false;
        }
        return true;
    }

    public void sendDataToDBController()
    {
        String userNameP = userNameEditText.getText().toString();
        String passwordP = passwordEditText.getText().toString();
        BasicNameValuePair tagReq = new BasicNameValuePair("tag","login");
        BasicNameValuePair userNameParam = new BasicNameValuePair("username",userNameP);
        BasicNameValuePair passwordParam = new BasicNameValuePair("password",passwordP);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagReq);
        nameValuePairList.add(userNameParam);
        nameValuePairList.add(passwordParam);
        new DBcontroller().execute(nameValuePairList);

    }

    @Override
    public void handleResponse(String jsonStr) {

        Log.d("handleResponse", jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String flg = jsonObj.getString(TAG_FLG);
                if (flg.equals("user"))
                    userNameEditText.setError("This user isn't exists");
                else if (flg.equals("password"))
                    passwordEditText.setError("This password is incorrect");
                        else if (flg.equals("already connected"))
                            passwordEditText.setError("user already connected");
                        else
                            userCanLogIn = true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ServiceHandler", "Couldn't get any data from the url");
        }


    }


}
