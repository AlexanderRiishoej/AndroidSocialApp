package com.mycompany.loginapp.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;

import me.relex.circleindicator.CircleIndicator;

public class Startup_act extends AppCompatActivity {
    private ViewPager mViewPager;
    private StartupViewPagerAdapter mStartupViewPagerAdapter;
    private static final int FRAGMENT_0 = 0;
    private static final int FRAGMENT_1 = 1;
    private static final int FRAGMENT_2 = 2;
    private AQuery aQuery;
    private AppBarLayout mAppBarLayout;
    private int mFragmentIndex = -1;
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_act);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mStartupViewPagerAdapter = new StartupViewPagerAdapter(getSupportFragmentManager());
        this.mViewPager.setAdapter(mStartupViewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(mViewPager);
        CircleIndicator defaultIndicator = (CircleIndicator) findViewById(R.id.indicator_default);
        defaultIndicator.setViewPager(mViewPager);
        // Set the amount of pages to be stored in memory on either side of the current active page
        this.mViewPager.setOffscreenPageLimit(2);

        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //tabLayout.setupWithViewPager(this.mViewPager);
        this.mViewPager.setCurrentItem(0);
    }

    public void onForgotPasswordPressed(View view){
        startActivity(new Intent(this, ResetPassword_act.class));
    }

    /** Fragment pager adapter maintaining the fragments
     * Use the FragmentStatePagerAdapter when there are many pages, and u need to save the page data across different pages
     * Use FragmentPagerAdapter when there are few static pages */
    public class StartupViewPagerAdapter extends FragmentStatePagerAdapter {
        private String[] titles = new String[]{"Feed", "Chats"};
        final private int mNumOfTitles = 2;

        public StartupViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment tabFragment = null;

            switch (position){
                case FRAGMENT_0:
                    tabFragment = StartupFragment.newInstance();
                    break;
                case FRAGMENT_1:
                    tabFragment = StartupFragment.newInstance();
                    break;
            }

            return tabFragment;
        }

        // return title at that given position
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return mNumOfTitles;
        }
    }
}
