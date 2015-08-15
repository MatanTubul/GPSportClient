package com.example.matant.gpsportclient.Controllers;


import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.ErrorHandler;
import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener,AsyncResponse {

    EditText userNameEditText, passwordEditText;
    Button loginB, signUpB;
    DBcontroller dbController;
    TextView forgotPasswordTV;
    boolean userCanLogIn;
    private static final String TAG_FLG = "flag";
    private ErrorHandler err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbController =   new DBcontroller();
        dbController.delegate = this;
        userCanLogIn = false;
        err = new ErrorHandler();

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

    public void onClick (View v) {
        Intent i = null;
        switch(v.getId()) {

            case R.id.loginB:
                final String checkUserName = userNameEditText.getText().toString();
                final String checkPassword = passwordEditText.getText().toString();
                if (validateLoginFields(checkUserName, checkPassword) == true)
                    sendDataToDBController();
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

        ArrayList editTextArrayList = new ArrayList<EditText>();
        editTextArrayList.add(userNameEditText);
        editTextArrayList.add(userNameEditText);

        if (err.fieldIsEmpty(editTextArrayList,"Field can't be empty"))
            return false;
        if (!err.validateEmailAddress(userName))
            return false;

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
        dbController =  new DBcontroller();
        dbController.delegate = this;
        dbController.execute(nameValuePairList);

    }

    @Override
    public void handleResponse(String jsonStr) {

        Log.d("handleResponse", jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String flg = jsonObj.getString(TAG_FLG);

                switch(flg)
                {
                    case "user":
                        userNameEditText.setError("This user isn't exists");
                        break;
                    case "password":
                        passwordEditText.setError("This password is incorrect");
                        break;
                    case "already connected":
                        passwordEditText.setError("user already connected");//pdialog
                        break;
                    case "verified":
                        startActivity(new Intent(Login.this, MainScreen.class));
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ServiceHandler", "Couldn't get any data from the url");
        }


    }


}
