package com.example.matant.gpsportclient.Controllers.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
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
import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.InterfacesAndConstants.Constants;
import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.R;
import com.example.matant.gpsportclient.Utilities.ErrorHandler;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ProfileFragmentController extends Fragment implements View.OnClickListener,AsyncResponse {

    private Button buttonSignup, buttonSelectIMg;
    private EditText editTextname, editTextemail, editTextmobile, editTextPassword, editTextConfirmPass;
    private ImageView imgv;
    private ImageButton rotateLeft,rotateRight;
    private ArrayAdapter<CharSequence> genderAdapter, mobileAdapter;
    private ArrayAdapter<String> ageAdapter;
    private Spinner spinnerCellCode, spinnerAge, spinnerGender;
    public ErrorHandler err;
    private String areaCode = "", userGender = "", yearOfBirth = "",prevMobile ="", prevEmail="", picture;
    private ProgressDialog progress;
    private Bitmap originbitmap = null, scaled = null;
    private int rotate;
    private DBcontroller dbController;
    private HashMap<String, String> userDetails;
    private GoogleCloudMessaging gcm;
    private SessionManager sm;
    private boolean newPhotoWasSelected;
    public ProfileFragmentController() {} // Required empty public constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_profile_fragment_controller, container, false);
        sm = SessionManager.getInstance(getActivity());
        err = new ErrorHandler();
        newPhotoWasSelected = false;
        Log.d("newPhotoWasSelected", "false");
        getActivity().setTitle("Update Profile");
        //updating the minimal age
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        if ((year - Constants.MINIMAL_YEAR_OF_BIRTH) >= Constants.MIN_AGE) {
            Constants.MINIMAL_YEAR_OF_BIRTH++;
        }

        buttonSignup = (Button) v.findViewById(R.id.ButtonSubmit);
        buttonSelectIMg = (Button) v.findViewById(R.id.buttonSelectImg);
        rotateLeft = (ImageButton) v.findViewById(R.id.imageButtonRleftt);
        rotateRight = (ImageButton) v.findViewById(R.id.imageButtonRright);
        editTextname = (EditText) v.findViewById(R.id.editTextName);
        editTextemail = (EditText) v.findViewById(R.id.editTextEmail);
        editTextmobile = (EditText) v.findViewById(R.id.editTextMobile);
        editTextPassword = (EditText) v.findViewById(R.id.editTextPassword);
        editTextConfirmPass = (EditText) v.findViewById(R.id.editTextConfirmPass);
        imgv = (ImageView) v.findViewById(R.id.imageViewGallery);

        //resetFields();

        spinnerAge = (Spinner) v.findViewById(R.id.spinnerAge);
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1970; i <= Constants.MINIMAL_YEAR_OF_BIRTH; i++) {
            years.add(Integer.toString(i));
        }

        ageAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, years);
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerGender = (Spinner) v.findViewById(R.id.spinnerGender);
        genderAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.gender, android.R.layout.simple_spinner_item);
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCellCode = (Spinner) v.findViewById(R.id.spinnerMobile);
        mobileAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.area_code, android.R.layout.simple_spinner_item);
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
        buttonSignup.setText("SUBMIT CHANGES");
        fillDataFromSMToUI();

        return v;
    }

    private void fillDataFromSMToUI()
    {
        userDetails = sm.getUserDetails();
        Log.d("setOnUI", "UI DATA");
        setTexts(userDetails);
        setSpinners(userDetails);
        setImage(userDetails);

    }

    private void setImage (HashMap<String,String> userDetails)
    {

        Log.d("setOnUI", "Image");
        String imageString = userDetails.get(Constants.TAG_IMG);

        if (imageString.equals("nofile"))
            Log.d("setOnUI", "nofile");
        else {
            scaled = decodeBase64(imageString);
            imgv.setImageBitmap(scaled);
        }

    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private void setTexts (HashMap<String,String> userDetails)
    {

        Log.d("setOnUI", "Texts");
        editTextPassword.setText(userDetails.get(Constants.TAG_PASS));
        editTextConfirmPass.setText(userDetails.get(Constants.TAG_PASS));
        editTextname.setText(userDetails.get(Constants.TAG_NAME));
        prevEmail = userDetails.get(Constants.TAG_EMAIL);
        editTextemail.setText(prevEmail);
        String cellPhoneNum = userDetails.get(Constants.TAG_MOB).substring(Constants.CELL_CODE_LENGTH);
        Log.d("cellPhone", cellPhoneNum);
        editTextmobile.setText(cellPhoneNum);

    }

    private void setSpinners(HashMap<String,String> userDetails)
    {
        int spinnerPosition;
        String stringForSpinner;

        Log.d("setOnUI", "Spinners");
        stringForSpinner = userDetails.get(Constants.TAG_GEN);
        if (stringForSpinner != null) {
            Log.d("gender", stringForSpinner);
            spinnerPosition = genderAdapter.getPosition(stringForSpinner);
            spinnerGender.setSelection(spinnerPosition);
        }

        stringForSpinner = userDetails.get(Constants.TAG_AGE);
        if (stringForSpinner != null) {
            Log.d("age", stringForSpinner);
            spinnerPosition = ageAdapter.getPosition(stringForSpinner);
            spinnerAge.setSelection(spinnerPosition);
        }

        stringForSpinner = userDetails.get(Constants.TAG_MOB);
        stringForSpinner = stringForSpinner.substring(0, stringForSpinner.length() - Constants.CELL_PHONE_LENGTH);
        String cellPhoneNum = userDetails.get(Constants.TAG_MOB).substring(Constants.CELL_CODE_LENGTH);
        if (stringForSpinner != null) {
            Log.d("mobile", stringForSpinner);
            spinnerPosition = mobileAdapter.getPosition(stringForSpinner);
            spinnerCellCode.setSelection(spinnerPosition);
            prevMobile = stringForSpinner;
            Log.d("prevMobile",prevMobile );
            prevMobile += cellPhoneNum;
            Log.d("prevMobile", prevMobile);
        }

    }


    @Override
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
            }

            case R.id.buttonSelectImg: {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Constants.SELECT_PHOTO);
                break;
            }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == Constants.SELECT_PHOTO && resultCode == getActivity().RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            originbitmap = BitmapFactory.decodeFile(imagePath, options);
            int nh = (int) ( originbitmap.getHeight() * (512.0 / originbitmap.getWidth()) );
            scaled = Bitmap.createScaledBitmap(originbitmap,512,nh,true);
            imgv.setImageBitmap(scaled);
            newPhotoWasSelected = true;
            Log.d("newPhotoWasSelected", "true");
            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }



    }


    @Override
    public void sendDataToDBController() {
        String userMobile = areaCode;
        userMobile += editTextmobile.getText().toString();
        Log.d("sendDataToDBController", "sendDataToDBController");

        BasicNameValuePair tagreq = new BasicNameValuePair("tag", "profile");
        BasicNameValuePair nEmail = new BasicNameValuePair("newemail", editTextemail.getText().toString());
        BasicNameValuePair pEmail = new BasicNameValuePair("prevemail", prevEmail);
        BasicNameValuePair nMobile = new BasicNameValuePair("newmobile", userMobile);
        BasicNameValuePair pMobile = new BasicNameValuePair("prevmobile", prevMobile);
        BasicNameValuePair regId = new BasicNameValuePair("regid", GCMRegistrar.getRegistrationId(this.getActivity()));
        Log.d("prefferences id",GCMRegistrar.getRegistrationId(this.getActivity()));
        BasicNameValuePair name = new BasicNameValuePair("firstname", editTextname.getText().toString());
        BasicNameValuePair password = new BasicNameValuePair("password", editTextPassword.getText().toString());
        BasicNameValuePair age = new BasicNameValuePair("birthyear", yearOfBirth);
        BasicNameValuePair gender = new BasicNameValuePair("gender", userGender);

        String whoAsChanged  = "0";
        if (!prevEmail.equals(editTextemail.getText().toString()))
            whoAsChanged = "1";
        if (!prevMobile.equals(userMobile)) {
            if (whoAsChanged.equals("1"))
                whoAsChanged = "3";
            else
                whoAsChanged = "2";
        }
        BasicNameValuePair changed = new BasicNameValuePair("changed", whoAsChanged);

        imgv.buildDrawingCache();
        Bitmap bmap = imgv.getDrawingCache();
        if (newPhotoWasSelected) {
            picture = Constants.setPhoto(bmap, Constants.COMP, sm); //compress photo if it was selected from mobile gallery
            Log.d("picture", "gallery picture selected");
        }
        else {
            picture = Constants.setPhoto(bmap, Constants.NO_COMP, sm); // if no mobile gallery photo was selected don't compress the photo
            Log.d("picture", "no gallery picture selected");

        }

        if(picture!=null)
        {
            Log.d("picture", "there's some picture");

            BasicNameValuePair image = new BasicNameValuePair("picture", picture);

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(tagreq);
            nameValuePairList.add(name);
            nameValuePairList.add(password);
            nameValuePairList.add(regId);
            nameValuePairList.add(age);
            nameValuePairList.add(gender);
            nameValuePairList.add(image);
            nameValuePairList.add(nEmail);
            nameValuePairList.add(nMobile);
            nameValuePairList.add(pEmail);
            nameValuePairList.add(pMobile);
            nameValuePairList.add(changed);

            dbController = new DBcontroller(getActivity().getApplicationContext(),this);
            dbController.execute(nameValuePairList);
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity()).create();
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
    public void handleResponse(String resStr) {
        progress.dismiss();

        Log.d("handleResponse", resStr);
        if (resStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(Constants.TAG_FLG);

                switch(flg)
                {
                    case "wrong input":
                        if (jsonObj.getString(Constants.TAG_USRCHK).equals("user already exists"))
                            editTextemail.setError("This email is taken");
                        if (jsonObj.getString(Constants.TAG_MOBCHK).equals("mobile already exists"))
                            editTextmobile.setError("This mobile is taken");
                        break;
                    case "succeed":

                        Log.d("succeed", "profile");
                        sm.StoreUserSession(editTextemail.getText().toString(), Constants.TAG_EMAIL);
                        sm.StoreUserSession(editTextname.getText().toString(), Constants.TAG_NAME);
                        sm.StoreUserSession(editTextPassword.getText().toString(),Constants.TAG_PASS);
                        sm.StoreUserSession(userGender, Constants.TAG_GEN);
                        sm.StoreUserSession(yearOfBirth,Constants.TAG_AGE);

                        String userMobile = areaCode;
                        userMobile += editTextmobile.getText().toString();
                        sm.StoreUserSession(userMobile,Constants.TAG_MOB);

                        if(picture!=null)
                            sm.StoreUserSession(picture,Constants.TAG_IMG);

                        Constants.reloadApp(getActivity(), MainScreen.class);

                        //resetFields();
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ServiceHandler", "Couldn't get any data from the url");
        }
    }

    @Override
    public void preProcess() {
        progress = ProgressDialog.show(this.getActivity(), "Profile update", "Updating your account", true);
    }

}
