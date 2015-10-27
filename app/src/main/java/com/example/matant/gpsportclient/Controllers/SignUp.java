package com.example.matant.gpsportclient.Controllers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.ProfileManager;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SignUp extends AppCompatActivity implements View.OnClickListener,AsyncResponse {

    private static final String SENDER_ID ="846271397731" ;
    private Button buttonSignup, buttonSelectIMg;
    private EditText editTextname, editTextemail, editTextmobile, editTextPassword, editTextConfirmPass;
    private ImageView imgv;
    private final static int SELECT_PHOTO = 12345;
    private  int MINIMAL_YEAR_OF_BIRTH = 2001;
    private ImageButton rotateLeft,rotateRight;
    ArrayAdapter<CharSequence> genderAdapter, mobileAdapter;
    ArrayAdapter<String> ageAdapter;
    private static final String TAG_FLG = "flag";
    private static final String TAG_NAME = "name";
    private static final String TAG_IMG = "image";
    private static final String TAG_MOB = "mobile";
    private static final String TAG_PASS = "password";
    private static final String TAG_EMAIL= "email";
    private static final String TAG_USRCHK = "usercheck";
    private static final String TAG_MOBCHK = "mobilecheck";
    private static final String TAG_AGE= "age";
    private static final String TAG_GEN= "gender";
    private static final String TAG_USECASE= "usecase";
    private static final int CELL_CODE_LENGTH= 3;
    private static final int CELL_PHONE_LENGTH= 7;
    private Spinner spinnerCellCode, spinnerAge, spinnerGender;
    public ErrorHandler err;
    private String areaCode = "", userGender = "", yearOfBirth = "",prevMobile ="", prevEmail="";
    private int MIN_AGE = 14;
    private int MOBILE_LENGTH = 10;
    private ProgressDialog progress;
    private Bitmap originbitmap=null;
    private Bitmap scaled=null;
    private int rotate;
    private Intent editProfileIntent;
    private DBcontroller dbController;
    private HashMap<String, String> userDetails;
    private String useCaseFlag;
    private GoogleCloudMessaging gcm;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        err = new ErrorHandler();
        useCaseFlag = "SignUp";
        //updating the minimal age
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        if ((year - MINIMAL_YEAR_OF_BIRTH) >= MIN_AGE) {
            MINIMAL_YEAR_OF_BIRTH++;
        }
        ///
        gcm = GoogleCloudMessaging.getInstance(this.getApplicationContext());
        sm = new SessionManager(this);
        String regID = sm.getUserDetails().get(sm.KEY_REGID);
        if(regID == null)
        {
            Log.d("register","begin register");
            registerGCM();
        }
        buttonSignup = (Button) findViewById(R.id.ButtonSubmit);
        buttonSelectIMg = (Button) findViewById(R.id.buttonSelectImg);
        rotateLeft = (ImageButton)findViewById(R.id.imageButtonRleftt);
        rotateRight = (ImageButton)findViewById(R.id.imageButtonRright);
        editTextname = (EditText) findViewById(R.id.editTextName);
        editTextemail = (EditText) findViewById(R.id.editTextEmail);
        editTextmobile = (EditText) findViewById(R.id.editTextMobile);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPass = (EditText) findViewById(R.id.editTextConfirmPass);
        imgv = (ImageView) findViewById(R.id.imageViewGallery);

        resetFields();


        spinnerAge = (Spinner) findViewById(R.id.spinnerAge);
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1970; i <= MINIMAL_YEAR_OF_BIRTH; i++) {
            years.add(Integer.toString(i));

        }
        ageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
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

        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
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


        spinnerCellCode = (Spinner) findViewById(R.id.spinnerMobile);
        mobileAdapter = ArrayAdapter.createFromResource(this, R.array.area_code, android.R.layout.simple_spinner_item);
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

        editTextname.setOnClickListener(this);
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


        buttonSignup.setOnClickListener(this);
        buttonSelectIMg.setOnClickListener(this);
        rotateRight.setOnClickListener(this);
        rotateLeft.setOnClickListener(this);

        editProfileIntent = getIntent();
        userDetails = (HashMap<String, String>) editProfileIntent.getSerializableExtra("USER_DETAILS");
        if (userDetails != null) {
            useCaseFlag = "Profile";
            buttonSignup.setText("SUBMIT CHANGES");
            Log.d("HashMap", userDetails.get(TAG_EMAIL));
            getDataFromDBController();
        }
    }

    private void registerGCM() {
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {

                try {
                    String regid = gcm.register(SENDER_ID);
                    Log.d("registerGCM id",regid);
                    sm.StoreUserSession(regid,sm.KEY_USERID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void getDataFromDBController()
    {
        String userStr = userDetails.get(TAG_EMAIL);
        Log.d("getDataFromDBController", "getting data to UI");
        BasicNameValuePair tagReq = new BasicNameValuePair("tag","profile");
        BasicNameValuePair method = new BasicNameValuePair("method","getprofile");
        BasicNameValuePair userNameParam = new BasicNameValuePair("username",userStr);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagReq);
        nameValuePairList.add(method);
        nameValuePairList.add(userNameParam);
        Log.d("user",userStr);
        dbController =  new DBcontroller(this,this);
        dbController.execute(nameValuePairList);
    }


    /**
     * function which respond to button clicks
     *
     * @param v-relevant UI widget.
     */
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


                //handle all the if statment
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


    /**
     * reset the edittext fields
     */
    public void resetFields() {
        editTextname.setHint("Name");
        editTextConfirmPass.setHint("Confirm Password");
        editTextPassword.setHint("Password");
        editTextmobile.setHint("Mobile");
        editTextemail.setHint("Email");
        imgv.setImageResource(R.drawable.camera);
    }

    @Override

    /**
     * loading photo from gallery phone to the application
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            originbitmap = BitmapFactory.decodeFile(imagePath, options);
            int nh = (int) ( originbitmap.getHeight() * (512.0 / originbitmap.getWidth()) );
            scaled = Bitmap.createScaledBitmap(originbitmap,512,nh,true);
            imgv.setImageBitmap(scaled);

            // Do something with the bitmap


            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }

    /**
     * this function handle with the server response and run the correct method on the UI
     * @param resStr - response fron the server
     */
    @Override
    public void handleResponse(String resStr) {
        progress.dismiss();

        Log.d("handleResponse", resStr);
        if (resStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(TAG_FLG);

                switch(flg)
                {
                    case "profile details retrieval":
                        Log.d("profile", resStr);
                        Log.d("class", this.getClass().getSimpleName());
                        fillDataFromDBToUI(jsonObj);
                        break;
                    case "wrong input":
                        if (jsonObj.getString(TAG_USRCHK).equals("user already exists"))
                            editTextemail.setError("This email is taken");
                        if (jsonObj.getString(TAG_MOBCHK).equals("mobile already exists"))
                            editTextmobile.setError("This mobile is taken");
                        break;
                    case "succeed":
                        if (jsonObj.getString(TAG_USECASE).equals("register"))
                        {
                            startActivity(new Intent(SignUp.this, Login.class));
                            resetFields();
                            finish();
                        }
                        else
                        {
                            //dialog and go to main freg
                            this.getFragmentManager().popBackStack();
                        }
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ServiceHandler", "Couldn't get any data from the url");
        }

    }

    private void fillDataFromDBToUI(JSONObject jsonObj)
    {
        Log.d("setOnUI", "UI DATA");
        setTexts(jsonObj);
        setSpinners(jsonObj);
        setImage(jsonObj);

    }

    private void setImage (JSONObject jsonObj)
    {
        try {
            Log.d("setOnUI", "Image");
            String imageString = jsonObj.getString(TAG_IMG);
            if (imageString.equals("nofile"))
                Log.d("setOnUI", "nofile");
            else {
                scaled = decodeBase64(imageString);
                imgv.setImageBitmap(scaled);
            }


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

            prevEmail = jsonObj.getString(TAG_EMAIL);
            editTextemail.setText(prevEmail);

            prevMobile = areaCode;
            String cellPhone = jsonObj.getString(TAG_MOB).substring(CELL_CODE_LENGTH);
            prevMobile += cellPhone;
            editTextmobile.setText(cellPhone);
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
            if (stringForSpinner != null) {
                Log.d("gender", stringForSpinner);
                spinnerPosition = genderAdapter.getPosition(stringForSpinner);
                spinnerGender.setSelection(spinnerPosition);
            }

            stringForSpinner = jsonObj.getString(TAG_AGE);
            if (stringForSpinner != null) {
                Log.d("age", stringForSpinner);
                spinnerPosition = ageAdapter.getPosition(stringForSpinner);
                spinnerAge.setSelection(spinnerPosition);
            }

            stringForSpinner = jsonObj.getString(TAG_MOB);
            stringForSpinner = stringForSpinner.substring(0, stringForSpinner.length() - CELL_PHONE_LENGTH);
            if (stringForSpinner != null) {
                Log.d("mobile", stringForSpinner);
                spinnerPosition = mobileAdapter.getPosition(stringForSpinner);
                spinnerCellCode.setSelection(spinnerPosition);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }






    /**
     * executing the register request to the server.
     */
    @Override
    public void sendDataToDBController() {

        String userMobile = areaCode;
        userMobile += editTextmobile.getText().toString();

        BasicNameValuePair tagreq = null, nEmail = null, pEmail = null, email = null, nMobile = null, pMobile = null,
                mobile = null, name = null, password = null, age = null, gender = null, changed = null,regId = null;

        if (useCaseFlag.equals("Profile"))
        {
            tagreq = new BasicNameValuePair("tag", "updateprofile");
            nEmail = new BasicNameValuePair("newemail", editTextemail.getText().toString());
            pEmail = new BasicNameValuePair("prevemail", prevEmail);
            nMobile = new BasicNameValuePair("newmobile", userMobile);
            pMobile = new BasicNameValuePair("prevmobile", prevMobile);

            String whoAsChanged  = "0";
            if (!prevEmail.equals(editTextemail.getText().toString()))
                whoAsChanged = "1";
            if (!prevMobile.equals(userMobile)) {
                if (whoAsChanged.equals("1"))
                    whoAsChanged = "3";
                else
                    whoAsChanged = "2";
            }
            changed = new BasicNameValuePair("changed", whoAsChanged);
        }
        else //useCaseFlag.equals("SignUp")
        {
            tagreq = new BasicNameValuePair("tag", "signup");
            email = new BasicNameValuePair("email", editTextemail.getText().toString());
            mobile = new BasicNameValuePair("mobile", userMobile);
        }
        Log.d("prefferences id",sm.getUserDetails().get(sm.KEY_REGID));
        regId = new BasicNameValuePair("regid", sm.getUserDetails().get(sm.KEY_REGID));
        name = new BasicNameValuePair("firstname", editTextname.getText().toString());
        password = new BasicNameValuePair("password", editTextPassword.getText().toString());
        age = new BasicNameValuePair("birthyear", yearOfBirth);
        gender = new BasicNameValuePair("gender", userGender);

        imgv.buildDrawingCache();
        Bitmap bmap = imgv.getDrawingCache();
        String picture = setPhoto(bmap);
        if(picture!=null)
        {
            BasicNameValuePair image = new BasicNameValuePair("picture", picture);

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(tagreq);
            nameValuePairList.add(name);
            nameValuePairList.add(password);
            nameValuePairList.add(regId);
            nameValuePairList.add(age);
            nameValuePairList.add(gender);
            nameValuePairList.add(image);

            if (useCaseFlag.equals("Profile"))
            {
                nameValuePairList.add(nEmail);
                nameValuePairList.add(nMobile);
                nameValuePairList.add(pEmail);
                nameValuePairList.add(pMobile);
                nameValuePairList.add(changed);
            }
            else //useCaseFlag.equals("SignUp")
            {
                nameValuePairList.add(email);
                nameValuePairList.add(mobile);
            }

            dbController = new DBcontroller(this,this);
            dbController.execute(nameValuePairList);
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(SignUp.this).create();
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


    @Override
    public void preProcess() {
        if (useCaseFlag.equals("SignUp"))
            progress = ProgressDialog.show(this, "Sign up", "Creating your account", true);
        else
            progress = ProgressDialog.show(this, "Profile update", "Updating your account", true);
    }

    /**
     * converting Bitmap image to String
     * @param bitmapm- image in Bitmap format
     * @return Bitmap image as a String
     */
    private String setPhoto(Bitmap bitmapm) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] byteArrayImage = baos.toByteArray();
            String imagebase64string = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            return imagebase64string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Please log out!", Toast.LENGTH_LONG);
    }
}