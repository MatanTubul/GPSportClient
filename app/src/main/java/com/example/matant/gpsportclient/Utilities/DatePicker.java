package com.example.matant.gpsportclient.Utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;

import com.example.matant.gpsportclient.InterfacesAndConstants.OnCompleteListener;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by matant on 9/10/2015.
 */
public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnCompleteListener mListener;
    private int year,month,day;
    private  Calendar cal;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            mListener =  (OnCompleteListener) getTargetFragment();
        }catch (ClassCastException e)
        {
            Log.d("Class casting Error", e.getMessage());
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date today = new Date();
          cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.MONTH,0);
        long minDate = cal.getTime().getTime();
        year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        pickerDialog.getDatePicker().setMinDate(minDate);
        return pickerDialog;

    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        {
            if( this.day > dayOfMonth)
            {

                mListener.onComplete("Date_not_valid","Please insert a valid date!");
            }else{

                mListener.onComplete("date",String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year));
            }

        }

    }
}
