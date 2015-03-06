package com.mycompany.loginapp;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
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
import android.widget.TextView;

import com.androidquery.AQuery;
import com.parse.ParseUser;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class UserActivity extends BaseActivity {

    // User
    private TextView user;

    /**
     * The user.
     */
    public static ParseUser username;

    private static AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setEnterTransition(makeEnterTransition());
        getWindow().setExitTransition(makeExitTransition());
        getWindow().setReenterTransition(makeReenterTransition());
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        aQuery = new AQuery(this);

        // Get the TexView and assign the username to it
//        user = (TextView) findViewById(R.id.signedInAs);
//        user.setText(username.getUsername());
        aQuery.id(R.id.signedInAs).text(username.getUsername());
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
                startActivity(new Intent(UserActivity.this, UserList.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;
            case R.id.action_logout:
                ParseUser.logOut();
                //this.finish();
                //Clear top: Clears the entire stack except for the activity being launched
                // http://stackoverflow.com/questions/3007998/on-logout-clear-activity-history-stack-preventing-back-button-from-opening-l
                startActivity(new Intent(UserActivity.this, Login.class).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP));
                this.finishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Transition makeEnterTransition() {
        TransitionSet enterTransition = new TransitionSet();

        Transition t = new Slide(Gravity.TOP).setDuration(600);
        enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.excludeTarget(R.id.toolbar_teal, true);
        enterTransition.addTransition(t);

        Transition tt = new Fade();
        enterTransition.addTransition(tt).setDuration(1000);
        return enterTransition;
    }

    private Transition makeExitTransition() {
        TransitionSet exitTransition = new TransitionSet();

        exitTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        exitTransition.excludeTarget(android.R.id.statusBarBackground, true);
        exitTransition.excludeTarget(R.id.toolbar_teal, true);
        Transition fade = new Fade();
        exitTransition.addTransition(fade);

        exitTransition.setDuration(500);
        return exitTransition;
    }

    private Transition makeReenterTransition() {
        TransitionSet enterTransition = new TransitionSet();
        enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.excludeTarget(R.id.toolbar_teal, true);

        Transition autoTransition = new AutoTransition().setDuration(700);
        Transition fade = new Fade().setDuration(800);
        enterTransition.addTransition(fade);
        enterTransition.addTransition(autoTransition);
        return enterTransition;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
