package com.example.matant.gpsportclient;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;


import com.example.matant.gpsportclient.Controllers.Activities.Login;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.Utilities.SessionManager;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sm = SessionManager.getInstance(this);
        final boolean is_coonected = sm.getUserDetails().get(Constants.TAG_CONNECTED).equals("false");
        Log.d("is connected",String.valueOf(is_coonected));
        Log.d("pref value",sm.getUserDetails().get(Constants.TAG_CONNECTED).toString());


        if (!isNetworkAvailable()) {
            new AlertDialog.Builder(this)
                    .setTitle("Connection Error")
                    .setMessage("Internet seems to be broken")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                              int pid = android.os.Process.myPid();
                              android.os.Process.killProcess(pid);
                              System.exit(0);
                        }
                    })
                    .setIcon(R.drawable.error_32)
                    .show();

        }else {

            new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity

                    if(is_coonected) {
                        Intent i = new Intent(SplashScreen.this, Login.class);
                        startActivity(i);
                    }else{
                        Intent i = new Intent(SplashScreen.this, MainScreen.class);
                        startActivity(i);
                    }

                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    /**
     * function that check if the internet services is available in case not application will be terminated
     * @return false-in case internet is not available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected =  activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        return  isConnected;
    }
}