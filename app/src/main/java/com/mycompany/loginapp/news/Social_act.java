package com.mycompany.loginapp.news;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.chat.ChatListFragment;
import com.mycompany.loginapp.chat.NewUserChat_act;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;

import de.greenrobot.event.EventBus;


public class Social_act extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private static final int FRAGMENT_0 = 0;
    private static final int FRAGMENT_1 = 1;
    private static final int FRAGMENT_2 = 2;
    private AQuery aQuery;
    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aQuery = new AQuery(this);
        aQuery.id(R.id.toolbar_title).text("Social");
        EventBus.getDefault().register(this);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        mViewPager = (ViewPager)findViewById(R.id.viewPager);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        // Set the amount of pages to be stored in memory on either side of the current active page
        mViewPager.setOffscreenPageLimit(3);

        // Bind the tabs to the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.social;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_new_chat:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(Social_act.this, NewUserChat_act.class),
                            ActivityOptions.makeSceneTransitionAnimation(Social_act.this).toBundle());
                } else {
                    startActivity(new Intent(Social_act.this, NewUserChat_act.class));
                }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Finishes this activity
     *
     * @param event - received when user presses Log Out
     */
    public void onEvent(MessageFinishActivities event) {
        Log.d(LOG, "FINISHED");
        this.finish();
    }

    /** Gets the current fragment associated with the viewPagerAdapter and sets its scrolling enabled/disabled, depending on the amount scrolled */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        //http://stackoverflow.com/questions/7379165/update-data-in-listfragment-as-part-of-viewpager/8886019#8886019
        Fragment currentFragment = (Fragment) mViewPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
        int index = mViewPager.getCurrentItem();

            if(i == 0) {
                switch (index){
                    case 0:
                        WallPostFragment w = (WallPostFragment)currentFragment;
                        w.setSwipeRefreshingEnabled(true);
                        return;
                    case 1:
                        ChatListFragment c = (ChatListFragment)currentFragment;
                        c.setSwipeRefreshingEnabled(true);
                        return;
                    case 2:
                        return;
                }
        } else {
                switch (index){
                    case 0:
                        WallPostFragment w = (WallPostFragment)currentFragment;
                        w.setSwipeRefreshingEnabled(false);
                        return;
                    case 1:
                        ChatListFragment c = (ChatListFragment)currentFragment;
                        c.setSwipeRefreshingEnabled(false);
                        return;
                    case 2:
                        return;
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    /** Fragment pager adapter maintaining the fragments
     * Use the FragmentStatePagerAdapter when there are many pages, and u need to save the page data across different pages
     * Use FragmentPagerAdapter when there are few static pages */
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private String[] tabs = new String[]{"Feed", "Chats", "Friends"};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment tabFragment = null;

            switch (position){
                case FRAGMENT_0:
                    tabFragment = WallPostFragment.newInstance("", "");
                    break;
                case FRAGMENT_1:
                    tabFragment = ChatListFragment.newInstance();
                    break;
                case FRAGMENT_2:
                    tabFragment = WallPostFragment.newInstance("", "");
                    break;
            }

            return tabFragment;
        }

        // return title at that given position
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
