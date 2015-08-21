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

import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SignUp extends AppCompatActivity implements View.OnClickListener,AsyncResponse {

    private Button buttonSignup, buttonSelectIMg;
    private EditText editTextname, editTextemail, editTextmobile, editTextPassword, editTextConfirmPass;
    private ImageView imgv;
    private final static int SELECT_PHOTO = 12345;
    private static int MINIMAL_YEAR_OF_BIRTH = 2001;
    private static final String TAG_FLG = "flag";
    private ImageButton rotateLeft,rotateRight;

    private Spinner spinerCellCode, spinerAge, spinnerGender;
    public ErrorHandler err;
    private String areaCode = "", userGender = "", yearOfBirth = "";
    private int MIN_AGE = 14;
    private int MOBILE_LENGTH = 10;
    private ProgressDialog progress;
    private Bitmap originbitmap;
    private Bitmap scaled;
    private int rotate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        err = new ErrorHandler();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        if ((year - MINIMAL_YEAR_OF_BIRTH) >= MIN_AGE) {
            MINIMAL_YEAR_OF_BIRTH++;
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


        spinerAge = (Spinner) findViewById(R.id.spinnerAge);
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1970; i <= MINIMAL_YEAR_OF_BIRTH; i++) {
            years.add(Integer.toString(i));

        }

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        //ArrayAdapter<CharSequence> ageAdapter = ArrayAdapter.createFromResource(this, R.array.age, android.R.layout.simple_spinner_item);

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinerAge.setAdapter(ageAdapter);
        //spinerAge.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        spinerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }
                yearOfBirth = spinerAge.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);

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


        spinerCellCode = (Spinner) findViewById(R.id.spinnerMobile);
        ArrayAdapter<CharSequence> mobileAdapter = ArrayAdapter.createFromResource(this, R.array.area_code, android.R.layout.simple_spinner_item);

        mobileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinerCellCode.setAdapter(mobileAdapter);

        spinerCellCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }
                areaCode = spinerCellCode.getSelectedItem().toString();
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
                Matrix matrix = new Matrix();
                rotate -=90;
                rotate  %= 360;
                matrix.postRotate(rotate);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaled , 0, 0, scaled .getWidth(), scaled .getHeight(), matrix, true);
                imgv.setImageBitmap(rotatedBitmap);
                break;
            }
            case R.id.imageButtonRright: {
                Matrix matrix = new Matrix();
                rotate +=90;
                rotate  %= 360;
                matrix.postRotate(rotate);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaled , 0, 0, scaled .getWidth(), scaled .getHeight(), matrix, true);
                imgv.setImageBitmap(rotatedBitmap);


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
                    case "user already exists":
                        editTextemail.setError("This email already exists");
                        break;
                    case "mobile already exists":
                        editTextmobile.setError("Mobile already exists");
                        break;
                    case "succeed":
                        startActivity(new Intent(SignUp.this, Login.class));
                        resetFields();
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
        BasicNameValuePair tagreq = new BasicNameValuePair("tag", "signup");
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


            DBcontroller dbController = new DBcontroller(this);
            dbController.delegate = this;
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
        progress = ProgressDialog.show(this, "Sign up",
                "Creating your account", true);
        
    }

    /**
     * converting Bitmap image to String
     * @param bitmapm- image in Bitmap format
     * @return Bitmap image as a String
     */
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




}
