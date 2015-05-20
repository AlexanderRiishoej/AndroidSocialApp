package com.mycompany.loginapp.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.androidquery.AQuery;
import com.astuetz.PagerSlidingTabStrip;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.fragments.WallPostFragment;


public class NewsFeed_act extends BaseActivity{
    private ViewPager viewPager;

    private static final int FRAGMENT_0 = 0;
    private static final int FRAGMENT_1 = 1;
    private static final int FRAGMENT_2 = 2;
    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aQuery = new AQuery(this);
        aQuery.id(R.id.toolbar_title).text("News");
       // materialTabHost = (MaterialTabHost)findViewById(R.id.materialTabHost);
        viewPager = (ViewPager)findViewById(R.id.viewPager2);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        tabs.setShouldExpand(true);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.news;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_edit_act, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Fragment pager adapter maintaining the fragments
     * Use the FragmentStatePagerAdapter when there are many pages, and u need to save the page data across different pages
     * Use FragmentPagerAdapter when there are few static pages */
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private String[] tabs = new String[]{"Feed", "Suggestions", "Friends"};

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
                    tabFragment = WallPostFragment.newInstance("", "");
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
