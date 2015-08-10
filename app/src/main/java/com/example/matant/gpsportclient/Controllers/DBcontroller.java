package com.example.matant.gpsportclient.Controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by matant on 8/9/2015.
 */
public  class DBcontroller extends AsyncTask<List<NameValuePair>,Void,String> {

    public DBcontroller() {
    }

    @Override
    protected String doInBackground(List<NameValuePair>... params) {
        String res = "";
        Log.d("forgot password","begin recover password");
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://10.0.2.2/gpSportserver/forgot_password.php");

        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params[0]);
            httpPost.setEntity(urlEncodedFormEntity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            // HttpResponse is an interface just like HttpPost.
            //Therefore we can't initialize them
            Log.d("httpPost","execute post");
            HttpResponse httpResponse = httpClient.execute(httpPost);

            // According to the JAVA API, InputStream constructor do nothing.
            //So we can't initialize InputStream although it is not an interface
            InputStream inputStream = httpResponse.getEntity().getContent();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            Log.d("InputStreamReader","execute inputStreamReader");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            Log.d("StringBuilder","execute StringBuilder");
            String bufferedStrChunk = null;

            while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                stringBuilder.append(bufferedStrChunk+"\n");
            }
            Log.d("inputStreamReader","execute close");
            inputStreamReader.close();
            res = stringBuilder.toString();
            Log.d("json result",res);
            JSONObject jarray = new JSONObject(res);

            Log.d("json result",jarray.toString());
            try {
                Log.d("JSON Msg:", jarray.getString("msg"));
            } catch (JSONException e) {
                Log.d("JSon Failed" , jarray.toString());
                e.printStackTrace();
            }
            return res;

        } catch (ClientProtocolException cpe) {
            System.out.println("Firstption caz of HttpResponese :" + cpe);
            cpe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Secondption caz of HttpResponse :" + ioe);
            ioe.printStackTrace();
        } catch (JSONException e) {
            Log.d("JSON error:", "failed to create jarray");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("Answer from server", s);
    }
}
