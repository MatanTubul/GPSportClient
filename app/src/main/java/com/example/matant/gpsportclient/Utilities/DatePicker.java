package com.example.matant.gpsportclient.Utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;

import com.example.matant.gpsportclient.InterfacesAndConstants.OnCompleteListener;

import java.util.Calendar;

/**
 * Created by matant on 9/10/2015.
 */
public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnCompleteListener mListener;


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
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        Log.d("Calendar",String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear)+"/"+String.valueOf(year));
        {
            mListener.onComplete("date",String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year));
        }

    }
}
