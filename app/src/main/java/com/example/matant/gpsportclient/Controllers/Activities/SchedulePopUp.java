package com.example.matant.gpsportclient.Controllers.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SchedulePopUp extends Activity implements View.OnClickListener {
    private RadioGroup rdg;
    private EditText editTextEventNumber;
    private Spinner spinnerRepeat,spinnerduration;
    private TextView txtSumDate;
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
    ArrayList<String> arr;
    private String sum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_pop_up);
        arr = new ArrayList<String>();

        rdg = (RadioGroup)findViewById(R.id.radioGroup);
        save = (Button)findViewById(R.id.ButtonSchedSave);
        cancel = (Button) findViewById(R.id.ButtonSchedCancel);
        txtSumDate = (TextView) findViewById(R.id.textViewSumVal);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
         jsonObj = new JSONObject();
        spinnerduration = (Spinner) findViewById(R.id.spinnerDuration);
        ArrayList<String> weeks = new ArrayList<String>();
        for (int i = 1; i <= Constants.MAXIMUM_WEEKS; i++) {
            weeks.add(Integer.toString(i));

        }
        String[] weeks_array = weeks.toArray(new String[weeks.size()]);
        spinnerduration.setAdapter(new MyAdapter(this, R.layout.custom_spinner, weeks_array));
        spinnerduration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSumText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerRepeat = (Spinner) findViewById(R.id.spinnerRepeat);
        spinnerRepeat.setAdapter(new MyAdapter(this, R.layout.custom_spinner, getResources().getStringArray(R.array.event_repeats)));
        spinnerRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSumText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cancel = (Button) findViewById(R.id.ButtonSchedCancel);
        editTextEventNumber = (EditText)findViewById(R.id.editTextEventsNumber);
        editTextEventNumber.setVisibility(View.GONE);
        editTextEventNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setSumText();
            }
        });
        txtSumDate.setText("Daily repeat");




        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .7));
        Date today = new Date();
        c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.MONTH, 0);
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);
        formated_date = year+"-"+month+1+"-"+day;
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
                        setSumText();
                        break;
                    }
                    case 1:
                    {
                        editTextEventNumber.setVisibility(View.GONE);
                        setSumText();
                        break;
                    }
                    case 2:
                    {
                        editTextEventNumber.setVisibility(View.VISIBLE);
                        editTextEventNumber.setText("10");
                        setSumText();
                        break;
                    }
                    case 4:
                    {
                        editTextEventNumber.setVisibility(View.GONE);
                        showDialog(DATE_PICKER_ID);
                        setSumText();
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
            year  = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day   = c.get(Calendar.DAY_OF_MONTH);
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
                month = monthOfYear+1;
                day   = dayOfMonth;
                formated_date = year+"-"+month+"-"+day;
                setSumText();
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
                    case 0:{
                        try {
                            jobj.put(Constants.TAG_REQUEST,"unlimited");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 1:
                    {
                        editTextEventNumber.setVisibility(View.GONE);
                        try {
                            jobj.put(Constants.TAG_REQUEST,"Year");
                            jobj.put("val",getNextYearDate());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 2:
                    {
                        editTextEventNumber.setVisibility(View.VISIBLE);
                        try {
                            jobj.put(Constants.TAG_REQUEST,"events_number");
                            jobj.put("val",editTextEventNumber.getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 4:
                    {
                        editTextEventNumber.setVisibility(View.GONE);
                        showDialog(DATE_PICKER_ID);
                        try {
                            jobj.put(Constants.TAG_REQUEST,"by_date");
                            jobj.put("val",formated_date);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                }
                Log.d("all_data",jsonObj.toString());
                i.putExtra("sched_prop",jsonObj.toString());
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
    public String getNextYearDate(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        Date nextYear = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String output = df.format(nextYear);
        return output;

    }
    public String getRepeat(String s){
        if(s.equals("Daily")){
            return "days";
        }
        else if(s.equals("Weekly")){
            return "weeks";
        }else if(s.equals("Monthly")){
            return "months";
        }
        else
            return null;
    }
    public void setSumText(){

        switch (pos){
            case 4:{
                if(spinnerduration.getSelectedItem().equals("1"))
                {
                    sum = spinnerRepeat.getSelectedItem()+" "+"repeat,"+" "+"Until"+" "+formated_date;
                }else{
                    sum = spinnerRepeat.getSelectedItem()+" "+"repeat every"+" "+spinnerduration.getSelectedItem()+" "+getRepeat(spinnerRepeat.getSelectedItem().toString())+","+" "+"Until"+" "+formated_date;
                }
                break;

            }
            case 2:{
                if(spinnerduration.getSelectedItem().equals("1"))
                {
                    sum = spinnerRepeat.getSelectedItem()+" "+"repeat,"+" "+editTextEventNumber.getText()+" "+"times";
                }else{
                    sum = spinnerRepeat.getSelectedItem()+" "+"repeat every"+" "+spinnerduration.getSelectedItem()+" "+getRepeat(spinnerRepeat.getSelectedItem().toString())+","+" "+editTextEventNumber.getText()+" "+"times";
                }
                break;
            }
            case 1:{
                if(spinnerduration.getSelectedItem().equals("1"))
                {
                    sum = spinnerRepeat.getSelectedItem()+" "+"repeat,"+" "+"Until"+" "+getNextYearDate();
                }else{
                    sum = spinnerRepeat.getSelectedItem()+" "+"repeat every"+" "+spinnerduration.getSelectedItem()+" "+getRepeat(spinnerRepeat.getSelectedItem().toString())+","+" "+"Until"+" "+getNextYearDate();
                }
                break;
            }
            case 0:{
                if(spinnerduration.getSelectedItem().equals("1"))
                {
                    sum = spinnerRepeat.getSelectedItem()+" "+"repeat";
                }else{
                    sum = spinnerRepeat.getSelectedItem()+" "+"repeat, Repeat Every"+" "+spinnerduration.getSelectedItem()+" "+getRepeat(spinnerRepeat.getSelectedItem().toString());
                }
                break;
            }
        }
        txtSumDate.setText(sum);
    }


}
