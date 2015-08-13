package com.example.matant.gpsportclient;

import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matant on 8/13/2015.
 */
public class ErrorHandler {
    private Pattern regexPattern;
    private Matcher regMatcher;

//validate if the input in the edittext is correct mail.
    public boolean validateEmailAddress(String emailAddress) {
        regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        regMatcher   = regexPattern.matcher(emailAddress);
        if(regMatcher.matches()){
            return true;
        } else {
            return false;
        }
    }
    //function that check each EdiText in the UI and notify if is empty.
    public  void fieldIsEmpty(ArrayList<EditText> et,String message){
        Log.i("function","fieldisEmpty");
        for(int i=0; i < et.size();i++) {
            Log.i("indes", String.valueOf(i));
            if (et.get(i).getText().toString().equals("")) {
                et.get(i).setError(message);
            }
        }

    }
}
