package com.example.matant.gpsportclient.Controllers;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.matant.gpsportclient.AsyncResponse;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.R;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity implements View.OnClickListener,AsyncResponse {

    private Button buttonLgn, buttonSignup, buttonSelectIMg;
    private EditText editTextname, editTextemail, editTextmobile, editTextPassword, editTextConfirmPass;
    private ImageView imgv;
    private final static int SELECT_PHOTO = 12345;
    private Spinner spinerCellCode,spinerAge,spinnerGender;
    public ErrorHandler err;
    private String areaCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        err = new ErrorHandler();

        buttonLgn = (Button) findViewById(R.id.ButtonLgn);
        buttonSignup = (Button) findViewById(R.id.ButtonSubmit);
        buttonSelectIMg = (Button) findViewById(R.id.buttonSelectImg);

        editTextname = (EditText) findViewById(R.id.editTextName);
        editTextemail = (EditText) findViewById(R.id.editTextEmail);

        editTextmobile = (EditText) findViewById(R.id.editTextMobile);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPass = (EditText) findViewById(R.id.editTextConfirmPass);
        imgv = (ImageView) findViewById(R.id.imageViewGallery);

        
        spinerAge = (Spinner)findViewById(R.id.spinnerAge);

        ArrayAdapter<CharSequence> ageAdapter = ArrayAdapter.createFromResource(this, R.array.age, android.R.layout.simple_spinner_item);

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinerAge.setAdapter(ageAdapter);

        spinerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        spinnerGender=(Spinner)findViewById(R.id.spinnerGender);

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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        
        

        spinerCellCode = (Spinner)findViewById(R.id.spinnerMobile);
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
                String regexeng = "^[a-zA-z.?]*";
                if(!editTextname.getText().toString().trim().matches(regexeng)){
                    editTextname.setError("Only English letters is valid");
                    editTextname.setText("");
                    editTextname.setHint("Name");
                }

            }
        });

        buttonLgn.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);
        buttonSelectIMg.setOnClickListener(this);
    }



    /**
     * function which respond to button clicks
     * @param v-relevant UI widget.
     */
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.ButtonLgn:

                {
                    i = new Intent(SignUp.this, Login.class);
                    startActivity(i);
                    resetFields();
                    break;
                }//case R.id.ButtonLgn

            case R.id.ButtonSubmit:
            {

                //begin check of empty fields
                ArrayList<EditText> arr = new ArrayList<EditText>();
                arr.add(editTextemail);
                arr.add(editTextname);
                arr.add(editTextConfirmPass);
                arr.add(editTextmobile);
                arr.add(editTextPassword);


                //handle all the if statment
                if(err.fieldIsEmpty(arr, "Field cannot be empty!")==true)
                {
                    //do not send data
                }
                else if(!err.validateEmailAddress(editTextemail.getText().toString()))
                {
                    //do not send data

                    editTextemail.setError("Email is invalid");
                }
                //Confirm Password
                else if(!(editTextPassword.getText().toString().equalsIgnoreCase(editTextConfirmPass.getText().toString())))
                {
                    //do not send data

                    editTextConfirmPass.setError("Passwords do not match!");
                }
                else
                {
                    areaCode +=editTextmobile.getText().toString();
                }



            } // case R.id.ButtonSubmit

                break;

            case R.id.buttonSelectImg:
            {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
            }//case R.id.buttonSelectImg

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
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            imgv.setImageBitmap(bitmap);

            // Do something with the bitmap


            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }

    @Override
    public void handleResponse(String resStr) {

    }

    @Override
    public void sendDataToDBController() {

    }
}
