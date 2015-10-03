package com.mycompany.loginapp.friends.find_friends;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.FriendsClickListener;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageFindFriends;
import com.mycompany.loginapp.friends.IFriendsRequest;
import com.mycompany.loginapp.itemDecorators.FriendsDividerItemDecoration;
import com.mycompany.loginapp.profile.ProfilePublic_act;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFriendsFragment extends Fragment implements SearchView.OnQueryTextListener {
    public static final String LOG = FindFriendsFragment.class.getSimpleName();
    @Bind(R.id.finds_friends_recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.find_friends_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.find_friends_progress) ProgressBar mProgressBar;
    private FriendsRecyclerAdapter mFriendsRecyclerAdapter;
    private Context mActivityContext;
    private List<ParseUser> mFriendsList;

    public FindFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mFragmentView = inflater.inflate(R.layout.fragment_find_friends, container, false);
        ButterKnife.bind(this, mFragmentView);
        mProgressBar.setVisibility(View.VISIBLE);

        this.initializeRecyclerView();
        //this.mFriendsList = new ArrayList<>();
        mFriendsRecyclerAdapter = new FriendsRecyclerAdapter(mActivityContext, this.mFriendsList,
                new ClickListener() { //clickListener for the buttons inside every layout row
                    @Override
                    public void onClick(View view, int position) {
                        IFriendsRequest friendRequestClass = FriendRequestFactory.getFriendRequestFactory().getFriendsRequestClass(getActivity());
                        final int clickedPosition = position - 1;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            friendRequestClass.sendFriendRequest(mFriendsRecyclerAdapter.getParseUser(clickedPosition));
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {}
                },
                new FriendsClickListener() { //clickListener for the entire main layout of the recyclerViews row
                    @Override
                    public void onClick(int position) {
                        final int clickedPosition = position - 1;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            EventBus.getDefault().postSticky(new MessageFindFriends(mFriendsRecyclerAdapter.getParseUser(clickedPosition)));
                            mActivityContext.startActivity(new Intent(mActivityContext, ProfilePublic_act.class));
                        } else {
                            EventBus.getDefault().postSticky(new MessageFindFriends(mFriendsRecyclerAdapter.getParseUser(clickedPosition)));
                            mActivityContext.startActivity(new Intent(mActivityContext, ProfilePublic_act.class));
                        }
                    }

                    @Override
                    public void onLongClick(int position) {}
                });
        this.mFriendsList = new ArrayList<>();
        mRecyclerView.setAdapter(mFriendsRecyclerAdapter);
        this.getUsers();
        this.initializeSwipeRefreshLayout();

        return mFragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void initializeSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    private void initializeRecyclerView() {
        mRecyclerView.requestDisallowInterceptTouchEvent(true);
        mRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivityContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new FriendsDividerItemDecoration(mActivityContext));
        this.setItemTouchHelper();
    }

    private void setItemTouchHelper() {
        final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return 0;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * Refreshes the content
     */
    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //refreshUserList();
                getUsers();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    /**
     * Gets all the parseUsers registered with this app except the current user logged in and sets the Adapters list
     */
    private void getUsers() {
        ParseUser.getQuery().whereNotEqualTo(ParseConstants.USERNAME, ApplicationMain.mCurrentParseUser.getUsername()).
                orderByDescending(ParseConstants.CREATED_AT).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUserChatObjects, ParseException e) {
                mProgressBar.setVisibility(View.GONE);
                if (e == null) {
                    if (parseUserChatObjects.size() > 0) {
                        mFriendsList.addAll(parseUserChatObjects);
                        mFriendsRecyclerAdapter.setChatList(parseUserChatObjects);
                    } else {
                        Utilities.showDialog(mActivityContext, "No users for chat were found. Try reloading the page.");
                    }
                } else {
                    Utilities.showDialog(mActivityContext, "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_find_friends_act, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<ParseUser> mFilteredFriendsList = filterFriendsList(this.mFriendsList, query);
        mFriendsRecyclerAdapter.animateTo(mFilteredFriendsList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    private List<ParseUser> filterFriendsList(List<ParseUser> mAdapterFriendsList, String query) {
        query = query.toLowerCase();

        final List<ParseUser> filteredFriendsList = new ArrayList<>();
        for (ParseUser parseUser : mAdapterFriendsList) {
            final String username = parseUser.getUsername().toLowerCase();

            if (username.contains(query)) {
                filteredFriendsList.add(parseUser);
            }
        }
        return filteredFriendsList;
    }
}
