package com.mycompany.loginapp.observable;

/**
 * Created by Alexander on 22-05-2015.
 * https://github.com/ksoichiro/Android-ObservableScrollView/blob/master/docs/basic/translating-toolbar.md
 * Base class for classes using the Observable Toolbar control
 */

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
import com.mycompany.loginapp.fragments.NavigationDrawerFragment;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import de.greenrobot.event.EventBus;

public abstract class ToolbarControlBaseActivity<S extends Scrollable> extends AppCompatActivity implements ObservableScrollViewCallbacks {
    public static final String LOG = BaseActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private S mScrollable;
    private NavigationDrawerFragment navigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            setTitle(null);
            if (mDrawerLayout != null) {
                mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
            } else {
                mToolbar.setNavigationIcon(null);
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

        mScrollable = createScrollable();
        mScrollable.setScrollViewCallbacks(this);
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

    //------------------------------------------------------------ Toolbar control part

    // Gets the height of the statusbar
    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // Get the height of the screen, minus the statusBar height in order to make up for the
    // 'android:fitsSystemWindows="true"' in the layout file
    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight() - getStatusBarHeight();
    }

    protected abstract int getLayoutResId();

    protected abstract S createScrollable();

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        Log.e("DEBUG", "onUpOrCancelMotionEvent: " + scrollState);
        if (scrollState == ScrollState.UP) {
            if (toolbarIsShown()) {
                hideToolbar();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbarIsHidden()) {
                showToolbar();
            }
        }
    }

    private boolean toolbarIsShown() {
        return mToolbar.getTranslationY() == 0;
    }

    private boolean toolbarIsHidden() {
        return mToolbar.getTranslationY() == -mToolbar.getHeight();
    }

    private void showToolbar() {
        moveToolbar(0);
    }

    private void hideToolbar() {
        moveToolbar(-mToolbar.getHeight());
    }

    private void moveToolbar(float toTranslationY) {
//        if (ViewHelper.getTranslationY(mToolbar) == toTranslationY) {
//            return;
//        }
        if (mToolbar.getTranslationY() == toTranslationY) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(mToolbar.getTranslationY(), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                //ViewHelper.setTranslationY(mToolbar, translationY);
                mToolbar.setTranslationY(translationY);
                //ViewHelper.setTranslationY((View) mScrollable, translationY);
                ((View) mScrollable).setTranslationY(translationY);
                //mToolbar.setTranslationY(mScrollable.getCurrentScrollY());
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
                lp.height = (int) -translationY + getScreenHeight() - lp.topMargin;
                ((View) mScrollable).requestLayout();
            }
        });
        animator.start();
    }
}