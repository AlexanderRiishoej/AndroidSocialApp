package com.mycompany.loginapp.base;

/**
 * Created by Alexander on 06-02-2015.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycompany.loginapp.fragments.NavigationDrawerFragment;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
import com.mycompany.loginapp.tabs.SlidingTabLayout;

import de.greenrobot.event.EventBus;

public abstract class BaseActivity extends ActionBarActivity {
    public static final String LOG = BaseActivity.class.getSimpleName();

    private NavigationDrawerFragment navigationDrawerFragment;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar_teal);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle(null);
            if (mDrawerLayout != null) {
                toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
            } else {
                toolbar.setNavigationIcon(null);
            }
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.teal_700));

            navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().
                    findFragmentById(R.id.fragment_navigation_drawer);
            if (navigationDrawerFragment != null && mDrawerLayout != null) {
                navigationDrawerFragment.setUpDrawer(R.id.scrimInsetsFrameLayout, mDrawerLayout);
            }
        }
    }

    protected abstract int getLayoutResource();

    protected void setActionBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }

    protected void setActionBarTitle(int titleId) {
        toolbar.setTitle(titleId);
    }

    protected void setDisplayHomeAsUpEnabled(boolean isEnabled) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(isEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        if (navigationDrawerFragment.getNavigationDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        switch (id) {
            case R.id.action_logout:
                EventBus.getDefault().post(new MessageFinishActivities());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG, "BaseActivity is destroyed. Belonging to context: " + this);
        super.onDestroy();
    }
}
