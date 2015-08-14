package com.example.matant.gpsportclient.Controllers;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.ErrorHandler;
import com.example.matant.gpsportclient.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassword extends Activity implements AsyncResponse {
    private Button forgotbtn;
    private EditText editxtemail;
    private Pattern regexPattern;
    private Matcher regMatcher;
    private ErrorHandler err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        editxtemail = (EditText)findViewById(R.id.editTextemail);
        forgotbtn = (Button)findViewById(R.id.buttonForgotP);
        err = new ErrorHandler();
        forgotbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!err.validateEmailAddress(editxtemail.getText().toString()))
                {
                    editxtemail.setError("email is invalid");
                }else
                {
                    sendDataToDBController();
                }
                editxtemail.setText("");



            }
        });
    }

    @Override
    public void handleResponse(String resStr) {

    }

    @Override
    public void sendDataToDBController() {

        String emailp = editxtemail.getText().toString();
        BasicNameValuePair tagreq = new BasicNameValuePair("tag","forgotpassword");
        BasicNameValuePair emailparam = new BasicNameValuePair("email",emailp);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagreq);
        nameValuePairList.add(emailparam);
        DBcontroller dbController =   new DBcontroller();
        dbController.delegate = this;
        dbController.execute(nameValuePairList);

    }
}
