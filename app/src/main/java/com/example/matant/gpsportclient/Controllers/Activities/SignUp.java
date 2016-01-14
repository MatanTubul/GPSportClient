package com.example.matant.gpsportclient.Controllers.Activities;

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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.Adapters.MyAdapter;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Class the handling the user registration
 * including:
 * selecting photo, user details, and params checks
 */


public class SignUp extends AppCompatActivity implements View.OnClickListener,AsyncResponse {

    private Button buttonSignup, buttonSelectIMg;
    private EditText editTextname, editTextemail, editTextmobile, editTextPassword, editTextConfirmPass;
    private ImageView imgv;
    private ImageButton rotateLeft,rotateRight;
    private ArrayAdapter<CharSequence> genderAdapter, mobileAdapter;
    private ArrayAdapter<String> ageAdapter;
    private Spinner spinnerCellCode, spinnerAge, spinnerGender;
    public ErrorHandler err;
    private String areaCode = "", userGender = "", yearOfBirth = "", picture;
    private ProgressDialog progress;
    private Bitmap originbitmap = null, scaled = null;
    private int rotate;
    private DBcontroller dbController;
    private GoogleCloudMessaging gcm;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        sm = SessionManager.getInstance(this);
        err = new ErrorHandler();
        //updating the minimal age
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        if ((year - Constants.MINIMAL_YEAR_OF_BIRTH) >= Constants.MIN_AGE) {
            Constants.MINIMAL_YEAR_OF_BIRTH++;
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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


        spinnerAge = (Spinner) findViewById(R.id.spinnerAge);
        ArrayList<String> years = new ArrayList<String>();
        for (int i = 1970; i <= Constants.MINIMAL_YEAR_OF_BIRTH; i++) {
            years.add(Integer.toString(i));

        }
        String[] yearscontent = new String[years.size()];
        yearscontent = years.toArray(yearscontent);
        spinnerAge.setAdapter(new MyAdapter(this, R.layout.custom_spinner, yearscontent));

        spinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearOfBirth = spinnerAge.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        spinnerGender.setAdapter(new MyAdapter(this,R.layout.custom_spinner,getResources().getStringArray(R.array.gender)));
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

                Log.d("SUBMITCLICKED", "SUBMITCLICKED");
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
                } else if (editTextmobile.length() + areaCode.length() != Constants.MOBILE_LENGTH) {
                    editTextmobile.setError("Mobile is not in the correct format");
                } else {
                    sendDataToDBController();
                }


                break;
            } // case R.id.ButtonSubmit


            case R.id.buttonSelectImg: {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Constants.SELECT_PHOTO);
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

    @Override

    /**
     * loading photo from gallery phone to the application
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == Constants.SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
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
            if(originbitmap == null)
            {
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Select image failed!")
                        .setMessage("Image is not available, please make sure the image located on the device!")
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                return;
                            }
                        })
                        .setIcon(R.drawable.error_32)
                        .show();

            }else{
                int nh = (int) ( originbitmap.getHeight() * (512.0 / originbitmap.getWidth()) );
                scaled = Bitmap.createScaledBitmap(originbitmap,512,nh,true);
                imgv.setImageBitmap(scaled);

                // At the end remember to close the cursor or you will end with the RuntimeException!
                cursor.close();
            }


        }
    }

    /**
     * this function handle with the server response and run the correct method on the UI
     * @param resStr - response fron the server
     */
    @Override
    public void handleResponse(String resStr) {
        progress.dismiss();


        if (resStr != null) {
            Log.d("handleResponse", resStr);
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);

                switch(flg)
                {
                    case "wrong input":
                            if (jsonObj.getString(Constants.TAG_USRCHK).equals("user already exists"))
                                editTextemail.setError("email already exist!");
                            if (jsonObj.getString(Constants.TAG_MOBCHK).equals("mobile already exists"))
                                editTextmobile.setError("Mobile already exist!");


                        break;
                    case "succeed":
                        Log.d("succeed", "register");
                        new android.support.v7.app.AlertDialog.Builder(this)
                                .setTitle("Registration Succeeded")
                                .setMessage("Registration finished successfully.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(SignUp.this, Login.class));
                                        finish();

                                    }
                                })
                                .setIconAttribute(android.R.attr.alertDialogIcon)
                                .show();
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ServiceHandler", "Couldn't get any data from the url");
        }

    }


    /**
     * executing the register request to the server.
     */
    @Override
    public void sendDataToDBController() {

        String userMobile = areaCode;
        userMobile += editTextmobile.getText().toString();

        Log.d("sendDataToDBController", "sendDataToDBController");

        BasicNameValuePair tagreq = new BasicNameValuePair("tag", "signup");
        BasicNameValuePair email = new BasicNameValuePair("email", editTextemail.getText().toString());
        BasicNameValuePair mobile = new BasicNameValuePair("mobile", userMobile);
        BasicNameValuePair regId = new BasicNameValuePair("regid", GCMRegistrar.getRegistrationId(this));
        Log.d("prefferences id",GCMRegistrar.getRegistrationId(this));
        BasicNameValuePair name = new BasicNameValuePair("firstname", editTextname.getText().toString());
        BasicNameValuePair password = new BasicNameValuePair("password", editTextPassword.getText().toString());
        BasicNameValuePair age = new BasicNameValuePair("birthyear", spinnerAge.getSelectedItem().toString());
        BasicNameValuePair gender = new BasicNameValuePair("gender", spinnerGender.getSelectedItem().toString());

        imgv.buildDrawingCache();
        Bitmap bmap = imgv.getDrawingCache();
        picture = Constants.setPhoto(bmap, Constants.COMP, null);
        if(picture!=null)
        {
            BasicNameValuePair image = new BasicNameValuePair("picture", picture);
            Log.d("picture", "not null");
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(tagreq);
            nameValuePairList.add(name);
            nameValuePairList.add(password);
            nameValuePairList.add(regId);
            nameValuePairList.add(age);
            nameValuePairList.add(gender);
            nameValuePairList.add(image);
            nameValuePairList.add(email);
            nameValuePairList.add(mobile);


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
            alertDialog.setIcon(R.drawable.error_32);
            alertDialog.show();
        }

    }

    @Override
    public void preProcess() {
            progress = ProgressDialog.show(this, "Sign up", "Creating your account", true);
    }

    /**
     * Override the back button on the device
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUp.this, Login.class));
        finish();
    }
}