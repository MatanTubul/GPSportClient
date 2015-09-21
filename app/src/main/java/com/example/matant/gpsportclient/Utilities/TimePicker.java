package com.example.matant.gpsportclient.Utilities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.matant.gpsportclient.OnCompleteListener;

import java.util.Calendar;

/**
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
        int minute = cal.get(Calendar.MINUTE);
        Bundle bundle = this.getArguments();
        Log.d("Calendar",String.valueOf(hour)+":"+String.valueOf(minute));
        if(bundle!=null){
            Time_Picker = bundle.getInt("Time",1);
        }
        switch (Time_Picker)
        {
            case START_TIME: {
                Time_Picker = START_TIME;
                return new TimePickerDialog(getActivity(), this, hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
            }
            case END_TIME:
            {
                Time_Picker = END_TIME;
                return new TimePickerDialog(getActivity(), this, hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
            }
        }
        return null;
    }
    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        Log.d("Calendar", String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
        String s ="";
        String min;

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

            if((end_hour < start_hour && start_hour != -1 && end_hour != -1) || hour > hourOfDay || (end_hour == start_hour && (start_min > end_min)) ) {
                Log.d("time different","start="+start_hour+"end="+end_hour+"time_picker="+Time_Picker+"end_min="+end_hour+"s_min="+start_hour);

                this.end_hour = -1;
                this.start_hour = -1;
                mListener.onComplete("incorrect_time", "Please insert valid time");
            }
        else {

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




}
