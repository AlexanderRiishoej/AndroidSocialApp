package com.mycompany.loginapp.news;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.itemDecorators.FriendsDividerItemDecoration;
import com.mycompany.loginapp.chat.StartNewChatClass;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.FriendsClickListener;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageFindFriends;
import com.mycompany.loginapp.itemDecorators.NewChatDividerItemDecoration;
import com.mycompany.loginapp.profile.ProfilePublic_act;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewChatFragment extends Fragment {
    public static final String LOG = NewChatFragment.class.getSimpleName();
    @Bind(R.id.finds_friends_recyclerView) RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NewChatRecyclerAdapter mFriendsRecyclerAdapter;
    @Bind(R.id.find_friends_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.fab) FloatingActionButton mFabButton;
    private Context mActivityContext;
    private ProgressBar mProgressBar;
    private List<ParseUser> mFriendsList;

    public static NewChatFragment newInstance() {
        final NewChatFragment fragment = new NewChatFragment();
        return fragment;
    }

    public NewChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityContext = getActivity();
        //EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_chat, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        mProgressBar = (ProgressBar) view.findViewById(R.id.find_friends_progress);
        //mFabButton = (ImageButton) findViewById(R.id.fabButton);
        this.initializeRecyclerView(view);
        mFriendsRecyclerAdapter = new NewChatRecyclerAdapter(mActivityContext, this.mFriendsList,
                new ClickListener() { //clickListener for the buttons inside every layout row
                    @Override
                    public void onClick(View view, int position) {
                        final StartNewChatClass startNewChatClass = new StartNewChatClass(mActivityContext);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            //gets the ParseUser clicked from the Adapter
                            startNewChatClass.startUserChatQuery(mFriendsRecyclerAdapter.getParseUser(position));
                        }
                        new MaterialDialog.Builder(mActivityContext)
                                .title("Chat clicked")
                                .show();
                    }

                    @Override
                    public void onLongClick(View view, int position) {}
                },
                new FriendsClickListener() { //clickListener for the entire main layout of the recyclerViews row
                    @Override
                    public void onClick(int position) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            EventBus.getDefault().postSticky(new MessageFindFriends(mFriendsRecyclerAdapter.getParseUser(position)));
                            mActivityContext.startActivity(new Intent(mActivityContext, ProfilePublic_act.class));
                        } else {
                            EventBus.getDefault().postSticky(new MessageFindFriends(mFriendsRecyclerAdapter.getParseUser(position)));
                            mActivityContext.startActivity(new Intent(mActivityContext, ProfilePublic_act.class));
                        }
                    }

                    @Override
                    public void onLongClick(int position) {}
                });

        mRecyclerView.setAdapter(mFriendsRecyclerAdapter);
        this.getUsers();
        this.initializeSwipeRefreshLayout(view);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return view;
    }

    private void initializeSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    /** Method used by the Social_act class in order to enable/disable the SwipeRefreshLayout depending on the amount scrolled */
    public void setSwipeRefreshingEnabled(boolean enabled) {
        if (mSwipeRefreshLayout == null) {
            return;
        }

        if (enabled) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    private void initializeRecyclerView(View view) {
        mRecyclerView.requestDisallowInterceptTouchEvent(true);

        mRecyclerView.setHasFixedSize(false);                            // Letting the system know that the list objects are of fixed size
        mLayoutManager = new LinearLayoutManager(mActivityContext);         // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new NewChatDividerItemDecoration(mActivityContext));
        this.setItemTouchHelper();
        //not using the recyclers onClickListener since this recyclerView has different childViews with different independent clicks
//        final RecyclerFindFriendsOnTouchListener mRecyclerFindFriendsOnTouchListener = new RecyclerFindFriendsOnTouchListener(mActivityContext, mRecyclerView, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                final int childViewPosition = position; // minus position of header
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    EventBus.getDefault().postSticky(new MessageFindFriends(mFriendsRecyclerAdapter.getParseUser(childViewPosition)));
//                    mActivityContext.startActivity(new Intent(mActivityContext, ProfilePublic_act.class));
//                } else {
//                    EventBus.getDefault().postSticky(new MessageFindFriends(mFriendsRecyclerAdapter.getParseUser(childViewPosition)));
//                    mActivityContext.startActivity(new Intent(mActivityContext, ProfilePublic_act.class));
//                }
//            }
//
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        });
//        mRecyclerView.addOnItemTouchListener(mRecyclerFindFriendsOnTouchListener);
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

    @Override
    public void onResume() {
        super.onResume();
        //mProgressBar.setVisibility(View.VISIBLE);
        //this.getUsers();
    }

    /**
     * Gets all the parseUsers registered with this app except the current user logged in and sets the Adapters list
     */
    private void getUsers() {
        getUsersQuery().whereNotEqualTo(ParseConstants.USERNAME, ApplicationMain.mCurrentParseUser.getUsername()).
                orderByDescending(ParseConstants.CREATED_AT).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUserChatObjects, ParseException e) {
                mProgressBar.setVisibility(View.GONE);
                if (e == null) {
                    if (parseUserChatObjects.size() > 0) {
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

    /**
     * The query for the list of chats
     * Somehow this query is messing up the session
     */
    private ParseQuery<ParseUser> getUsersQuery() {
        return ParseUser.getQuery();
    }

}
