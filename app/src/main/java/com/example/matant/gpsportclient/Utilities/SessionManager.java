package com.example.matant.gpsportclient.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.matant.gpsportclient.Controllers.Login;

import java.util.HashMap;

/**
 * This singleton class will manage the user session including get/store info by Shared Preferences
 * Created by matant on 8/27/2015.
 */
public class SessionManager {

    private static SessionManager sessionManagerInstance = null;
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
    public  final String KEY_EMAIL = "email";
    public  String KEY_NAME = "name";
    public final String KEY_MOBILE = "mobile";
    public final String KEY_USERID = "user_id";
    public final String KEY_REGID = "reg_id";

    public static SessionManager getInstance(Context context){
        if(sessionManagerInstance == null)
        {
            sessionManagerInstance = new SessionManager(context);
        }
        return sessionManagerInstance;
    }

    private SessionManager(Context context)
    {
        this._context=context;
        pref = _context.getSharedPreferences(PREFER_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void StoreUserSession(String val,String Key){
        editor.putString(Key,val);
        editor.commit();
    }

    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();

        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_MOBILE,pref.getString(KEY_MOBILE,null));
        user.put(KEY_USERID,pref.getString(KEY_USERID,null));
        user.put(KEY_REGID,pref.getString(KEY_REGID,null));
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
