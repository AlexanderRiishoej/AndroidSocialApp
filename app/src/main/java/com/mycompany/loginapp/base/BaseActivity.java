package com.mycompany.loginapp.base;

/**
 * Created by Alexander on 06-02-2015.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.navigationDrawer.NavigationDrawerFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String LOG = BaseActivity.class.getSimpleName();

    private Toolbar mToolbar;
    @Nullable @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);
        this.getToolbar();
        //mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
            mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().
                    findFragmentById(R.id.fragment_navigation_view);
            if (mNavigationDrawerFragment != null && mDrawerLayout != null) {
                mNavigationDrawerFragment.setUpDrawer(mDrawerLayout);
            }
        }
    }

    protected abstract int getLayoutResource();

    // Setting and getting the toolbar
    protected Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = ButterKnife.findById(this, R.id.toolbar_teal);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }
        return this.mToolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        if (id == android.R.id.home && mDrawerLayout == null) {
            this.onBackPressed();
            return true;
        }

        if (mNavigationDrawerFragment.getNavigationDrawerToggle().onOptionsItemSelected(item) && mDrawerLayout != null) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG, "BaseActivity is destroyed. Belonging to context: " + this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.getNavigationDrawerToggle().syncState();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mNavigationDrawerFragment.getNavigationView())) {
            mDrawerLayout.closeDrawer(mNavigationDrawerFragment.getNavigationView());
        } else {
            super.onBackPressed();
        }
    }
}
