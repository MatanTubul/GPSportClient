package com.example.matant.gpsportclient.Utilities;

import java.util.Calendar;

/**
 * Created by matant on 12/18/2015.
 */
public class DateAndTimeFunctions {

    private Calendar cal;
    private String current_time,current_date;
    public DateAndTimeFunctions(){
        cal = Calendar.getInstance();
    }

    /**
     * return the current time as string
     * @return -current time
     */
    public String getCorrentTime()
    {
        String min = "";

        if(cal.get(Calendar.MINUTE)<10)
            min = "0"+String.valueOf(cal.get(Calendar.MINUTE));
        else
            min = String.valueOf(cal.get(Calendar.MINUTE));

        current_time = cal.get(Calendar.HOUR_OF_DAY)+":"+min;
        return current_time;
    }

    /**
     * return the current date
     * @return current date
     */
    public  String getCurrentDate()
    {

        current_date = cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
        return current_date;
    }

}
