package com.example.matant.gpsportclient.Utilities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.matant.gpsportclient.InterfacesAndConstants.OnCompleteListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * class that presenting our custom TimePicker widget.
 * Created by matant on 9/7/2015.
 */
public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    private int Time_Picker ;
    static final int START_TIME = 1;
    static final int END_TIME = 2;
    private OnCompleteListener mListener;
    private int hour;
    public int test;
    private int start_hour = -1;
    private int end_hour = -1;
    private int start_min = -1;
    private int end_min = -1;
    private String chosen_date;
    private boolean datecompre;
    private int equation_type = 1;
    private int curr_minute;
    private Date start_date;
    private Date date1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            mListener =  (OnCompleteListener) getTargetFragment();
        }catch (ClassCastException e)
        {
            Log.d("Class casting Error",e.getMessage());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        final Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        curr_minute = cal.get(Calendar.MINUTE);
        Bundle bundle = this.getArguments();
        Log.d("Calendar",String.valueOf(hour)+":"+String.valueOf(curr_minute));
        String type = "";
        if(bundle!=null){
            type = bundle.getString("type");
            if(type.equals("search")){
                equation_type = Integer.valueOf(bundle.getString("equation"));
            }
            Time_Picker = Integer.valueOf(bundle.getString("val"));
            chosen_date = bundle.getString("date");

        }
        Log.d("chosen date",chosen_date);

        try{
            Date tmp = new Date();
            Log.d("current date",tmp.toString());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
             date1 = sdf.parse(chosen_date);

            Date date2 = new Date();
            start_date = date2;

            if(date1.after(date2)){
                datecompre = true;
            } else if(date1.before(date2)){
                datecompre = false;
            } else {
                datecompre = false;
            }

        }catch(ParseException ex){
            ex.printStackTrace();
        }
        switch (Time_Picker)
        {
            case START_TIME: {
                Time_Picker = START_TIME;
                start_date = date1;
                return new TimePickerDialog(getActivity(), this, hour, curr_minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
            }
            case END_TIME:
            {
                Time_Picker = END_TIME;
                Log.d("datecompare",String.valueOf(datecompre));
                return new TimePickerDialog(getActivity(), this, hour, curr_minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
            }
        }
        return null;
    }

    /**
     * execute when the user pick a time.
     * @param view - which view is related to
     * @param hourOfDay - user hour choice
     * @param minute - user minute choice
     */
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        String s ="";
        String min;
        Log.d("datecompare on set",String.valueOf(datecompre));
        if(minute < 10)
             min = "0"+Integer.toString(minute);
        else
            min = String.valueOf(minute);
        if (Time_Picker == START_TIME)
        {
            start_hour = hourOfDay;
            start_min = minute;
        }
        else{
            end_hour = hourOfDay;
            end_min = minute;
        }
        if(equation_type == 1)
        {
            if(datecompre == false){
                if(hour > hourOfDay){
                    mListener.onComplete("incorrect_time", "Please insert valid time");
                }
                else if((end_hour < start_hour && start_hour != -1 && end_hour != -1)  || (end_hour == start_hour && (start_min > end_min))){

                    this.end_hour = -1;
                    this.start_hour = -1;
                    mListener.onComplete("incorrect_time", "Please insert valid time");
                }else{
                    setMyTime(s,hourOfDay,min);
                }
            }
            else {
                if((end_hour < start_hour && start_hour != -1 && end_hour != -1)  || (end_hour == start_hour && (start_min > end_min))){
                    this.end_hour = -1;
                    this.start_hour = -1;
                    mListener.onComplete("incorrect_time", "Please insert valid time");
                }else{
                    setMyTime(s,hourOfDay,min);
                }
            }
        }else{
            Date curr_date = new Date();
            if(start_date.after(curr_date))
                setMyTime(s,hourOfDay,min);
            else{
                if((hour > hourOfDay || (hour == hourOfDay && curr_minute > minute)) && Time_Picker == START_TIME)
                {
                    this.end_hour = -1;
                    this.start_hour = -1;
                    mListener.onComplete("incorrect_time", "Please insert valid time");
                }
                else
                    setMyTime(s,hourOfDay,min);
            }
        }
    }

    public void setMyTime(String s,int hourOfDay,String min){
        Log.d("time picker on set",String.valueOf(Time_Picker));
        if (Time_Picker == START_TIME) {

            s = String.valueOf(hourOfDay) + ":" + min;
            Log.d("hour different","start="+start_hour+"end="+end_hour);
            mListener.onComplete("start_time", s);
        } else {
            Log.d("hour different","start="+start_hour+"end="+end_hour);
            s = String.valueOf(hourOfDay) + ":" + min;
            mListener.onComplete("end_time", s);

        }

    }




}
