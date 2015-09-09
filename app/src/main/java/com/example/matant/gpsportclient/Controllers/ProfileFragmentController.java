package com.example.matant.gpsportclient.Controllers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.Calendar;


public class ProfileFragmentController extends Fragment implements AsyncResponse, View.OnClickListener {
    private Button buttonSignup, buttonSelectIMg;
    private EditText editTextname, editTextemail, editTextmobile, editTextPassword, editTextConfirmPass;
    private ImageView imgv;
    private final static int SELECT_PHOTO = 12345;
    private  int MINIMAL_YEAR_OF_BIRTH = 2001;
    private static final String TAG_FLG = "flag";
    private ImageButton rotateLeft,rotateRight;

    private Spinner spinerCellCode, spinerAge, spinnerGender;
    public ErrorHandler err;
    private String areaCode = "", userGender = "", yearOfBirth = "";
    private int MIN_AGE = 14;
    private int MOBILE_LENGTH = 10;
    private ProgressDialog progress;
    private Bitmap originbitmap=null;
    private Bitmap scaled=null;
    private int rotate;

    private OnFragmentInteractionListener mListener;


    public ProfileFragmentController() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        spinerAge = (Spinner)  rootView.findViewById(R.id.spinnerAge);
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1970; i <= MINIMAL_YEAR_OF_BIRTH; i++) {
            years.add(Integer.toString(i));

        }

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
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

        spinnerGender = (Spinner) rootView.findViewById(R.id.spinnerGender);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.gender, android.R.layout.simple_spinner_item);

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


        spinerCellCode = (Spinner) rootView.findViewById(R.id.spinnerMobile);
        ArrayAdapter<CharSequence> mobileAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.area_code, android.R.layout.simple_spinner_item);

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void handleResponse(String resStr) {

    }

    @Override
    public void sendDataToDBController() {

    }

    @Override
    public void preProcess() {

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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
