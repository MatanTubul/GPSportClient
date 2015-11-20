package com.example.matant.gpsportclient.Controllers.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String formated_date;

    static final int DATE_PICKER_ID = 1111;
    private  Calendar c;
    private JSONObject jsonObj;
    final Context context = this;
    private JSONObject jobj;
    private JSONArray jsonarr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_pop_up);

        rdg = (RadioGroup)findViewById(R.id.radioGroup);
        rbYear = (RadioButton)findViewById(R.id.radioButtonYear);
        rdDate = (RadioButton)findViewById(R.id.radioButtonInDate);
        rdDate = (RadioButton)findViewById(R.id.radioButtonEvents);
        save = (Button)findViewById(R.id.ButtonSchedSave);
        cancel = (Button) findViewById(R.id.ButtonSchedCancel);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
         jsonObj = new JSONObject();
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
        spinnerRepeat.setAdapter(new MyAdapter(this, R.layout.custom_spinner, getResources().getStringArray(R.array.event_repeats)));
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
        jobj = new JSONObject();
        jsonarr = new JSONArray();
        try {
            jobj.put(Constants.TAG_REQUEST,"Year");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonarr.put(jobj);
        try {
            jsonObj.put("radio_group",jsonarr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        rdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                pos = rdg.indexOfChild(findViewById(checkedId));
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
            if(dayOfMonth < day || monthOfYear < month || selectedYear < year){
                new AlertDialog.Builder(context)
                        .setTitle("Date Error!")
                        .setMessage("Date is not valid! Please select another date.")
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.error_32)
                        .show();
            }else{
                year  = selectedYear;
                month = monthOfYear;
                day   = dayOfMonth;
                formated_date = year+"-"+month+"-"+day;
            }

        }
    };

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId())
        {
            case R.id.ButtonSchedSave:
                
                 i = getIntent();
                //insert data

                try {
                    jsonObj.put("repeat",spinnerRepeat.getSelectedItem());
                    jsonObj.put("duration",spinnerduration.getSelectedItem());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                switch (pos)
                {
                    case 0:
                    {
                        editTextEventNumber.setVisibility(View.GONE);
                        try {
                            jobj.put(Constants.TAG_REQUEST,"Year");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 1:
                    {
                        editTextEventNumber.setVisibility(View.VISIBLE);
                        try {
                            jobj.put(Constants.TAG_REQUEST,"events_number");
                            jobj.put("numbers",editTextEventNumber.getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 3:
                    {
                        editTextEventNumber.setVisibility(View.GONE);
                        showDialog(DATE_PICKER_ID);
                        try {
                            jobj.put(Constants.TAG_REQUEST,"by_date");
                            jobj.put("expiration_date",formated_date);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                }

                Log.d("all_data",jsonObj.toString());
                setResult(RESULT_OK, i);
                finish();
                break;
            case R.id.ButtonSchedCancel:
                 i = getIntent();
                setResult(RESULT_CANCELED,i);
                finish();

                break;
        }
    }
}
