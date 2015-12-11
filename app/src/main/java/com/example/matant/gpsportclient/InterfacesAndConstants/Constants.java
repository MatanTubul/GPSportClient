package com.example.matant.gpsportclient.InterfacesAndConstants;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.Utilities.SessionManager;
import com.google.android.gcm.GCMRegistrar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Nir B on 26/09/2015.
 */
public final class Constants {

    ////////////////////////Global Constants/////////////////////////

    public static final void reloadApp(Context fromContext, Class toClass){
        fromContext.startActivity(new Intent(fromContext, toClass));
    }

    /**
     * converting Bitmap image to String
     * @param bitmap- image in Bitmap format
     * @return Bitmap image as a String
     */
    public static final String setPhoto(Bitmap bitmap,String str, SessionManager sm) {
        String imagebase64string;
        try {
            if (str.equals("compress")) {
                Log.d("picture", "compressing");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] byteArrayImage = baos.toByteArray();
                try {
                    baos.flush();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagebase64string = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                if (sm != null)
                    sm.getUserDetails().put(Constants.TAG_IMG,imagebase64string);
                }
            else {
                imagebase64string = sm.getUserDetails().get(Constants.TAG_IMG);
                Log.d("picture", "no compressing");
                }
            return imagebase64string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String COMP = "compress";
    public static final String NO_COMP = "no compress";
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
    public static final String TAG_SOCCER = "Soccer";
    public static final String TAG_BASKETBALL = "BasketBall";
    public static final String TAG_BICYCLE = "Bicycle";
    public static final String TAG_RUNNING = "Running";
    public static final String TAG_LONG = "lon";
    public static final String TAG_LAT = "lat";
    public static final String TAG_RADIUS = "radius";
    public static final int REQUEST_CODE_SET_SCHEDULE = 2;
    public static final int MAXIMUM_WEEKS = 30;
    public static final String TAG_ATTEND = "attend";
    public static final String TAG_NOT_ATTEND = "not attend";



    //event
    public static final String MODE_UPDATE = "edit";
    public static final String MODE_CREATE = "create_event";
    public static final String MODE_DELETE = "delete";
    public static final String TAG_MODE = "mode";
    //event


    ////////////////////////Fragments Constants/////////////////////////


    //ViewEventFragmentController Constants

    public static final String TAG_GET_EVENT_USERS = "get_event_users";
    public static final String TAG_EVENT_ID = "event_id";
    public static final String TAG_KIND_OF_SPORT = "kind_of_sport";
    public static final String TAG_START_TIME = "start_time";
    public static final String TAG_ADDRESS = "address";
    public static final String TAG_MIN_AGE = "min_age";
    public static final String TAG_IS_PRIVATE = "private";
    public static final String TAG_CURR_PRT = "current_participants";
    public static final String TAG_MAX_PRT = "max_participants";


    //GoogleMapFragmentController Constants
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.example.matant.gpsportclient";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    // Location updates intervals in sec
    public static final int UPDATE_INTERVAL = 10000; // 10 sec
    public static final int FASTEST_INTERVAL = 5000; // 5 sec
    public static final int DISPLACEMENT = 10; // 10 meters
    public static final int DEFAULT_RADIUS = 5; // 5 km

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
    public static final int MENU_SIZE = 9;


    ////////////////////////Utilities Constants/////////////////////////

    //SessionManager Constants
    public static final String PREFER_NAME = "Session";
    public static final String TAG_CONNECTED = "is_connected";
    // Shared pref mode
    public static final int PRIVATE_MODE = 0;
    public static final String TAG_MSG = "msg";

    //DBController
    public static final int timeoutConnection = 30*1000;
    public static final int timeoutSocket = 30*1000;
}
