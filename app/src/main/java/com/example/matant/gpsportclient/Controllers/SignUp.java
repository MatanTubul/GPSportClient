package com.example.matant.gpsportclient.Controllers;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.matant.gpsportclient.MainScreen;
import com.example.matant.gpsportclient.R;

public class SignUp extends ActionBarActivity implements View.OnClickListener {
    private Button buttonLgn,buttonSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        buttonLgn = (Button)findViewById(R.id.ButtonLgn);
        buttonSignup = (Button) findViewById(R.id.ButtonSubmit);

        buttonLgn.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);
    }

    public void onClick (View v) {
        Intent i = null;
        switch(v.getId()){
            case R.id.ButtonLgn:
                i = new Intent(SignUp.this, Login.class);
                startActivity(i);
                break;
        }
    }
}
