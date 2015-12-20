package com.example.matant.gpsportclient.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

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

    public static SessionManager getInstance(Context context){
        if(sessionManagerInstance == null)
        {
            sessionManagerInstance = new SessionManager(context);
        }
        return sessionManagerInstance;
    }

    private SessionManager(Context context)
    {
        Log.d("new pref","this is new pref");
        this._context=context;
        pref = _context.getSharedPreferences(Constants.PREFER_NAME, Constants.PRIVATE_MODE);
        editor = pref.edit();
        if(getSearchIndex() <= 0 )
                    storeIndex(5);
        if(getUserDetails().get(Constants.TAG_CONNECTED) == null)
            StoreUserSession("false", Constants.TAG_CONNECTED);
    }

    public void StoreUserSession(String val,String Key ){

        editor.putString(Key, val);
        editor.commit();
    }
    public int getSearchIndex(){
        int idx = pref.getInt(Constants.TAG_SEARCH_INDEX,0);
        return  idx;
    }
    public void storeIndex(int num){
        editor.putInt(Constants.TAG_SEARCH_INDEX,num);
        editor.commit();
    }
    public HashMap<String,String> getRecentSearches(){
        HashMap<String,String> rs = new HashMap<String,String>();
        rs.put(Constants.TAG_SEARCH1,pref.getString(Constants.TAG_SEARCH1,null));
        rs.put(Constants.TAG_SEARCH2,pref.getString(Constants.TAG_SEARCH2,null));
        rs.put(Constants.TAG_SEARCH3,pref.getString(Constants.TAG_SEARCH3,null));
        rs.put(Constants.TAG_SEARCH4,pref.getString(Constants.TAG_SEARCH4,null));
        rs.put(Constants.TAG_SEARCH5,pref.getString(Constants.TAG_SEARCH5,null));
        return rs;
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
        user.put(Constants.TAG_CONNECTED,pref.getString(Constants.TAG_CONNECTED,null));

        return user;
    }

    public void logoutUser(){
        //clean pref
        //editor.clear();
        //editor.commit();
        //this.isConnected = false;
        StoreUserSession("false", Constants.TAG_CONNECTED);
        editor.commit();
        editor.apply();
        Log.d("user connection",getUserDetails().get(Constants.TAG_CONNECTED).toString());
        Intent i = new Intent(_context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }



}
