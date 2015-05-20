package com.mycompany.loginapp.fragments;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.news.NewsFeed_act;
import com.mycompany.loginapp.profile.ProfilePrivate_act;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.adapters.NavigationRecyclerAdapter;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;
import com.mycompany.loginapp.eventMessages.MessageUpdateProfilePicture;
import com.mycompany.loginapp.singletons.MySingleton;
import com.mycompany.loginapp.chat.NewUserChat_act;
import com.mycompany.loginapp.chat.UserChatList_act;

import de.greenrobot.event.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {
    public static final String LOG = NavigationDrawerFragment.class.getSimpleName();

    /** Recycler */
    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private NavigationRecyclerAdapter mRecyclerAdapter;                        // Declaring Adapter For Recycler View
    private RecyclerView.LayoutManager mLayoutManager;

    private NavigationRecyclerAdapter navigationAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Runnable mRunnable;
    private static Handler mHandler;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View fragmentView;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLearnedDrawer = MySingleton.getMySingleton().getDefaultSharedPreferences().getBoolean(getString(R.string.user_learned_drawer), false);
        mHandler = new Handler();

        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment view
        View drawerLayout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        /** The RecyclerView for this NavigationDrawer */
        mRecyclerView = (RecyclerView)drawerLayout.findViewById(R.id.recyclerDrawerView);
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mRecyclerAdapter = new NavigationRecyclerAdapter(getActivity());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,navigation_header view name, navigation_header view email,
        // and navigation_header view profile picture
        mRecyclerView.setAdapter(mRecyclerAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(getActivity());         // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        /** OnItemTouchListener for the RecyclerView */
        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(position == 0) return;
                mDrawerLayout.closeDrawers();
                //Toast.makeText(getActivity(), "The Item Clicked is: " + position, Toast.LENGTH_SHORT).show();

                final int childViewPosition = position;
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        selectDrawerItem(childViewPosition); //Implement your switch case logic in this func
                    }
                };
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return drawerLayout;
    }

    public void setUpDrawer(int fragmentId, DrawerLayout drawerLayout) {
        this.mDrawerLayout = drawerLayout;
        this.fragmentView = getActivity().findViewById(fragmentId);

        this.mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer = true;
                    MySingleton.getMySingleton().getDefaultSharedPreferences().edit()
                            .putBoolean(getString(R.string.user_learned_drawer), mUserLearnedDrawer)
                            .apply();
                }
                //getSupportActionBar().setTitle("Navigation!");
                /** Redraws the toolbar */
                getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (mRunnable != null) {
                    mHandler.post(mRunnable);
                    mRunnable = null;
                }
                //getSupportActionBar().setTitle(mActivityTitle);
                getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        if(!mUserLearnedDrawer && !mFromSavedInstanceState){
            mDrawerLayout.openDrawer(this.fragmentView);
        }
        mDrawerLayout.setFitsSystemWindows(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
    }

    private void selectDrawerItem(int item){
        switch (item){
            case 1: // item is Home/User activity
                if(getActivity().getClass() == ProfilePrivate_act.class) break;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().startActivity(new Intent(getActivity(), ProfilePrivate_act.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                } else {
                    startActivity(new Intent(getActivity(), ProfilePrivate_act.class));
                }
                break;
            case 2: // item is profile not implemented yet
                if(getActivity().getClass() == NewsFeed_act.class) break;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().startActivity(new Intent(getActivity(), NewsFeed_act.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                } else {
                    startActivity(new Intent(getActivity(), NewsFeed_act.class));
                }
                break;
            case 3: // item is active chat user list
                if(getActivity().getClass() == UserChatList_act.class) break;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().startActivity(new Intent(getActivity(), UserChatList_act.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                } else {
                    startActivity(new Intent(getActivity(), UserChatList_act.class));
                }
                break;
            case 4: // item is create new chat
                if(getActivity().getClass() == NewUserChat_act.class) break;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().startActivity(new Intent(getActivity(), NewUserChat_act.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                } else {
                    getActivity().startActivity(new Intent(getActivity(), NewUserChat_act.class));
                }
                break;
            case 5: // item is settings - not implemented yet
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /** Event received when a new profile picture has been chosen
     *  User_act starts this event from the OnActivityResult() */
    public void onEvent(MessageUpdateProfilePicture newProfilePictureEvent){
        //mRecyclerAdapter.profilePicturePath = newProfilePictureEvent.imageUri;
        mRecyclerAdapter.updateRecyclerItem();
    }

    public ActionBarDrawerToggle getNavigationDrawerToggle(){
        return mDrawerToggle;
    }

//    public static interface ClickListener{
//
//        public void onClick(View view, int position);
//
//        public void onLongClick(View view, int position);
//    }
}
