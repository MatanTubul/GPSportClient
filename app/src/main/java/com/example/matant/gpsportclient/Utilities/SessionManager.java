package com.example.matant.gpsportclient.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.matant.gpsportclient.Controllers.Login;

import java.util.HashMap;

/**
 * This class will manage the user session including get/store info by Shared Preferences
 * Created by matant on 8/27/2015.
 */
public class SessionManager {

    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;
    private static final String PREFER_NAME = "Session";
    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public  SessionManager(Context context)
    {
        this._context=context;
        pref = _context.getSharedPreferences(PREFER_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void StoreUserSession(String Email){
        editor.putString(KEY_EMAIL,Email);
        editor.commit();
    }

    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();

        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        return user;
    }

    public void logoutUser(){
        //clean pref
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }
}