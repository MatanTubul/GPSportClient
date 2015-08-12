package com.example.matant.gpsportclient.Controllers;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.matant.gpsportclient.AsyncResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nir b on 11/08/2015.
 * test
 */
public class DBcontroller extends AsyncTask <List<NameValuePair>, Void, InputStream>{

    private final String url= "http://10.0.2.2/test.php";
    public AsyncResponse delegate= null;
    private InputStream is = null;

    @Override
    protected InputStream doInBackground(List<NameValuePair>... params) {
        return postDataToServer(params[0]);
    }

    @Override
    protected void onPostExecute(InputStream is) {
        delegate.handleResponse(is);
    }

    private InputStream postDataToServer(List<NameValuePair> list) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        try {
            post.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (ClientProtocolException e) {
            // process execption
        } catch (IOException e) {
            // process execption
        }

        return is;
    }

    }







