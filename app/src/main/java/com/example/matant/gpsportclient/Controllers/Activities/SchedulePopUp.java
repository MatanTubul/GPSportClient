package com.example.matant.gpsportclient.Controllers.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.MyAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SchedulePopUp extends Activity implements View.OnClickListener {
    private RadioGroup rdg;
    private RadioButton rbYear,rdDate,rdEventCounter;
    private EditText editTextEventNumber;
    private Spinner spinnerRepeat,spinnerduration;
    private Button save,cancel;
    private int pos;

    private int year;
    private int month;
    private int day;

    static final int DATE_PICKER_ID = 1111;
    private  Calendar c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_pop_up);

        rdg = (RadioGroup)findViewById(R.id.radioGroup);
        rbYear = (RadioButton)findViewById(R.id.radioButtonYear);
        rdDate = (RadioButton)findViewById(R.id.radioButtonInDate);
        rdDate = (RadioButton)findViewById(R.id.radioButtonEvents);
        save = (Button)findViewById(R.id.ButtonSchedSave);
        spinnerduration = (Spinner) findViewById(R.id.spinnerDuration);
        ArrayList<String> weeks = new ArrayList<String>();
        for (int i = 0; i <= Constants.MAXIMUM_WEEKS; i++) {
            weeks.add(Integer.toString(i));

        }
        String[] weeks_array = weeks.toArray(new String[weeks.size()]);
        spinnerduration.setAdapter(new MyAdapter(this, R.layout.custom_spinner, weeks_array));
        spinnerduration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /*TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }*/
            }
        });






        spinnerRepeat = (Spinner) findViewById(R.id.spinnerRepeat);
        spinnerRepeat.setAdapter(new MyAdapter(this,R.layout.custom_spinner,getResources().getStringArray(R.array.event_repeats)));
        spinnerRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               /*TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }*/
            }
        });
        cancel = (Button) findViewById(R.id.ButtonSchedCancel);
        editTextEventNumber = (EditText)findViewById(R.id.editTextEventsNumber);
        editTextEventNumber.setVisibility(View.GONE);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));
        Date today = new Date();
        c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.MONTH, 0);
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        rdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                pos = rdg.indexOfChild(findViewById(checkedId));
                Log.d("position is", String.valueOf(pos));

                switch (pos)
                {
                    case 0:
                    {
                        editTextEventNumber.setVisibility(View.GONE);
                        break;
                    }
                    case 1:
                    {
                        editTextEventNumber.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 3:
                    {
                        editTextEventNumber.setVisibility(View.GONE);
                        showDialog(DATE_PICKER_ID);
                        break;
                    }
                }

            }
        });

    }
    public Dialog onCreateDialog(int id) {
        switch (id){
            case DATE_PICKER_ID:{
                Date today = new Date();
                c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.MONTH,0);
                long minDate = c.getTime().getTime();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog pickerDialog = new DatePickerDialog(this, pickListener, year, month, day);
                pickerDialog.getDatePicker().setMinDate(minDate);
                return pickerDialog;

            }
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener pickListener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(android.widget.DatePicker view, int selectedYear, int monthOfYear, int dayOfMonth) {
            year  = selectedYear;
            month = monthOfYear;
            day   = dayOfMonth;
        }
    };

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId())
        {
            case R.id.ButtonSchedSave:
                /* i = getIntent();
                //insert data

                setResult(RESULT_OK,i);
                finish();
                break;*/
            case R.id.ButtonSchedCancel:
                 i = getIntent();
                setResult(RESULT_CANCELED,i);
                finish();

                break;
        }
    }


}
