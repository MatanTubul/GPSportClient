package com.example.matant.gpsportclient.Utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;

import com.example.matant.gpsportclient.InterfacesAndConstants.OnCompleteListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * class that presenting our custom DatePicker widget.
 * Created by matant on 9/10/2015.
 */
public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnCompleteListener mListener;
    private int year,month,day,DATE_PICKER = -1;
    private  Calendar cal;
    private int startDay = -1,startMonth = -1,startYear = -1,endMonth = -1,endDay = -1,endYear = -1;
    private int startCnt = 0,endCnt = 0; // counter which indicates if both start and end date choosed by the user.
    private Date startDate = null,endDate = null,currDate;


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
        Bundle bundle = this.getArguments();
        String type ="";
        if(bundle!=null){
            type = bundle.getString("date");
            if(type.equals("search"))
                DATE_PICKER = Integer.valueOf(bundle.getString("val"));
        }
        Log.d("date picker on create",String.valueOf(DATE_PICKER));

        if(DATE_PICKER != -1){
            switch (DATE_PICKER){
                case 1:
                    DATE_PICKER = 1;
                    break;
                case 2:
                    DATE_PICKER = 2;
                    break;
            }
        }


        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        pickerDialog.getDatePicker().setMinDate(minDate);
        return pickerDialog;

    }

    /**
     * execute when the user pick a date.
     * @param view - which view the widget is related to
     * @param year - the year that the user choose
     * @param monthOfYear - the month that the user choose
     * @param dayOfMonth - the day that the user choose
     */
    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        {
            if(DATE_PICKER == -1){
                Log.d("in mode","create");
                // this instance created by create fragment
                if( this.day > dayOfMonth && this.month == monthOfYear)
                {

                    mListener.onComplete("Date_not_valid","Please insert a valid date!");
                }else{

                    mListener.onComplete("date",String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year));
                }
            }else{
                Log.d("in mode","search");
                    String myDate = String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    currDate = new Date();
                    // this instance created by search fragment
                    if(DATE_PICKER == 1){ //this is start date
                        try {
                            Log.d("date picker",String.valueOf(DATE_PICKER));
                            startDate = sdf.parse(myDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else{ // this is end date
                        try {
                            Log.d("date picker",String.valueOf(DATE_PICKER));
                            endDate = sdf.parse(myDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if(startDate !=null && endDate !=null){
                        Log.d("Date picker message","both Date object not null");
                       if(startDate.after(endDate))
                       {
                           mListener.onComplete("Date_not_valid","Please insert a valid date!");
                           startDate = null;
                           endDate = null;
                       }else{
                           if(DATE_PICKER == 1)
                               mListener.onComplete("start_date",myDate);
                           else
                               mListener.onComplete("end_date", myDate);
                       }
                    }else{
                        if(DATE_PICKER == 1)
                            mListener.onComplete("start_date",myDate);
                        else
                            mListener.onComplete("end_date", myDate);
                    }
            }


        }

    }
}
