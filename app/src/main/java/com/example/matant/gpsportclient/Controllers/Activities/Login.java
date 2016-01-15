package com.example.matant.gpsportclient.Controllers.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.GoogleCloudNotifications.GCMIntentService;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.google.android.gcm.GCMRegistrar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Login Screen- boot in case the user did not logged in
 * handling the Login request
 */

public class Login extends AppCompatActivity implements View.OnClickListener,AsyncResponse {

    EditText userNameEditText, passwordEditText;
    Button loginB, signUpB;
    DBcontroller dbController;
    TextView forgotPasswordTV;
    TextView errorTV;
    boolean userCanLogIn;
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
        userNameEditText.setOnClickListener(this);
        passwordEditText=(EditText)findViewById(R.id.passwordTF);
        passwordEditText.setOnClickListener(this);
        forgotPasswordTV=(TextView)findViewById(R.id.forgotPasswordTV);
        errorTV = (TextView)findViewById(R.id.errorText);
        loginB=(Button)findViewById(R.id.loginB);
        signUpB=(Button)findViewById(R.id.signUpB);

        sm = SessionManager.getInstance(this);

        loginB.setOnClickListener(this);
        signUpB.setOnClickListener(this);
        forgotPasswordTV.setPaintFlags(forgotPasswordTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPasswordTV.setOnClickListener(this);
        GCMRegistrar.register(Login.this, GCMIntentService.SENDER_ID);
    }

    public void onClick (View v) {
        Intent i = null;
        clearErrors();
        switch(v.getId()) {
            case R.id.loginB:
                if (validateLoginFields() == true)
                    sendDataToDBController();
                break;
            case R.id.signUpB:
                i = new Intent(Login.this, SignUp.class);
                finish();
                break;
            case R.id.forgotPasswordTV:
                i = new Intent(Login.this, ForgotPassword.class);
                finish();
                break;
        }
      if (i!=null)
           startActivity(i);

    }

    private void clearErrors()
    {
        userNameEditText.setError(null);
        passwordEditText.setError(null);
        errorTV.setVisibility(View.GONE);

    }

    private boolean validateLoginFields()
    {

        ArrayList editTextArrayList = new ArrayList<EditText>();
        editTextArrayList.add(userNameEditText);
        editTextArrayList.add(passwordEditText);

        if (err.fieldIsEmpty(editTextArrayList,"Field can't be empty"))
            return false;
        if (!err.validateEmailAddress(userNameEditText.getText().toString())) {
            errorTV.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    public void sendDataToDBController()
    {
        sm.StoreUserSession(userNameEditText.getText().toString(),Constants.TAG_EMAIL);
        String userNameP = userNameEditText.getText().toString();
        String passwordP = passwordEditText.getText().toString();
        BasicNameValuePair tagReq = new BasicNameValuePair("tag","login");
        BasicNameValuePair userNameParam = new BasicNameValuePair("username",userNameP);
        BasicNameValuePair passwordParam = new BasicNameValuePair("password",passwordP);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagReq);
        nameValuePairList.add(userNameParam);
        nameValuePairList.add(passwordParam);
        Log.d("sending login req", "login");
        dbController =  new DBcontroller(this,this);
        dbController.execute(nameValuePairList);

    }

    @Override
    public void preProcess() {
            progress = ProgressDialog.show(this, "Login",
                    "Please wait while the login", true);

        }
    public void onStop(){
        super.onStop();
        if(progress != null){
            progress.dismiss();
        }
    }



    @Override
    public void handleResponse(String jsonStr) {
        progress.dismiss();


        if (jsonStr != null) {
            Log.d("handleResponse", jsonStr);
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);

                switch(flg)
                {

                    case "User was not found":
                        errorTV.setVisibility(View.VISIBLE);
                        errorTV.setText("User/Password is incorrect");
                        break;
                    case "Password is incorrect":
                        errorTV.setVisibility(View.VISIBLE);
                        errorTV.setText("User/Password is incorrect");
                        break;
                    case "already connected":
                        errorTV.setVisibility(View.VISIBLE);
                        errorTV.setText("user already connected");
                        break;
                    case "verified":
                        sm.StoreUserSession(jsonObj.getString("name"),Constants.TAG_NAME);
                        sm.StoreUserSession(jsonObj.getString("mobile"),Constants.TAG_MOB);
                        sm.StoreUserSession(jsonObj.getString("password"),Constants.TAG_PASS);
                        sm.StoreUserSession(jsonObj.getString("gender"),Constants.TAG_GEN);
                        sm.StoreUserSession(jsonObj.getString("user_id"), Constants.TAG_USERID);
                        sm.StoreUserSession(jsonObj.getString("age"),Constants.TAG_AGE);
                        sm.StoreUserSession(jsonObj.getString("image"),Constants.TAG_IMG);
                        sm.StoreUserSession(jsonObj.getString("gcm_id"),Constants.TAG_REGID);
                        sm.StoreUserSession("true",Constants.TAG_CONNECTED);
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
}
