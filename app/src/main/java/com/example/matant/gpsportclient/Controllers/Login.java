package com.example.matant.gpsportclient.Controllers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.R;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick (View v) {
        int clickedId = v.getId();
        Intent i = null;
        if (clickedId == R.id.LoginB)
            //validate input localy
            //validat input on server
             i = new Intent(Login.this,MainScreen.class);
        else if (clickedId == R.id.signUpB)
                 i = new Intent(Login.this,SignUp.class);
             else if (clickedId == R.id.forgotPasswordB)
                     i = new Intent(Login.this,ForgotPassword.class);
    if (i!=null)
      startActivity(i);
    }



}
