package com.mycompany.loginapp;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ResetPassword extends BaseActivity {

    private EditText email;
    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDisplayHomeAsUpEnabled(true);
        aQuery = new AQuery(this);
        email = (EditText)findViewById(R.id.Email);
        aQuery.id(R.id.toolbar_title).text(null);
        aQuery.id(R.id.sub_header_text_view).text(R.string.reset_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            getWindow().setEnterTransition(makeEnterTransition());
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        }

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
                            ResetPassword.this.finishAfterTransition();
                        } else {
                            Utilities.showDialog(
                                    ResetPassword.this, getString(R.string.err_reset_pw) + " " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void finishAfterTransition() {
        Log.d("finishAfterTransition()", "ResetPassword");
        getWindow().setReturnTransition(makeReturnTransition());
        super.finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        super.onBackPressed();
    }

    private Transition makeEnterTransition() {
        TransitionSet enterTransition = new TransitionSet();

        Transition t = new Slide(Gravity.LEFT).setDuration(600);
        enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.excludeTarget(R.id.toolbar_teal, true);
        enterTransition.addTransition(t);

        Transition tt = new Fade();
        enterTransition.addTransition(tt).setDuration(1000);
        return enterTransition;
    }

    private Transition makeReturnTransition() {
        TransitionSet returnTransition = new TransitionSet();

        Transition upperPartSlide = new Slide(Gravity.LEFT);
        returnTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        returnTransition.excludeTarget(android.R.id.statusBarBackground, true);
        returnTransition.excludeTarget(R.id.toolbar_teal, true);
        returnTransition.addTransition(upperPartSlide);

        Transition fade = new Fade();
        returnTransition.addTransition(fade);

        returnTransition.setDuration(500);
        return returnTransition;
    }
}
