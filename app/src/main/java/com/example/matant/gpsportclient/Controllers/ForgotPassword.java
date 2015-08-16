package com.example.matant.gpsportclient.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.ErrorHandler;
import com.example.matant.gpsportclient.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPassword extends Activity implements AsyncResponse {
    private Button forgotbtn;
    private EditText editxtemail;
    private Pattern regexPattern;
    private Matcher regMatcher;
    private ErrorHandler err;
    DBcontroller dbController;
    private static final String TAG_FLG = "flag";
    Session session = null;

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
                    editxtemail.setError("email is invalid");
                else
                    sendDataToDBController();
            }
        });


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
                        editxtemail.setError("This user isn't exists");
                        break;
                    case "recovered":
                        sendMail();
                        startActivity(new Intent(ForgotPassword.this, Login.class));
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ServiceHandler", "Couldn't get any data from the url");
        }
    }

    private void sendMail()
    {
        Log.d("sendMail", "sendMail");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("nirbercovic@gmail.com", "021663398ss");
            }
        });
        Log.d("sendMail", "connection was created");
                try {
                    final Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("nirbercovic@gmail.com"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("nirbercovic@gmail.com"));
                    message.setSubject("s");
                    message.setContent("sss", "text/html; charset=utf-8");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                           try{ Transport.send(message);}
                           catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    Log.d("sendMail", "email was sent");
                } catch (Exception e) {
                    e.printStackTrace();
                }




            }


            @Override
            public void sendDataToDBController() {

                String emailp = editxtemail.getText().toString();
                BasicNameValuePair tagreq = new BasicNameValuePair("tag", "forgotpassword");
                BasicNameValuePair emailparam = new BasicNameValuePair("email", emailp);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(tagreq);
                nameValuePairList.add(emailparam);
                dbController = new DBcontroller();
                dbController.delegate = this;
                dbController.execute(nameValuePairList);

            }
        }
