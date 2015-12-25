package com.example.matant.gpsportclient.DataClasses;

import com.example.matant.gpsportclient.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by nirb on 11/20/2015.
 */
public class MapMarker implements Serializable{

    private String mLabel;
    private Double mLatitude;
    private Double mLongitude;
    private JSONObject mJsonObject;
    private BitmapDescriptor mIcon;
    private int mBitmap;
    private Marker mMarker;
    private HashMap sh;

    public JSONObject getmJsonObject() {
        return mJsonObject;
    }

    public void setmJsonObject(JSONObject mJsonObject) {
        this.mJsonObject = mJsonObject;
    }

    public int getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(int mBitmap) {
        this.mBitmap = mBitmap;
    }

    public Marker getmMarker() {
        return mMarker;
    }

    public void setmMarker(Marker mMarker) {
        this.mMarker = mMarker;
    }

    public MapMarker(JSONObject jsonObject)
    {
        this.mJsonObject = jsonObject;
        fillDataFromJsonObject();
    }

    private void fillDataFromJsonObject()
    {
        try {
            mLatitude = Double.parseDouble(mJsonObject.getString("latitude"));
            mLongitude = Double.parseDouble(mJsonObject.getString("longitude"));
            mLabel= mJsonObject.getString("kind_of_sport");
            mIcon = getSportMarkerIcon();

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private BitmapDescriptor getSportMarkerIcon()
    {
        sh = SportsHash.getSportsHash();
        SportsHash.Sport sport=(SportsHash.Sport) sh.get(mLabel);
        mBitmap = sport.getSportMapMarkerId();
        return BitmapDescriptorFactory.fromResource(mBitmap);

    }

    public String getmLabel()
    {
        return mLabel;
    }

    public void setmLabel(String mLabel)
    {
        this.mLabel = mLabel;
    }

    public BitmapDescriptor getmIcon()
    {
        return mIcon;
    }

    public void setmIcon(BitmapDescriptor icon)
    {
        this.mIcon = icon;
    }

    public Double getmLatitude()
    {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude)
    {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude()
    {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude)
    {
        this.mLongitude = mLongitude;
    }
}