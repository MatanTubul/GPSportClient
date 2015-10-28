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



import com.example.matant.gpsportclient.Controllers.Login;
import com.example.matant.gpsportclient.GoogleCloudNotifications.GCMIntentService;
import com.google.android.gcm.GCMRegistrar;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


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
                    .setIconAttribute(android.R.attr.alertDialogIcon)
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
                    Intent i = new Intent(SplashScreen.this, Login.class);
                    startActivity(i);

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