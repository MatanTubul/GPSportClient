package com.example.matant.gpsportclient;

import android.widget.EditText;

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
}
