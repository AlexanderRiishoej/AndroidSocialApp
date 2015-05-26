package com.mycompany.loginapp.MaterialTabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.fragments.WallPostFragment;

/**
 * Created by Alexander on 24-05-2015.
 */
public class MainTab_act extends BaseActivity {
    private static final int FRAGMENT_0 = 0;
    private static final int FRAGMENT_1 = 1;
    private static final int FRAGMENT_2 = 2;

    private MaterialViewPager mViewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(null);

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        toolbar = mViewPager.getToolbar();
        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.getViewPager().setAdapter(viewPagerAdapter);

        // Set the amount of pages to be stored in memory on either side of the current active page
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
        mViewPager.getViewPager().setCurrentItem(0);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.material_tabs_main;
    }

    /** Fragment pager adapter maintaining the fragments
     * Use the FragmentStatePagerAdapter when there are many pages, since Fragments are not saved in memory
     * Use FragmentPagerAdapter when there are few static pages, and pages are saved in memory */
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private String[] tabs = new String[]{"Feed", "Suggestions", "Friends"};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        int oldPosition = -1;
        @Override
        public Fragment getItem(int position) {
            Fragment tabFragment = null;

            switch (position){
                case FRAGMENT_0:
                    tabFragment = FragmentTab_frag.newInstance("", "");
                    break;
                case FRAGMENT_1:
                    tabFragment = FragmentTab_frag.newInstance("", "");
                    break;
                case FRAGMENT_2:
                    tabFragment = FragmentTab_frag.newInstance("", "");
                    break;
            }

            return tabFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            //only if position changed
            if (position == oldPosition)
                return;
            oldPosition = position;

            int color = 0;
            String imageUrl = "";
            switch (position) {
                case 0:
                    imageUrl = "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg";
                    color = getResources().getColor(R.color.blue);
                    break;
                case 1:
                    imageUrl = "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg";
                    color = getResources().getColor(R.color.green);
                    break;
                case 2:
                    imageUrl = "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg";
                    color = getResources().getColor(R.color.cyan);
                    break;
                case 3:
                    imageUrl = "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg";
                    color = getResources().getColor(R.color.red);
                    break;
            }

            final int fadeDuration = 400;
            mViewPager.setImageUrl(imageUrl, fadeDuration);
            mViewPager.setColor(color, fadeDuration);

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
