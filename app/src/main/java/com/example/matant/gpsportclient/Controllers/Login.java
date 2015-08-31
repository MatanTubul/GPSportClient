package com.example.matant.gpsportclient.Controllers;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.SessionManager;

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
    private ProgressDialog progress;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userCanLogIn = false;
        err = new ErrorHandler();


        userNameEditText=(EditText)findViewById(R.id.userNameTF);
        passwordEditText=(EditText)findViewById(R.id.passwordTF);
        forgotPasswordTV=(TextView)findViewById(R.id.forgotPasswordTV);

        loginB=(Button)findViewById(R.id.loginB);
        signUpB=(Button)findViewById(R.id.signUpB);

        sm = new SessionManager(this);

        loginB.setOnClickListener(this);
        signUpB.setOnClickListener(this);
        forgotPasswordTV.setPaintFlags(forgotPasswordTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPasswordTV.setOnClickListener(this);
    }

    public void onClick (View v) {
        Intent i = null;
        switch(v.getId()) {

            case R.id.loginB:
                if (validateLoginFields() == true)
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
           startActivity(i);

    }

    private boolean validateLoginFields()
    {

        ArrayList editTextArrayList = new ArrayList<EditText>();
        editTextArrayList.add(userNameEditText);
        editTextArrayList.add(passwordEditText);

        if (err.fieldIsEmpty(editTextArrayList,"Field can't be empty"))
            return false;
        if (!err.validateEmailAddress(userNameEditText.getText().toString())) {
            userNameEditText.setError("email is invalid");
            return false;
        }
        return true;
    }

    public void sendDataToDBController()
    {
        sm.StoreUserSession(userNameEditText.getText().toString());
        String userNameP = userNameEditText.getText().toString();
        String passwordP = passwordEditText.getText().toString();
        BasicNameValuePair tagReq = new BasicNameValuePair("tag","login");
        BasicNameValuePair userNameParam = new BasicNameValuePair("username",userNameP);
        BasicNameValuePair passwordParam = new BasicNameValuePair("password",passwordP);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagReq);
        nameValuePairList.add(userNameParam);
        nameValuePairList.add(passwordParam);
        dbController =  new DBcontroller(this,this);

        dbController.execute(nameValuePairList);

    }

    @Override
    public void preProcess() {
            progress = ProgressDialog.show(this, "Login",
                    "Please wait while the login", true);

        }



    @Override
    public void handleResponse(String jsonStr) {
        progress.dismiss();

        Log.d("handleResponse", jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String flg = jsonObj.getString(TAG_FLG);

                switch(flg)
                {
                    case "User was not found":
                        userNameEditText.setError("This user isn't exists");
                        break;
                    case "User/Password is incorrect":
                        passwordEditText.setError("This password is incorrect");
                        break;
                    case "already connected":
                        passwordEditText.setError("user already connected");//pdialog
                        break;
                    case "verified":
                        //initialize session manager
                        SharedPreferences sessionManager =getSharedPreferences("Session", Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed = sessionManager.edit();
                        ed.putString("username", userNameEditText.getText().toString());
                        ed.commit();
                        ///

                        startActivity(new Intent(Login.this, MainScreen.class));
                        finish();
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ServiceHandler", "Couldn't get any data from the url");
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Please log out!", Toast.LENGTH_LONG);
    }
}
