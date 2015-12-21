package com.example.matant.gpsportclient.Utilities;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matant on 8/13/2015.
 */
public class ErrorHandler {
    private Pattern regexPattern;
    private Matcher regMatcher;

    /**
     * validate if the input in the edittext is correct mail
     * @param emailAddress-input of the edittext field.
     * @return
     */
    public boolean validateEmailAddress(String emailAddress) {
        regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        regMatcher   = regexPattern.matcher(emailAddress);
        if(regMatcher.matches()){
            return true;
        } else {
            return false;
        }
    }


    /**
     * function that check each EdiText in the UI and notify if is empty.
      * @param et - array of all the edittext
     * @param message- error message that will appear in the relevant field.
     */
    public boolean fieldIsEmpty(ArrayList<EditText> et,String message){
        boolean flag = false;
        for(int i=0; i < et.size();i++)
            if (et.get(i).getText().toString().equals(""))
            {
                et.get(i).setError(message);
                if (flag == false)
                    flag = true;
            }
        return flag;

    }



}
