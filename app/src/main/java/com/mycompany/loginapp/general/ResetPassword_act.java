package com.mycompany.loginapp.general;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ResetPassword_act extends BaseActivity {

    private EditText email;
    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.makeWindowTransition();
        TextInputLayout mTextInputResetPasswordLayout = (TextInputLayout) findViewById(R.id.forgot_password_textinput);
        email = mTextInputResetPasswordLayout.getEditText();
        aQuery = new AQuery(this);
        aQuery.id(R.id.toolbar_title).text("Reset password");
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
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
                            Utilities.showDialog(ResetPassword_act.this, "A message has been sent to your email address with a request to reset your password.");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ResetPassword_act.this.finishAfterTransition();
                            }
                            else ResetPassword_act.this.finish();
                        } else {
                            Utilities.showDialog(
                                    ResetPassword_act.this, getString(R.string.err_reset_pw) + " " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------------------
     * Window Transitions
     */
    private void makeWindowTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(makeEnterTransition());
            getWindow().setExitTransition(makeReturnTransition());
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        }
    }
    private Transition makeEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet enterTransition = new TransitionSet();

            Transition groupTransition = new Slide(Gravity.LEFT);

            LinearLayout resetPswLayoutMain = (LinearLayout) findViewById(R.id.main_reset_password);
            groupTransition.addTarget(resetPswLayoutMain);
            resetPswLayoutMain.setTransitionGroup(true);
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            enterTransition.excludeTarget(R.id.toolbar_teal, true);
            //enterTransition.addTransition(fadeIn);
            enterTransition.addTransition(groupTransition).setDuration(300);

            return enterTransition;
        } else return null;
    }

    private Transition makeReturnTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet enterTransition = new TransitionSet();

            Transition upperPartSlide = new Slide(Gravity.LEFT);
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            enterTransition.excludeTarget(R.id.toolbar_teal, true);
            enterTransition.addTransition(upperPartSlide);

            enterTransition.setDuration(300);
            return enterTransition;
        } else return null;
    }
}
