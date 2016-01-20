package com.example.matant.gpsportclient.Utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;

/**
 * this class goal is to open a properties file.
 * in our application we have a "config.properties" file that holding
 * the url to our server API.
 * Created by matant on 8/20/2015.
 */
public class PropertyReader {

    private Context context;
    private Properties properties;

    public PropertyReader(Context context){
        this.context=context;
        properties = new Properties();
    }

    /**
     * return properties object
     * @param file - path to our config file
     * @return properties object
     */
    public Properties getMyProperties(String file){
        try{
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(file);
            properties.load(inputStream);

        }catch (Exception e){
            Log.d("Failed load properties", e.getMessage());
        }

        return properties;
    }}
