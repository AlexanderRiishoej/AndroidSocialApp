package com.mycompany.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.ParseUser;


public class UserActivity extends BaseActivity {

    // User
    private TextView user;

    /**
     * The user.
     */
    public static ParseUser username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.account);

        // Get the TexView and assign the username to it
        user = (TextView) findViewById(R.id.signedInAs);
        user.setText(username.getUsername());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.user;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_chat:
                startActivity(new Intent(UserActivity.this, UserList.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
