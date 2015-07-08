package com.mycompany.loginapp.chat;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.ChatListRecyclerAdapter;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageUserChat;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment {
    public static final String LOG = UserChatList_act.class.getSimpleName();
    public static ParseUser user;
    private ChatListRecyclerAdapter chatListRecyclerAdapter;
    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AQuery aQuery;
    private ArrayList<ParseObject> userList;
    private FloatingActionButton mFabButton;
    private boolean isRunning;
    // A list of DateHolders that represents the updated dates of the chats
    private ArrayList<DateHolder<Date>> dateHolderList;
    private Context actContext;
    private ProgressBar mProgressBar;

    public static ChatListFragment newInstance() {
        ChatListFragment fragment = new ChatListFragment();
        return fragment;
    }

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actContext  = getActivity();
        //EventBus.getDefault().register(this);
        aQuery = new AQuery(actContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        // Inflate the layout for this fragment
        mProgressBar = (ProgressBar) view.findViewById(R.id.chat_list_progress);
        mProgressBar.setVisibility(View.VISIBLE);
        //mFabButton = (ImageButton) findViewById(R.id.fabButton);
        initializeRecyclerView(view);
        userList = new ArrayList<>();
        chatListRecyclerAdapter = new ChatListRecyclerAdapter(actContext, userList);
        mRecyclerView.setAdapter(chatListRecyclerAdapter);
        initializeSwipeRefreshLayout(view);
        mFabButton = (FloatingActionButton) view.findViewById(R.id.fab);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        return view;
    }

    public void setSwipeRefreshingEnabled(boolean enabled){
        if(mSwipeRefreshLayout == null){
            return;
        }

        if(enabled){
            mSwipeRefreshLayout.setEnabled(true);
        }
        else{
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    private void initializeSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
//        mSwipeRefreshLayout.setProgressViewOffset(false, 180, 300);
    }

    private void initializeRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.userChatListRecyclerView);
        mRecyclerView.setHasFixedSize(false);                            // Letting the system know that the list objects are of fixed size
        //mRecyclerAdapter = new NavigationRecyclerAdapter(getActivity());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,navigation_header view name, navigation_header view email,
        // and navigation_header view profile_image picture
        mLayoutManager = new LinearLayoutManager(actContext);         // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(actContext));
        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(actContext, mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                if (position == Constants.TYPE_HEADER) {
//                    return;
//                }
                // only for testing purposes
//                if (position > 2) {
//                    return;
//                }
                final int childViewPosition = position; // minus position of header

                if (userList.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        EventBus.getDefault().postSticky(new MessageUserChat(userList.get(childViewPosition)));
//                        actContext.startActivity(new Intent(actContext, Chat_act.class).
//                                        putExtra(Constants.EXTRA_DATA, userList.get(childViewPosition).getParseUser(ParseConstants.USERNAME).getUsername()),
//                                ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                        actContext.startActivity(new Intent(actContext, Chat_act.class));
                    } else {
                        EventBus.getDefault().postSticky(new MessageUserChat(userList.get(childViewPosition)));
                        actContext.startActivity(new Intent(actContext, Chat_act.class));
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    /**
     * Refreshes the content
     */
    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshUserList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    /**
     * (non-Javadoc)
     * When the system destroys your activity, it calls the onDestroy() method for your Activity
     */
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Be aware that the system calls this method every time your activity comes into the foreground,
     * including when it's created for the first time. As such, you should implement onResume() to initialize components
     * that you release during onPause() and perform any other initializations that must occur each time the activity enters the Resumed state
     */
    @Override
    public void onResume() {
        super.onResume();
        isRunning = true;
        this.getActiveUserChats();
        //refreshUserList();
        //refreshUserList();
        //loadUserList();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.isRunning = false;
    }

    /**
     * Update user status.
     *
     * @param online true if user is online
     */
    private void updateUserStatus(boolean online) {
        user.put(ParseConstants.ONLINE, online);
        user.saveEventually();
    }

    /**
     * Gets all the active chats for the current logged in user
     */
    private void refreshUserList() {
//        final ProgressDialog dia = ProgressDialog.show(this, null,
//                getString(R.string.alert_loading));
        final List<ParseObject> userChatList = new ArrayList<ParseObject>();
        getUserChatListQuery().orderByDescending(ParseConstants.UPDATED_AT).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseUserChatObjects, ParseException e) {
//                dia.dismiss();
                if (e == null) {
                    if (parseUserChatObjects.size() > 0 && (parseUserChatObjects.size() > userList.size() || parseUserChatObjects.size() < userList.size())) {
                        userChatList.addAll(parseUserChatObjects);
                        userList = new ArrayList<ParseObject>(userChatList);
                        chatListRecyclerAdapter.setUserChatObjectList(userList);
                        chatListRecyclerAdapter.notifyItemRangeChanged(1, userList.size());
                        //userChatListRecyclerAdapter.notifyDataSetChanged();

                    }
                } else {
                    Utilities.showDialog(actContext, "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Gets the active chat associated with the current logged in user
     */
    private void getActiveUserChats() {
        getUserChatListQuery().orderByDescending(ParseConstants.UPDATED_AT).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseUserChatObjects, ParseException e) {
                mProgressBar.setVisibility(View.GONE);
                if (e == null) {
                    if (parseUserChatObjects.size() > 0) {
                        loadUserList(parseUserChatObjects);
                    } else {
                        Utilities.showDialog(actContext, "No users for chat were found. Try reloading the page.");
                    }
                } else {
                    Utilities.showDialog(actContext, "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Loads the users into the list of chat users
     */
    private void loadUserList(List<ParseObject> userChats) {
        // If the size of the two data sets are different it means that x-number of users has requested a new chat hence
        // the whole data set has to be notified, since i do not know how many is actively requesting to start a new chat
        if (userChats.size() != this.userList.size()) {
            userList = new ArrayList<ParseObject>(userChats);
            chatListRecyclerAdapter.setUserChatObjectList(userList);
            chatListRecyclerAdapter.notifyDataSetChanged();
            dateHolderList = new ArrayList<DateHolder<Date>>();
            for (ParseObject p : userChats) {
                dateHolderList.add(new DateHolder<Date>(p.getUpdatedAt()));
            }
        }
        // check and update the
        checkUpdatedChats(userChats);
        // update the list of chats every one second
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isRunning) {
                    getActiveUserChats();
                }
            }
        }, 1000);
    }

    /**
     * The query for the list of chats
     */
    private ParseQuery<ParseObject> getUserChatListQuery() {
        ParseQuery<ParseObject> parseObjectQuery = ParseQuery.getQuery(ParseConstants.CHAT_USERS);
//        parseObjectQuery.whereMatches("chatUserId", ParseUser.getCurrentUser().getUsername());
        parseObjectQuery.whereMatches(ParseConstants.CHAT_ID, ParseUser.getCurrentUser().getObjectId());
        parseObjectQuery.include(ParseConstants.USERNAME);
        parseObjectQuery.include(ParseConstants.CREATED_BY);

        return parseObjectQuery;
    }

    /**
     * Checks whether or not, the current list of chats needs to be updated due to new messages, new chat requests etc.
     * Iterates through the newly updated list of chats received by Parse and then checks for each old item in
     * the current list of chats, if anything has changed and needs to be updated.
     * Hence the items position and id relative to the old list, has to be checked
     */
    private void checkUpdatedChats(List<ParseObject> userChats) {

        // iterate through each element in the list of updated userchats
        for (int i = 0; i < userChats.size(); i++) {
            // save the object id of the item of the newly updated list from Parse
            String newListObjectId = userChats.get(i).getObjectId();
            // now iterate through the old user chatlist in order to check for every item in the old list
            for (int j = 0; j < userList.size(); j++) {
                // save the object id of the item of the old list in order to compare it with the object id of the new
                String oldListObjectId = userList.get(j).getObjectId();
                // check if any of the old object ids matches the object id of the updated list
                // if they match, and their positions, relative to the lists, are different, then it means that
                // the position of the element in the old list has been updated to the one of the new list.
                if (oldListObjectId.equals(newListObjectId) && i != j) {
                    // notify that item has changed and should be moved in the adapter to the top
                    chatListRecyclerAdapter.notifyItemChanged(j); // j+1 due to header
                    chatListRecyclerAdapter.notifyItemMoved(j, 0); // j+1 due to header
                    // remove the item from its old position in the list and current dateHolder corresponding to the current chat list
                    userList.remove(j);
                    dateHolderList.remove(j);
                    // insert item at its new position which is at the top of the list
                    userList.add(0, userChats.get(i));
                    dateHolderList.add(0, new DateHolder<Date>(userChats.get(i).getUpdatedAt()));
                    // update the user chatlist of the adapter in order to keep the adapters list up-to-date
                    chatListRecyclerAdapter.setUserChatObjectList(userList);
                }
                // if the position is 0 and the object ids match and the updated date is after the one in the old list
                // then make sure to update this item in the list
                if (oldListObjectId.equals(newListObjectId) && i == 0 && j == 0 &&
                        userChats.get(0).getUpdatedAt().after(dateHolderList.get(0).getLastUpdated())) {
                    {
                        userList.set(0, userChats.get(0));
                        dateHolderList.add(0, new DateHolder<Date>(userChats.get(0).getUpdatedAt()));
                        chatListRecyclerAdapter.notifyItemChanged(0);
                    }
                }
                // if the two object ids are the same and the are placed on the same position in the list, it means that they have not changed hence
                // no further actions should be taken
                else if (oldListObjectId.equals(newListObjectId) && i == j) {
                    break;
                }
            }
        }
    }
}