package com.mycompany.loginapp.base;

/**
 * Created by Alexander on 06-02-2015.
 */

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.login.Login2_act;
import com.mycompany.loginapp.navigationDrawer.NavigationDrawerFragment;
import com.mycompany.loginapp.profile.ProfileImageHolder;
import com.parse.ParseUser;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String LOG = BaseActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment navigationDrawerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        this.getToolbar();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //mToolbar = (Toolbar) findViewById(R.id.toolbar_teal);
        if (mToolbar != null) {
            //setSupportActionBar(mToolbar);
            setTitle(null); //mToolbar.setTitle(null);

            if (mDrawerLayout != null) {
                mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
            } else {
                mToolbar.setNavigationIcon(null);
            }
        }

        if (mDrawerLayout != null) {
            //setup the navigation drawer fragment
            navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().
                    findFragmentById(R.id.fragment_navigation_view);
            if (navigationDrawerFragment != null && mDrawerLayout != null) {
                navigationDrawerFragment.setUpDrawer(mDrawerLayout);
            }
        }
    }

    protected abstract int getLayoutResource();

    // Setting and getting the toolbar
    protected Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_teal);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }
        return this.mToolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        if (id == android.R.id.home && mDrawerLayout == null) {
            this.onBackPressed();
            return true;
        }

        if (navigationDrawerFragment.getNavigationDrawerToggle().onOptionsItemSelected(item) && mDrawerLayout != null) {
            return true;
        }

        switch (id) {
            case R.id.action_logout:
                ParseUser.logOut();
                ProfileImageHolder.setImageFilesNull();
                //EventBus.getDefault().post(new MessageFinishActivities());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //startActivity(new Intent(this, Login_act.class));
                    startActivity(new Intent(this, Login2_act.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                    this.finish();
                } else {
//                    startActivity(new Intent(this, Login_act.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    startActivity(new Intent(this, Login2_act.class));
                    this.finish();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG, "BaseActivity is destroyed. Belonging to context: " + this);
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (navigationDrawerFragment != null) {
            navigationDrawerFragment.getNavigationDrawerToggle().syncState();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(navigationDrawerFragment.getNavigationView())) {
            mDrawerLayout.closeDrawer(navigationDrawerFragment.getNavigationView());
        } else {
            super.onBackPressed();
        }
    }
}
