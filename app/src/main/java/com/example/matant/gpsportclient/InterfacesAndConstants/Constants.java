package com.example.matant.gpsportclient.InterfacesAndConstants;

import android.content.Context;
import android.content.Intent;

import com.example.matant.gpsportclient.MainScreen;

/**
 * Created by Nir B on 26/09/2015.
 */
public final class Constants {

    ////////////////////////Global Constants/////////////////////////

    public static final void reloadApp(Context fromContext, Class toClass){
        fromContext.startActivity(new Intent(fromContext, toClass));
    }
    public static final String TAG_NAME = "name";
    public static final String TAG_MOB = "mobile";
    public static final String TAG_EMAIL= "email";
    public static final String TAG_REGID = "reg_id";
    public static final String TAG_USERID = "user_id";
    public static final String TAG_IMG = "image";
    public static final String TAG_FLG = "flag";
    public static final String TAG_PASS = "password";
    public static final String TAG_AGE= "age";
    public static final String TAG_GEN= "gender";
    public static final String TAG_REQUEST = "tag";
    public static final String TAG_MANAGER_ID = "manager_id";
    public static final String TAG_REQUEST_FAILED = "failed";
    public static final String TAG_REQUEST_SUCCEED = "success";
    public static final String TAG_SOCCER = "soccer";
    public static final String TAG_BASKETBALL = "basketball";
    public static final String TAG_BICYCLE = "bicycle";
    public static final String TAG_RUNNING = "running";



    ////////////////////////Fragments Constants/////////////////////////

    //GoogleMapFragmentController Constants
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.example.matant.gpsportclient";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    //ProfileFragmentController Constants
    public static final int REQUEST_CODE = 1;


    ////////////////////////Activities Constants/////////////////////////

    //SignUp Constants
    public static final String TAG_USRCHK = "usercheck";
    public static final String TAG_MOBCHK = "mobilecheck";
    public static final String TAG_USECASE= "usecase";
    public static final int CELL_CODE_LENGTH= 3;
    public static final int CELL_PHONE_LENGTH= 7;
    public static final int SELECT_PHOTO = 12345;
    public static int MINIMAL_YEAR_OF_BIRTH = 2001;
    public static int MIN_AGE = 14;
    public static int MOBILE_LENGTH = 10;
    public static final String SENDER_ID ="846271397731" ;

    //MainScreen Constants
    public static final int MENU_SIZE = 8;


    ////////////////////////Utilities Constants/////////////////////////

    //SessionManager Constants
    public static final String PREFER_NAME = "Session";
    // Shared pref mode
    public static final int PRIVATE_MODE = 0;
    public static final String TAG_MSG = "msg";
}
