package com.example.matant.gpsportclient.Utilities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by matant on 9/7/2015.
 */
public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public  interface OnCompleteListener {
        public  void onComplete(String flag,String time);
    }

    private int Time_Picker ;
     static final int START_TIME = 1;
     static final int END_TIME = 2;
    private OnCompleteListener mListener;

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
        int hour = cal.get(Calendar.HOUR_OF_DAY);
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
        Log.d("Calendar",String.valueOf(hourOfDay)+":"+String.valueOf(minute));
        String s ="";

        if(Time_Picker == START_TIME)
        {
             s = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
            mListener.onComplete("start_time",s);
        }
        else
        {
            s = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
            mListener.onComplete("end_time",s);

        }

    }


}
