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
import android.view.View;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.chat.ChatListFragment;
import com.mycompany.loginapp.chat.NewUserChat_act;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
import com.mycompany.loginapp.fragmentFactory.FragmentFactory;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


public class Social_act extends BaseActivity {
    @Bind(R.id.viewPager) ViewPager mViewPager;
    @Bind(R.id.appbar) AppBarLayout mAppBarLayout;
    private ViewPagerAdapter mViewPagerAdapter;
    private static final int FRAGMENT_0 = 0;
    private static final int FRAGMENT_1 = 1;
    private static final int FRAGMENT_2 = 2;
    private int mFragmentIndex = -1;
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getToolbar().setTitle("Social");
        EventBus.getDefault().register(this);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        // Set the amount of pages to be stored in memory on either side of the current active page
        mViewPager.setOffscreenPageLimit(3);

        // Bind the tabs to the ViewPager
        TabLayout tabLayout = ButterKnife.findById(this, R.id.tabs);
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
        this.setViewPagerScrollListener();
        this.setAppBarScrollListener();
    }

    private void setAppBarScrollListener() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if(i >= 0){
                    enableDisableSwipeRefresh(true);
                }
                else {
                    enableDisableSwipeRefresh(false);
                }
            }
        });
    }

    private void setViewPagerScrollListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
    }

    private void enableDisableSwipeRefresh(boolean b) {
        final int index = mViewPager.getCurrentItem();
        if (index != mFragmentIndex) {
        mFragmentIndex = index;
        mCurrentFragment = (Fragment) mViewPagerAdapter.instantiateItem(mViewPager, mFragmentIndex);
    }
            if(mCurrentFragment instanceof WallPostFragment){
                ((WallPostFragment)mCurrentFragment).setSwipeRefreshingEnabled(b);
            }
            else if(mCurrentFragment instanceof ChatListFragment){
                ((ChatListFragment)mCurrentFragment).setSwipeRefreshingEnabled(b);
            }
            else {
                ((NewChatFragment)mCurrentFragment).setSwipeRefreshingEnabled(b);
            }
//        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_social;
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

    @Override
    protected void onPause() {
        super.onPause();
//        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mAppBarLayout.addOnOffsetChangedListener(this);
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
                    tabFragment = FragmentFactory.getFragmentFactory().getFragment(WallPostFragment.class.getSimpleName());
                    break;
                case FRAGMENT_1:
                    tabFragment = FragmentFactory.getFragmentFactory().getFragment(ChatListFragment.class.getSimpleName());
                    break;
                case FRAGMENT_2:
                    tabFragment = FragmentFactory.getFragmentFactory().getFragment(NewChatFragment.class.getSimpleName());
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
