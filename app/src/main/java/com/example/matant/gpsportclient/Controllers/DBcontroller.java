package com.example.matant.gpsportclient.Controllers;


import android.content.Context;

import android.os.AsyncTask;

import android.util.Log;

import com.example.matant.gpsportclient.InterfacesAndConstants.AsyncResponse;
import com.example.matant.gpsportclient.Utilities.PropertyReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import java.io.IOException;

import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.Properties;

/**
 * Created by Nir b on 11/08/2015.
 * test
 */
public class DBcontroller extends AsyncTask <List<NameValuePair>, Void, String>{

    private static String url = "";
    private  Context context = null ;
    public AsyncResponse delegate= null;

    private PropertyReader propertyReader;
    private Properties properties;

    public DBcontroller(Context mycontext,AsyncResponse myresponse) {
        context = mycontext;
        delegate = myresponse;
        propertyReader = new PropertyReader(context);
        properties = propertyReader.getMyProperties("config.properties");
        url = properties.getProperty("url");
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        delegate.preProcess();



    }

    @Override
    protected String doInBackground(List<NameValuePair>... params) {
        return postDataToServer(params[0]);
    }

    @Override
    protected void onPostExecute(String resStr) {
        super.onPostExecute(resStr);
        delegate.handleResponse(resStr);

    }

    private String postDataToServer(List<NameValuePair> list) {
        //check
        for (int i=0;i<list.size();i++)
            Log.d("responseString", list.get(i).toString());
        //check
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        String responseString = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity);
            Log.d("responseString1", responseString);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("responseString2", responseString);
        return responseString;
    }


}










