package com.mycompany.loginapp;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
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
import android.transition.Explode;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * The Class Register is the Activity class that shows user registration screen
 * that allows user to register itself on Parse server.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
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

    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.title_activity_register);
        setDisplayHomeAsUpEnabled(true);
        aQuery = new AQuery(this);
        aQuery.id(R.id.toolbar_title).text(null);
        user = (EditText) findViewById(R.id.Username);
        pwd = (EditText) findViewById(R.id.Password);
        email = (EditText) findViewById(R.id.Email);

        aQuery.id(R.id.sub_header_text_view).text(R.string.register_account);
        getWindow().setEnterTransition(makeEnterTransition());
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle();

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
        switch (id){
            case android.R.id.home:
                Register.this.finishAfterTransition();
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
                    finishAfterTransition();
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

    @Override
    public void finishAfterTransition() {
        Log.d("finishAfterTransition()", "Ran");
        getWindow().setReturnTransition(makeReturnTransition());
        super.finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
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
        TransitionSet enterTransition = new TransitionSet();

        Transition upperPartSlide = new Slide(Gravity.LEFT);
        enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.excludeTarget(R.id.toolbar_teal, true);
        enterTransition.addTransition(upperPartSlide);

        Transition fade = new Fade();
        enterTransition.addTransition(fade);

        enterTransition.setDuration(500);
        return enterTransition;
    }
}
