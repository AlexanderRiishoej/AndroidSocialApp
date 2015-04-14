package com.mycompany.loginapp.oldFiles;

/**
 * Created by Alexander on 11-04-2015.
 */
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mycompany.loginapp.NavigationDrawerFragment;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.NavigationRecyclerAdapter;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;

import de.greenrobot.event.EventBus;

public abstract class BaseActivityOld extends ActionBarActivity {
    public static final String LOG = BaseActivityOld.class.getSimpleName();

    private NavigationDrawerFragment navigationDrawerFragment;
    private Toolbar toolbar;
    private ListView mDrawerListView;
    private NavigationRecyclerAdapter navigationAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Runnable mRunnable;
    private static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        toolbar = (Toolbar) findViewById(R.id.toolbar_teal);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle(null);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //mDrawerListView = (ListView)findViewById(R.id.navList);
        if(mDrawerListView != null) {
            //mHandler = new Handler();
            //navigationAdapter = new NavigationAdapter(this);
            //mDrawerListView.setAdapter(navigationAdapter);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.teal_700));
            mDrawerLayout.setFitsSystemWindows(true);

            mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if(position == 0) return; // Header pressed, do nothing.
                    mDrawerLayout.closeDrawers();

                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            //selectDrawerItem(position); //Implement your switch case logic in this func
                        }
                    };
                }
            });
            setupDrawerToggle();
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().
                findFragmentById(R.id.fragment_navigation_drawer);
        if(navigationDrawerFragment != null && mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.teal_700));
            mDrawerLayout.setFitsSystemWindows(true);
//            setupDrawerToggle();
//            mDrawerToggle.setDrawerIndicatorEnabled(true);
//            mDrawerLayout.setDrawerListener(mDrawerToggle);
//            mDrawerToggle.syncState();
            navigationDrawerFragment.setUpDrawer(R.id.scrimInsetsFrameLayout, mDrawerLayout);
        }
    }

    protected abstract int getLayoutResource();

    protected void setActionBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }

    protected void setActionBarTitle(int titleId) {
        toolbar.setTitle(titleId);
    }

    protected void setDisplayHomeAsUpEnabled(boolean isEnabled){
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

        switch (id){
            case R.id.action_logout:
                EventBus.getDefault().post(new MessageFinishActivities());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Method called when selecting an item in the navigation drawer */
//    private void selectDrawerItem(int item){
//        switch (item){
//            case 1: // item is Home/User activity
//                if(this.getClass() == User_act.class) break;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    startActivity(new Intent(this, User_act.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//                } else {
//                    startActivity(new Intent(this, User_act.class));
//                }
//                break;
//            case 2: // item is profile not implemented yet
//                break;
//            case 3: // item is active chat user list
//                if(this.getClass() == UserList_act.class) break;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    startActivity(new Intent(this, UserList_act.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//                } else {
//                    startActivity(new Intent(this, UserList_act.class));
//                }
//                break;
//            case 4: // item is create new chat
//                if(this.getClass() == NewUserChat_act.class) break;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    startActivity(new Intent(this, NewUserChat_act.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//                } else {
//                    startActivity(new Intent(this, NewUserChat_act.class));
//                }
//                break;
//            case 5: // item is settings - not implemented yet
//                break;
//        }
//    }

//    private void addDrawerItems() {
//        String[] osArray = { "Android", "iOS", "Windows", "OS X", "Linux" };
//        navigationAdapter = new NavigationAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);
//        mDrawerListView.setAdapter(navigationAdapter);
//    }

    /** The ActionBarDrawerToggle ties together the NavigationDrawer, ToolBar and Activity so they work in sync */
    private void setupDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                //getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (mRunnable != null) {
                    mHandler.post(mRunnable);
                    mRunnable = null;
                }
                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG, "BaseActivity is destroyed. Belonging to context: " + this);
        super.onDestroy();
    }
}
