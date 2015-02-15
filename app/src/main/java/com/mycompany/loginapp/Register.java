package com.mycompany.loginapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * The Class Register is the Activity class that shows user registration screen
 * that allows user to register itself on Parse server.
 */
public class Register extends BaseActivity {

    /**
     * The username EditText.
     */
    private EditText user;

    /**
     * The password EditText.
     */
    private EditText pwd;

    /**
     * The email EditText.
     */
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.ic_menu_white_18dp);
        setActionBarTitle(R.string.title_activity_register);

        user = (EditText) findViewById(R.id.Username);
        pwd = (EditText) findViewById(R.id.Password);
        email = (EditText) findViewById(R.id.Email);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.register;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void register(View view) {
        String u = user.getText().toString();
        String p = pwd.getText().toString();
        String e = email.getText().toString();
        if (u.length() == 0 || p.length() == 0 || e.length() == 0) {
            Utilities.showDialog(this, R.string.err_fields_empty);
            return;
        }
        final ProgressDialog dia = ProgressDialog.show(this, null,
                getString(R.string.alert_wait));

        final ParseUser pu = new ParseUser();
        pu.setEmail(e);
        pu.setPassword(p);
        pu.setUsername(u);
        pu.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {
                dia.dismiss();
                if (e == null) {
//                    UserList.user = pu;
//                    startActivity(new Intent(Register.this, UserList.class));
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Utilities.showDialog(
                            Register.this,
                            getString(R.string.err_singup) + " "
                                    + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
