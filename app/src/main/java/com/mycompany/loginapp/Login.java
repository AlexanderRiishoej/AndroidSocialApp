package com.mycompany.loginapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * The Class Login is an Activity class that shows the login screen to users.
 * The current implementation simply includes the options for Login and button
 * for Register. On login button click, it sends the Login details to Parse
 * server to verify user.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Login extends BaseActivity {

    ChatApp chatApp;

    EditText editText;
    /**
     * The username edittext.
     */
    private EditText user;

    /**
     * The password edittext.
     */
    private EditText pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatApp = new ChatApp();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_teal);
        setActionBarIcon(R.drawable.ic_menu_white_18dp);

        user = (EditText) findViewById(R.id.Username);
        pwd = (EditText) findViewById(R.id.Password);
//        if (toolbar != null) {
////            setSupportActionBar(toolbar);
////            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    // Handle the menu item
//                    int id = item.getItemId();
//
//                    //noinspection SimplifiableIfStatement
//                    if (id == R.id.action_settings) {
//                        return true;
//                    }
//                    return true;
//                }
//            });
//
//            // Inflate a menu to be displayed in the toolbar
//            toolbar.inflateMenu(R.menu.menu_main);
//        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.login_form;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                //drawer.openDrawer(Gravity.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void LoginPressed(View view) {
        editText = (EditText) findViewById(R.id.status_text);
        editText.setText("Login pressed!");
        //Utilities.showDialog(this, getString(R.string.err_login));

        String u = user.getText().toString();
        String p = pwd.getText().toString();
        if (u.length() == 0 || p.length() == 0) {
            Utilities.showDialog(this, R.string.err_fields_empty);
            return;
        }
        final ProgressDialog dia = ProgressDialog.show(this, null,
                getString(R.string.alert_wait));
        ParseUser.logInInBackground(u, p, new LogInCallback() {

            @Override
            public void done(ParseUser pu, ParseException e) {
                dia.dismiss();
                if (pu != null) {
                    // Start the logged in activity with the name of the person who logged in
                    UserActivity.username = pu;
                    UserList.user = pu;
                    startActivity(new Intent(Login.this, UserActivity.class));
                    finish();
                } else {
                    Utilities.showDialog(
                            Login.this,
                            getString(R.string.err_login) + " "
                                    + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // If Register Button is pressed.
    public void RegisterPressed(View view) {
        editText = (EditText) findViewById(R.id.status_text);
        editText.setText("Register pressed!");

        // If Register Button is pressed - Start the activity for the Register class
        //startActivityForResult(new Intent(this, Register.class), 10);
        startActivityForResult(new Intent(this, Register.class), 10);
    }

    /* (non-Javadoc)
 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            Log.d("Result_OK", "Result is ok");
            //finish();
        }

    }
//
//    private void SetUpActionBar() {
//        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            ActionBar actionBar = getActionBar();
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }
}
