package com.example.matant.gpsportclient.Controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.Utilities.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ProfileFragmentController extends Fragment implements AsyncResponse, View.OnClickListener {
    private Button buttonSignup, buttonSelectIMg;
    private EditText editTextname, editTextemail, editTextmobile, editTextPassword, editTextConfirmPass;
    private ImageView imgv;
    private final static int SELECT_PHOTO = 12345;
    private  int MINIMAL_YEAR_OF_BIRTH = 2001;
    private static final String TAG_FLG = "flag";
    private static final String TAG_NAME = "name";
    private static final String TAG_IMG = "image";
    private static final String TAG_MOB = "mobile";
    private static final String TAG_PASS = "password";
    private static final String TAG_EMAIL= "email";
    private static final String TAG_AGE= "age";
    private static final String TAG_GEN= "gender";
    private static final int CELL_CODE_LENGTH= 3;
    private static final int CELL_PHONE_LENGTH= 7;

    private ImageButton rotateLeft,rotateRight;

    private Spinner spinnerCellCode, spinnerAge, spinnerGender;
    ArrayAdapter<CharSequence> genderAdapter, mobileAdapter;
    ArrayAdapter<String> ageAdapter;
    public ErrorHandler err;
    private String areaCode = "", userGender = "", yearOfBirth = "";
    private int MIN_AGE = 14;
    private int MOBILE_LENGTH = 10;
    private ProgressDialog progress;
    private Bitmap originbitmap=null;
    private Bitmap scaled=null;
    private int rotate;
    DBcontroller dbController;
    private SessionManager sm;

    public ProfileFragmentController() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_sign_up, container, false);
        err = new ErrorHandler();

        //updating the minimal age
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        if ((year - MINIMAL_YEAR_OF_BIRTH) >= MIN_AGE) {
            MINIMAL_YEAR_OF_BIRTH++;
        }
        ///

        buttonSignup = (Button) rootView.findViewById(R.id.ButtonSubmit);
        buttonSignup.setText("submit changes");
        buttonSelectIMg = (Button)  rootView.findViewById(R.id.buttonSelectImg);
        rotateLeft = (ImageButton) rootView.findViewById(R.id.imageButtonRleftt);
        rotateRight = (ImageButton) rootView.findViewById(R.id.imageButtonRright);

        editTextname = (EditText)  rootView.findViewById(R.id.editTextName);
        editTextemail = (EditText)  rootView.findViewById(R.id.editTextEmail);

        editTextmobile = (EditText)  rootView.findViewById(R.id.editTextMobile);
        editTextPassword = (EditText)  rootView.findViewById(R.id.editTextPassword);
        editTextConfirmPass = (EditText)  rootView.findViewById(R.id.editTextConfirmPass);
        imgv = (ImageView)  rootView.findViewById(R.id.imageViewGallery);

       // setFieldsByUserDetails();


        spinnerAge = (Spinner)  rootView.findViewById(R.id.spinnerAge);
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1970; i <= MINIMAL_YEAR_OF_BIRTH; i++) {
            years.add(Integer.toString(i));

        }

        ageAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
        //ArrayAdapter<CharSequence> ageAdapter = ArrayAdapter.createFromResource(this, R.array.age, android.R.layout.simple_spinner_item);

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAge.setAdapter(ageAdapter);
        //spinnerAge.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        spinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }
                yearOfBirth = spinnerAge.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerGender = (Spinner) rootView.findViewById(R.id.spinnerGender);
        genderAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }
                userGender = spinnerGender.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCellCode = (Spinner) rootView.findViewById(R.id.spinnerMobile);
        mobileAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.area_code, android.R.layout.simple_spinner_item);

        mobileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCellCode.setAdapter(mobileAdapter);

        spinnerCellCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }
                areaCode = spinnerCellCode.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String regexeng = "^[a-zA-Z\\s]*$";
                if (!editTextname.getText().toString().trim().matches(regexeng)) {
                    editTextname.setError("Only English letters is valid");
                    editTextname.setText("");
                    editTextname.setHint("Name");
                }

            }
        });

        editTextname.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);
        buttonSelectIMg.setOnClickListener(this);

        rotateRight.setOnClickListener(this);
        rotateLeft.setOnClickListener(this);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDataFromDBController();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void handleResponse(String jsonStr) {
        Log.d("handleResponse", jsonStr);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String flg = jsonObj.getString(TAG_FLG);

                switch(flg)
                {
                    case "profile details retrieval":
                        Log.d("profile", jsonStr);
                        fillDateFromDBToUI(jsonObj);
                        break;
                    case "user already exists":
                        editTextemail.setError("This email already exists");
                        break;
                    case "mobile already exists":
                        editTextmobile.setError("Mobile already exists");
                        break;
                    case "succeed":
                        //dialog and go to main freg
                        //resetFields();
                        getActivity().getFragmentManager().popBackStack();
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ServiceHandler", "Couldn't get any data from the url");
        }
    }

    private void fillDateFromDBToUI(JSONObject jsonObj)
    {
         Log.d("setOnUI", "UI DATA");
         setTexts(jsonObj);
         setSpinners(jsonObj);
         setImage(jsonObj);

    }

    private void setImage (JSONObject jsonObj)
    {
        try{
            Log.d("setOnUI", "Image");
            String imageString = jsonObj.getString(TAG_IMG);
            Bitmap bMap = decodeBase64(imageString);
            imgv.setImageBitmap(bMap);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private void setTexts (JSONObject jsonObj)
    {
        try{
            Log.d("setOnUI", "Texts");
            editTextPassword.setText(jsonObj.getString(TAG_PASS));
            editTextConfirmPass.setText(jsonObj.getString(TAG_PASS));
            editTextname.setText(jsonObj.getString(TAG_NAME));
            editTextemail.setText(jsonObj.getString(TAG_EMAIL));
            editTextmobile.setText(jsonObj.getString(TAG_MOB).substring(CELL_CODE_LENGTH));
           }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSpinners(JSONObject jsonObj)
    {
        int spinnerPosition;
        String stringForSpinner;
        try {
            Log.d("setOnUI", "Spinners");
            stringForSpinner = jsonObj.getString(TAG_GEN);
            if (!stringForSpinner.equals(null)) {
                spinnerPosition = genderAdapter.getPosition(stringForSpinner);
                spinnerGender.setSelection(spinnerPosition);
            }

            stringForSpinner = jsonObj.getString(TAG_AGE);
            if (!stringForSpinner.equals(null)) {
                spinnerPosition = ageAdapter.getPosition(stringForSpinner);
                spinnerAge.setSelection(spinnerPosition);
                }

            stringForSpinner = jsonObj.getString(TAG_MOB);
            stringForSpinner = stringForSpinner.substring(0, stringForSpinner.length() - CELL_PHONE_LENGTH);
            if (!stringForSpinner.equals(null)) {
                spinnerPosition = mobileAdapter.getPosition(stringForSpinner);
                spinnerCellCode.setSelection(spinnerPosition);
                }
            }
        catch (JSONException e) {
          e.printStackTrace();
            }
    }

    @Override
    public void sendDataToDBController() {
        String userMobile = areaCode;
        userMobile += editTextmobile.getText().toString();
        BasicNameValuePair tagreq = new BasicNameValuePair("tag", "updateprofile");
        BasicNameValuePair name = new BasicNameValuePair("firstname", editTextname.getText().toString());
        BasicNameValuePair email = new BasicNameValuePair("email", editTextemail.getText().toString());
        BasicNameValuePair mobile = new BasicNameValuePair("mobile", userMobile);
        BasicNameValuePair password = new BasicNameValuePair("password", editTextPassword.getText().toString());
        BasicNameValuePair age = new BasicNameValuePair("birthyear", yearOfBirth);
        BasicNameValuePair gender = new BasicNameValuePair("gender", userGender);
        imgv.buildDrawingCache();
        Bitmap bmap = imgv.getDrawingCache();
        String picture = setPhoto(bmap);
        if(picture!=null)
        {
            BasicNameValuePair image = new BasicNameValuePair("picture", picture);

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(tagreq);
            nameValuePairList.add(name);
            nameValuePairList.add(email);
            nameValuePairList.add(mobile);
            nameValuePairList.add(password);
            nameValuePairList.add(age);
            nameValuePairList.add(gender);
            nameValuePairList.add(image);

            DBcontroller dbController = new DBcontroller(this.getActivity().getApplicationContext(),this);

            dbController.execute(nameValuePairList);
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(ProfileFragmentController.this.getActivity()).create();
            alertDialog.setTitle("Image Failure");
            alertDialog.setMessage("Failed to convert image!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    private String setPhoto(Bitmap bitmapm) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteArrayImage = baos.toByteArray();
            String imagebase64string = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            return imagebase64string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void preProcess() {
        //Log.d("preProcess", "getting data to UI");
    }

    private void getDataFromDBController()
    {
        String user = sm.getUserDetails().get(sm.KEY_EMAIL);
        Log.d("getDataFromDBController", "getting data to UI");
        BasicNameValuePair tagReq = new BasicNameValuePair("tag","profile");
        BasicNameValuePair method = new BasicNameValuePair("method","getprofile");
        BasicNameValuePair userNameParam = new BasicNameValuePair("username",user);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagReq);
        nameValuePairList.add(method);
        nameValuePairList.add(userNameParam);
        Log.d("user",user);
        dbController =  new DBcontroller(this.getActivity().getApplicationContext(),this);
        dbController.execute(nameValuePairList);
    }


    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {


            case R.id.ButtonSubmit: {

                //begin check of empty fields
                ArrayList<EditText> arr = new ArrayList<EditText>();
                arr.add(editTextemail);
                arr.add(editTextname);
                arr.add(editTextConfirmPass);
                arr.add(editTextmobile);
                arr.add(editTextPassword);


                //handle all the if statments
                if (err.fieldIsEmpty(arr, "Field cannot be empty!") == true) {
                    break;
                } else if (!err.validateEmailAddress(editTextemail.getText().toString())) {
                    //do not send data

                    editTextemail.setError("Email is invalid");
                    break;
                }
                //Confirm Password
                else if (!(editTextPassword.getText().toString().equalsIgnoreCase(editTextConfirmPass.getText().toString()))) {
                    //do not send data

                    editTextConfirmPass.setError("Passwords do not match!");
                    break;
                } else if (editTextmobile.length() + areaCode.length() != MOBILE_LENGTH) {
                    editTextmobile.setError("Mobile is not in the correct format");
                } else {
                    sendDataToDBController();
                }


                break;
            } // case R.id.ButtonSubmit


            case R.id.buttonSelectImg: {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
            }//case R.id.buttonSelectImg

            case R.id.imageButtonRleftt: {
                if (scaled != null) {
                    Matrix matrix = new Matrix();
                    rotate -= 90;
                    rotate %= 360;
                    matrix.postRotate(rotate);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaled, 0, 0, scaled.getWidth(), scaled.getHeight(), matrix, true);
                    imgv.setImageBitmap(rotatedBitmap);

                }
                break;
            }
            case R.id.imageButtonRright: {
                if (scaled != null) {
                    Matrix matrix = new Matrix();
                    rotate += 90;
                    rotate %= 360;
                    matrix.postRotate(rotate);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaled, 0, 0, scaled.getWidth(), scaled.getHeight(), matrix, true);
                    imgv.setImageBitmap(rotatedBitmap);


                }
                break;
            }

        }
    }



}
