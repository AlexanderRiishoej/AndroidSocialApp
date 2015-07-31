package com.mycompany.loginapp.general;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.login.Login2_act;
import com.mycompany.loginapp.profile.ProfilePrivate_act;
import com.mycompany.loginapp.registration.Register_act;

public class Startup_act extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_act);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_startup_act, menu);
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

    public void onLoginPressed(View view){
        startActivity(new Intent(this, Login2_act.class));

    }

    public void onCreateAccountPressed(View view){
        startActivity(new Intent(this, Register_act.class));
    }

    public void onForgotPasswordPressed(View view){
        startActivity(new Intent(this, ResetPassword_act.class));
    }
}
