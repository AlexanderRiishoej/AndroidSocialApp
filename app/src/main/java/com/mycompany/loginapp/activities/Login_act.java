package com.mycompany.loginapp.activities;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.profile.ProfilePrivate_act;
import com.mycompany.loginapp.registration.Register_act;
import com.mycompany.loginapp.singletons.MySingleton;
import com.mycompany.loginapp.chat.UserChatList_act;
import com.mycompany.loginapp.eventMessages.MessageEvent;
import com.mycompany.loginapp.utilities.Utilities;
import com.mycompany.loginapp.views.CharacterCountErrorWatcher;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import de.greenrobot.event.EventBus;

/**
 * The Class Login is an Activity class that shows the login screen to users.
 * The current implementation simply includes the options for Login and button
 * for Register. On login button click, it sends the Login details to Parse
 * server to verify user.
 */
//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Login_act extends BaseActivity {

    public static final String LOG = Login_act.class.getSimpleName();
    /**
     * The parseUser edittext.
     */
    private EditText user;
    private TextInputLayout mTextInputUsernameLayout;
    private TextInputLayout mTextInputPasswordLayout;

    /**
     * The password edittext.
     */
    private EditText pwd;

    private AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.makeWindowTransition();
        aq = new AQuery(this);
        aq.id(R.id.toolbar_title).text(getString(R.string.login_form));

        mTextInputUsernameLayout = (TextInputLayout)findViewById(R.id.username_textinput);
        mTextInputPasswordLayout = (TextInputLayout)findViewById(R.id.password_textinput);

        user = mTextInputUsernameLayout.getEditText();
        pwd = mTextInputPasswordLayout.getEditText();
        user.addTextChangedListener(new CharacterCountErrorWatcher(mTextInputUsernameLayout, 5, 20));
        pwd.addTextChangedListener(new CharacterCountErrorWatcher(mTextInputPasswordLayout, 4, 20));
        //mTextInputUsernameLayout.setError("Bob");
        user.setText(MySingleton.getMySingleton().getDefaultSharedPreferences().getString("parseUser", ""));
        pwd.setText(MySingleton.getMySingleton().getDefaultSharedPreferences().getString("password", ""));
    }

    /**
     * Called when Activity starts and resumes
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.login_form;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                //drawer.openDrawer(Gravity.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Logs the User in.
     * If User credentials are wrong an error is shown with Parse.com's error description.
     * Finishes the current activity with call, finish()
     */
    public void LoginPressed(View view) {
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
                    //User_act.parseUser = pu;
                    UserChatList_act.user = pu;
                    startActivity(new Intent(Login_act.this, ProfilePrivate_act.class));
                    finish();
                } else {
                    Utilities.showDialog(
                            Login_act.this,
                            getString(R.string.err_login) + " "
                                    + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Starts the Register activity.
     */
    public void RegisterPressed(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(new Intent(this, Register_act.class), 10, ActivityOptions.makeSceneTransitionAnimation(Login_act.this).toBundle());
        } else {
            startActivity(new Intent(this, Register_act.class));
        }
        EventBus.getDefault().post(new MessageEvent("Hello everyone!"));
    }

    /**
     * Whenever you use any signup or login methods, the user is cached on disk.
     * You can treat this cache as a session, and automatically assume the user is logged in
     */
    private void CurrentUserLoggedIn() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            //ProfilePrivate_act.parseUser = currentUser;
            UserChatList_act.user = currentUser;
            startActivityForResult(new Intent(this, ProfilePrivate_act.class), 9);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //this.finishAfterTransition();
                this.finish();
            }
            else{
                finish();
            }
        } else {
            return;
            // show the signup or login screen
        }
    }

    /**
     * If a user wants to reset a password
     *
     * @param view
     */
    public void ForgotPassword(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            startActivity(new Intent(Login_act.this, ResetPassword_act.class), ActivityOptions.makeSceneTransitionAnimation(Login_act.this).toBundle());
        } else {
            startActivity(new Intent(Login_act.this, ResetPassword_act.class));
        }
    }

    /* (non-Javadoc)
 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            Log.d("Result_OK", "Result is ok");
            this.finish();
        }
    }

    @Override
    public void finishAfterTransition() {
        Log.d("Finishing Login", "Login");
        super.finishAfterTransition();
    }

    // This method will be called when a MessageEvent is posted
    public void onEvent(MessageEvent event) {
        //Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
        Log.d(LOG, event.message);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Called when this activity starts or resumes
     */
    @Override
    protected void onResume() {
        super.onResume();
        CurrentUserLoggedIn();
    }

    /**
     * Called when another activity is in focus
     */
    @Override
    protected void onPause() {
        super.onPause();
        ApplicationMain.getDefaultSharedPreferences()
                .edit()
                .putString("parseUser", user.getText().toString())
                .putString("password", pwd.getText().toString())
                .apply();
        //commit writes data to persistent data immediately while apply will handle it in the background
    }

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------------------
     * Window Transitions
     */
    private void makeWindowTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setReenterTransition(makeReenterTransition());
            this.getWindow().setEnterTransition(makeEnterTransition());
            ActivityOptions.makeSceneTransitionAnimation(Login_act.this).toBundle();
        }
    }

    private Transition makeEnterTransition() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet enterTransition = new TransitionSet();
            //Find main view group of the view
            LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
            // Create a slide transition
            Transition slide = new Slide(Gravity.BOTTOM).setDuration(300);
            // Add the main view group to the slide transition
            slide.addTarget(mainLinearLayout);
            // Remember to set the transitiongroup to true
            mainLinearLayout.setTransitionGroup(true);
            // Finally add the transition to the TransitionSet
            enterTransition.addTransition(slide);

            return enterTransition;

        }
        else return null;
    }
private Transition makeReenterTransition() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        TransitionSet reenterTransition = new TransitionSet();
        reenterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        reenterTransition.excludeTarget(android.R.id.statusBarBackground, true);
        reenterTransition.excludeTarget(R.id.toolbar_teal, true);

        Transition slideInFromLeft = new Slide(Gravity.LEFT);

        reenterTransition.addTransition(slideInFromLeft).setDuration(300);
        return reenterTransition;

    } else return null;
}
}
