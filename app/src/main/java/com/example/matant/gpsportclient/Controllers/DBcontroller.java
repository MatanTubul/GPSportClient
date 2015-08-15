package com.example.matant.gpsportclient.Controllers;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.example.matant.gpsportclient.AsyncResponse;

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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nir b on 11/08/2015.
 * test
 */
public class DBcontroller extends AsyncTask <List<NameValuePair>, Void, String>{

    private final String url= "http://10.0.2.2/test.php";
    public AsyncResponse delegate= null;



    @Override
    protected String doInBackground(List<NameValuePair>... params) {
        return postDataToServer(params[0]);
    }

    @Override
    protected void onPostExecute(String resStr) {
        delegate.handleResponse(resStr);
        super.onPostExecute(resStr);
    }

    private String postDataToServer(List<NameValuePair> list) {
        Log.d("responseString", list.get(0).toString() +""+ list.get(1).toString()+""+ list.get(2).toString());
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        String responseString = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

                Log.d("responseString", responseString);
        return responseString;
    }

    }







