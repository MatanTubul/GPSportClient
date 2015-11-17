package com.example.matant.gpsportclient.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.matant.gpsportclient.Controllers.Activities.Login;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.MainScreen;

import java.util.HashMap;

/**
 * This singleton class will manage the user session including get/store info by Shared Preferences
 * Created by matant on 8/27/2015.
 */
public class SessionManager  {

    private static SessionManager sessionManagerInstance = null;
    // Shared Preferences reference
    SharedPreferences pref;
    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    private boolean isConnected = false;

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
        pref = _context.getSharedPreferences(Constants.PREFER_NAME,Constants.PRIVATE_MODE);
        editor = pref.edit();
    }

    public void StoreUserSession(String val,String Key){
        editor.putString(Key,val);
        editor.commit();
    }

    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();

        user.put(Constants.TAG_EMAIL, pref.getString(Constants.TAG_EMAIL, null));
        user.put(Constants.TAG_NAME, pref.getString(Constants.TAG_NAME, null));
        user.put(Constants.TAG_MOB,pref.getString(Constants.TAG_MOB,null));
        user.put(Constants.TAG_PASS, pref.getString(Constants.TAG_PASS, null));
        user.put(Constants.TAG_GEN, pref.getString(Constants.TAG_GEN, null));
        user.put(Constants.TAG_USERID,pref.getString(Constants.TAG_USERID,null));
        user.put(Constants.TAG_AGE,pref.getString(Constants.TAG_AGE,null));
        user.put(Constants.TAG_IMG,pref.getString(Constants.TAG_IMG,null));
        user.put(Constants.TAG_REGID,pref.getString(Constants.TAG_REGID,null));
        return user;
    }

    public void logoutUser(){
        //clean pref
        editor.clear();
        editor.commit();
        this.isConnected = false;

        Intent i = new Intent(_context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isConnected() {
        return isConnected;
    }
    public void setIsConnected(boolean isConnected) {

        this.isConnected = isConnected;
    }
}
