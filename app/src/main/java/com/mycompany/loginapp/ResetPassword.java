package com.mycompany.loginapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;


public class ResetPassword extends BaseActivity {

    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDisplayHomeAsUpEnabled(true);

        email = (EditText)findViewById(R.id.Email);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.reset_password;
    }

    public void resetPassword(View view){

        String em = email.getText().toString();

        if (em.length() == 0 || em.length() == 0) {
            Utilities.showDialog(this, R.string.err_fields_empty);
            return;
        }
        final ProgressDialog dia = ProgressDialog.show(this, null,
                getString(R.string.alert_wait));

        ParseUser.requestPasswordResetInBackground(email.getText().toString(),
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        dia.dismiss();
                        if (e == null) {
                            Utilities.showDialog(ResetPassword.this, "A message has been sent to your email address with a request to reset your password.");
                            ResetPassword.this.finish();
                        } else {
                            Utilities.showDialog(
                                    ResetPassword.this, getString(R.string.err_reset_pw) + " " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }
}
