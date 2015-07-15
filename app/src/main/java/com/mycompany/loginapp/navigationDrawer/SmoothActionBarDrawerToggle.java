package com.mycompany.loginapp.navigationDrawer;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Alexander on 15-07-2015.
 * A version of ActionBarDrawerToggle that listens to the state of the NavigationDrawer
 */
public class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

    private Runnable runnable;
    private Activity mActivity;

    public SmoothActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        this.mActivity = activity;
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        mActivity.invalidateOptionsMenu();
    }
    @Override
    public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
        mActivity.invalidateOptionsMenu();
    }
    @Override
    public void onDrawerStateChanged(int newState) {
        super.onDrawerStateChanged(newState);
        if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
            runnable.run();
            runnable = null;
        }
    }

    public void runWhenIdle(Runnable runnable) {
        this.runnable = runnable;
    }
}
