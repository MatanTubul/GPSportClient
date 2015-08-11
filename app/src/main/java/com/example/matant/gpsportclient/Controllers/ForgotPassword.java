package com.example.matant.gpsportclient.Controllers;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.matant.gpsportclient.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassword extends Activity {
    private Button forgotbtn;
    private EditText editxtemail;
    private Pattern regexPattern;
    private Matcher regMatcher;
    private DBcontroller db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        editxtemail = (EditText)findViewById(R.id.editTextemail);
        forgotbtn = (Button)findViewById(R.id.buttonForgotP);
        forgotbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateEmailAddress(editxtemail.getText().toString()))
                {
                    editxtemail.setError("email is invalid");
                }else
                {
                    String emailp = editxtemail.getText().toString();
                    BasicNameValuePair tagreq = new BasicNameValuePair("tag","forgotpassword");
                    BasicNameValuePair emailparam = new BasicNameValuePair("email",emailp);
                    List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                    nameValuePairList.add(tagreq);
                    nameValuePairList.add(emailparam);
                   new DBcontroller().execute(nameValuePairList);

                }
                editxtemail.setText("");



            }
        });
    }
    public boolean validateEmailAddress(String emailAddress) {

        regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        regMatcher   = regexPattern.matcher(emailAddress);
        if(regMatcher.matches()){
            return true;
        } else {
            return false;
        }
    }


}
