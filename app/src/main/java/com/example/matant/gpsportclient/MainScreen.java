package com.example.matant.gpsportclient;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.matant.gpsportclient.Controllers.Fragments.CreateEventFragmentController;
import com.example.matant.gpsportclient.Controllers.DBcontroller;
import com.example.matant.gpsportclient.Controllers.Fragments.GoogleMapFragmentController;
import com.example.matant.gpsportclient.Controllers.Fragments.ProfileFragmentController;
import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.Utilities.DrawerItem;
import com.example.matant.gpsportclient.Utilities.DrawerItemCustomAdapter;
import com.example.matant.gpsportclient.Utilities.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainScreen extends AppCompatActivity implements AsyncResponse {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    private static final String TAG_FLG = "flag";

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    public final int MENU_SIZE = 8;
    private DBcontroller dbController;
    private ProgressDialog progress = null;
    private SessionManager sm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        sm = SessionManager.getInstance(this);

        mTitle = mDrawerTitle = "Home";
        DrawerItem [] drawerItems = new DrawerItem[MENU_SIZE];
        drawerItems[0] = new DrawerItem(R.drawable.home,"Home");
        drawerItems[1] = new DrawerItem(R.drawable.profile,"Profile");
        drawerItems[2] = new DrawerItem(R.drawable.search,"Search Events");
        drawerItems[3] = new DrawerItem(R.drawable.create,"Create Event");
        drawerItems[4] = new DrawerItem(R.drawable.manage,"Manage Event");
        drawerItems[5] = new DrawerItem(R.drawable.attending,"Attending List");
        drawerItems[6] = new DrawerItem(R.drawable.recent_search_24,"Recent Searches");
        drawerItems[7] = new DrawerItem(R.drawable.logout,"Log Out");

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_menu, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setTitle(mNavigationDrawerItemTitles[0]);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            selectItem(0);
        }

    }

    @Override
    public void handleResponse(String resStr) {
        try
        {
            if((this.progress!= null )&& this.progress.isShowing())
            {
                this.progress.dismiss();
            }
        }catch (final IllegalArgumentException e){
            Log.d("Dialog error",e.getMessage());
        }catch (final Exception e){
            Log.d("Dialog error",e.getMessage());
        }
            finally {
            this.progress = null;
        }

        Log.d("handleResponse", resStr);
        if(resStr!=null)
        {
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                String flg = jsonObj.getString(TAG_FLG);
                switch (flg) {
                    case "user logged out":
                    {
                        sm.logoutUser();
                        break;
                    }
                    case "query failed": {
                        Toast.makeText(getApplicationContext(),"Error Connection",Toast.LENGTH_LONG).show();
                        break;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void sendDataToDBController() {

        String user = sm.getUserDetails().get(sm.KEY_EMAIL);
        BasicNameValuePair tagReq = new BasicNameValuePair("tag","logout");
        BasicNameValuePair userNameParam = new BasicNameValuePair("username",user);
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        nameValuePairList.add(tagReq);
        nameValuePairList.add(userNameParam);
        dbController =  new DBcontroller(this,this);

        dbController.execute(nameValuePairList);

    }

    @Override
    public void preProcess() {
        this.progress = ProgressDialog.show(this, "Log Out",
                "Logging out...", true,false);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0: //Home
                fragment = new GoogleMapFragmentController();
                break;
            case 1: //Profile
                fragment = ProfileFragmentController.getInstance();
                break;
            case 2: //Search Events
                //fragment = new SearchEventFragmentController();
                break;
            case 3: //Create Events
                fragment = new CreateEventFragmentController();
                break;
            case 4: //Manage Events
                //fragment = new ManageEventFragmentController();
                break;
            case 5: //Attending List
                //fragment = new AttendingListFragmentController();
                break;
            case 6: //Recent Searches
                //fragment = new RecentSearchesFragmentController();
                break;
            case 7: { //Log Out

                logout();
                finish(); //destroy the main activity
            }
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            getSupportActionBar().setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    public void logout()
    {

        sendDataToDBController();
    }


}
