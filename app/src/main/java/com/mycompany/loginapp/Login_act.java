package com.mycompany.loginapp;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.base.MainApp;
import com.mycompany.loginapp.eventMessages.MessageEvent;
import com.mycompany.loginapp.utilities.Utilities;
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
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Login_act extends BaseActivity {

    /**
     * The parseUser edittext.
     */
    private EditText user;

    /**
     * The password edittext.
     */
    private EditText pwd;

    private AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setReenterTransition(makeReenterTransition());
        this.getWindow().setEnterTransition(makeReenterTransition());
        ActivityOptions.makeSceneTransitionAnimation(Login_act.this).toBundle();
        //setDisplayHomeAsUpEnabled(false);
        //setActionBarIcon(R.drawable.ic_menu_white_24dp);
        aq = new AQuery(this);
        user = (EditText) findViewById(R.id.Username);
        pwd = (EditText) findViewById(R.id.Password);
        //Used to store values from your app and accessing them later
        user.setText(MainApp.getDefaultSharedPreferences().getString("parseUser", ""));
        pwd.setText(MainApp.getDefaultSharedPreferences().getString("password", ""));
    }

    /**
     * Called when Activity starts and resumes
     */
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
                    User_act.parseUser = pu;
                    UserList_act.user = pu;
                    startActivity(new Intent(Login_act.this, User_act.class));
//                    startActivity(new Intent(Login.this, UserActivity.class), ActivityOptions.makeSceneTransitionAnimation(
//                            Login.this).toBundle());
                    //getWindow().setExitTransition(makeExitTransition());
                    //ActivityOptions.makeSceneTransitionAnimation(Login.this).toBundle();
                    Login_act.this.finishAfterTransition();
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
        startActivity(new Intent(this, Register_act.class), ActivityOptions.makeSceneTransitionAnimation(Login_act.this).toBundle());
        EventBus.getDefault().post(new MessageEvent("Hello everyone!"));
    }

    /**
     * Whenever you use any signup or login methods, the user is cached on disk.
     * You can treat this cache as a session, and automatically assume the user is logged in
     */
    private void CurrentUserLoggedIn(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            User_act.parseUser = currentUser;
            UserList_act.user = currentUser;
            startActivityForResult(new Intent(this, User_act.class), 9);
            this.finishAfterTransition();
        } else {
            return;
            // show the signup or login screen
        }
    }

    /**
     * If a user wants to reset a password
     * @param view
     */
    public void ForgotPassword(View view){
        startActivity(new Intent(Login_act.this, ResetPassword_act.class), ActivityOptions.makeSceneTransitionAnimation(Login_act.this).toBundle());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finishAfterTransition() {
        this.getWindow().setExitTransition(makeExitTransition());
        Log.d("Finishing Login", "Login");
        super.finishAfterTransition();
    }

    // This method will be called when a MessageEvent is posted
    public void onEvent(MessageEvent event){
        //Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
        Log.d("EVENT RECEIVED AT LOGIN WITH MESSAGE: ", event.message);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
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
        MainApp.getDefaultSharedPreferences()
                .edit()
                .putString("parseUser", user.getText().toString())
                .putString("password", pwd.getText().toString())
                .apply();
        //commit writes data to persistent data immediately while apply will handle it in the background
    }

    private Transition makeExitTransition() {
        TransitionSet exitTransition = new TransitionSet();

//        RelativeLayout subHeader = (RelativeLayout)findViewById(R.id.view_group);
//        RelativeLayout loginButtons = (RelativeLayout)findViewById(R.id.login_buttons);
//        RelativeLayout forgotPassword = (RelativeLayout)findViewById(R.id.login_forgot_password);
//        subHeader.setTransitionGroup(true);
//        loginButtons.setTransitionGroup(true);
//        forgotPassword.setTransitionGroup(true);
//
        exitTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        exitTransition.excludeTarget(android.R.id.statusBarBackground, true);
        exitTransition.excludeTarget(R.id.toolbar_teal, true);

        Transition autoTransition = new AutoTransition().setDuration(7000);

        Transition fade = new Fade().setDuration(5000);
        exitTransition.addTransition(fade);
        exitTransition.addTransition(autoTransition);

        exitTransition.setDuration(5000);
        return exitTransition;
    }

    private Transition makeReenterTransition() {
        TransitionSet enterTransition = new TransitionSet();
        LinearLayout mainLinearLayout = (LinearLayout)findViewById(R.id.main_linear_layout);
        Transition t = new Slide(Gravity.BOTTOM).setDuration(300);
        t.addTarget(mainLinearLayout);
        mainLinearLayout.setTransitionGroup(true);
//        enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
//        enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
//        enterTransition.excludeTarget(R.id.toolbar_teal, true);

        Transition slide = new Slide(Gravity.BOTTOM).setDuration(200);
        Transition fade = new Fade().setDuration(500);
//        enterTransition.addTransition(fade);
        enterTransition.addTransition(t);

        return enterTransition;
    }
}