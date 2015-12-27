package com.example.matant.gpsportclient.InterfacesAndConstants;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.example.matant.gpsportclient.Utilities.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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




    public static final String TAG_SEARCH = "search";
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
    public static final String TAG_BASKETBALL = "Basketball";
    public static final String TAG_BICYCLE = "Bicycle";
    public static final String TAG_RUNNING = "Running";
    public static final String TAG_LONG = "lon";
    public static final String TAG_LAT = "lat";
    public static final String TAG_RADIUS = "radius";
    public static final int REQUEST_CODE_SET_SCHEDULE = 2;
    public static final int MAXIMUM_WEEKS = 30;
    public static final String TAG_ATTEND = "attend";
    public static final String TAG_NOT_ATTEND = "not attend";
    public static final String TAG_RES_INV_USR = "response_invited_user";
    public static final String TAG_HM_STATUS = "hash_map_status";


    //event
    public static final String MODE_UPDATE = "edit";
    public static final String MODE_CREATE = "create_event";
    public static final String MODE_DELETE = "delete";
    public static final String TAG_MODE = "mode";
    public static final String MODE_SEARCH_DEF = "default_search";
    public static final String MODE_SEARCH_REQ = "request_search";


    //event


    ////////////////////////Fragments Constants/////////////////////////


    //ViewEventFragmentController Constants

    public static final String TAG_VIEW = "view";
    public static final String TAG_REQUEST_VIEW_SUCCEED = "view_succeed";
    public static final String TAG_GET_EVENT_USERS = "get_event_users";
    public static final String TAG_EVENT_ID = "event_id";
    public static final String TAG_USER_STAT= "user_status";
    public static final String TAG_KIND_OF_SPORT = "kind_of_sport";
    public static final String TAG_START_TIME = "start_time";
    public static final String TAG_ADDRESS = "address";
    public static final String TAG_MIN_AGE = "min_age";
    public static final String TAG_IS_PRIVATE = "event_is_private";
    public static final String TAG_PRIVATE = "private";
    public static final String TAG_CURR_PRT = "current_participants";
    public static final String TAG_MAX_PRT = "max_participants";
    public static final String TAG_PART_USR = "participate_user";
    public static final String LAT = "latitude";
    public static final String LONG = "longitude";

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

    //GPSportLocationManager
    public static final long GPS_MIN_DISTANCE_CHANGE = 2; // 2
    public static final long GPS_MIN_TIME_BETWEEN_UPDATE = 1000 * 5 * 1; // 5
    public static final long NETWORK_MIN_DISTANCE_CHANGE = 5; // 5
    public static final long NETWORK_MIN_TIME_BETWEEN_UPDATE = 1000 * 10 * 1; // 10
    //GPSportLocationManager

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
    public static final int MENU_SIZE = 11;


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

    //Search Events
    public static final String TAG_START_DATE = "start_date";
    public static final String TAG_END_DATE = "end_date";
    public static final String TAG_END_TIME = "end_time";
    public static final String TAG_PUBLIC = "public";


    //Recent Searches
    public static final String TAG_SEARCH1 = "5";
    public static final String TAG_SEARCH2 = "4";
    public static final String TAG_SEARCH3 = "3";
    public static final String TAG_SEARCH4 = "2";
    public static final String TAG_SEARCH5 = "1";
    public static final String TAG_SEARCH_INDEX = "idx";
    public static final String TAG_REAL_ADDRESS = "real_address";
    public static final String TAG_SEARCH_PARAMS_FROM_RECENT = "search_params";





}
