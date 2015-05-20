package com.mycompany.loginapp.chat;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;
import com.mycompany.loginapp.adapters.UserChatListRecyclerAdapter;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
import com.mycompany.loginapp.eventMessages.MessageUserChat;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class UserChatList_act extends BaseActivity {

    public static final String LOG = UserChatList_act.class.getSimpleName();
    /**
     * The user.
     */
    public static ParseUser user;
    private UserAdapter userAdapter;
    private UserChatListRecyclerAdapter userChatListRecyclerAdapter;
    /** Recycler */
    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private RecyclerView.Adapter mRecyclerAdapter;                        // Declaring Adapter For Recycler View
    private RecyclerView.LayoutManager mLayoutManager;
    /**
     * The Chat list.
     */
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static AQuery aQuery;
    /**
     * The date of last message in conversation.
     */
    private ParseRelation<ParseObject> userChatRelation;
    private ArrayList<ParseObject> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //updateUserStatus(true);
        makeWindowTransition();
        aQuery = new AQuery(this);
        aQuery.id(R.id.toolbar_title).text("Chat users");
//        listView = aQuery.id(R.id.list).getListView();
//
//        userAdapter = new UserAdapter();
        initializeRecyclerView();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        getActiveUserChats();
    }

    private void initializeRecyclerView(){
        /** The RecyclerView for this NavigationDrawer */
        mRecyclerView = (RecyclerView)findViewById(R.id.userChatListRecyclerView);
        mRecyclerView.setHasFixedSize(false);                            // Letting the system know that the list objects are of fixed size
        //mRecyclerAdapter = new NavigationRecyclerAdapter(getActivity());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,navigation_header view name, navigation_header view email,
        // and navigation_header view profile picture
        mRecyclerView.setAdapter(mRecyclerAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);         // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        //recyclerViewAddOnItemClickListener();
        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(this, mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final int childViewPosition = position;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        EventBus.getDefault().postSticky(new MessageUserChat(userList.get(childViewPosition)));
                        startActivity(new Intent(UserChatList_act.this, Chat_act.class).
                                        putExtra(Constants.EXTRA_DATA, userList.get(childViewPosition).getParseUser("username").getUsername()),
                                ActivityOptions.makeSceneTransitionAnimation(UserChatList_act.this).toBundle());
                    } else {
                        EventBus.getDefault().postSticky(new MessageUserChat(userList.get(childViewPosition)));
                        startActivity(new Intent(UserChatList_act.this, Chat_act.class));
                    }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void removeUser(ParseUser p) {
        //friendsRelation.remove(p);
        user.saveInBackground();
    }

    /** Refreshes the content  */
    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                ParseUser testUser = new ParseUser();
//                testUser.setUsername("Torben");
//                testUser.setEmail("test@email.com");
//                userList.add(testUser);
//                userAdapter.notifyDataSetChanged();
                //loadUserList();
                refreshUserList();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.user_chat_list;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_new_chat:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(UserChatList_act.this, NewUserChat_act.class), ActivityOptions.makeSceneTransitionAnimation(UserChatList_act.this).toBundle());
                }
                else {
                    startActivity(new Intent(UserChatList_act.this, NewUserChat_act.class));
                }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * (non-Javadoc)
     * When the system destroys your activity, it calls the onDestroy() method for your Activity
     */
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        updateUserStatus(false);
        super.onDestroy();
    }

    /**
     * Be aware that the system calls this method every time your activity comes into the foreground,
     * including when it's created for the first time. As such, you should implement onResume() to initialize components
     * that you release during onPause() and perform any other initializations that must occur each time the activity enters the Resumed state
     */
    @Override
    protected void onResume() {
        super.onResume();
        //refreshUserList();
        //loadUserList();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Finishes this activity
     *
     * @param event - received when user presses Log Out
     */
    public void onEvent(MessageFinishActivities event) {
        //Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
        Log.d("CLOSE EVENT RECEIVED: ", "FINISHING USER_LIST");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.finishAfterTransition();
        } else finish();
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

    /** Gets all the active chats for the current logged in user */
    private void refreshUserList() {
        final ProgressDialog dia = ProgressDialog.show(this, null,
                getString(R.string.alert_loading));

        final List<ParseObject> userChatList = new ArrayList<ParseObject>();
        ParseQuery<ParseObject> parseObjectQuery = ParseQuery.getQuery("ChatUsers");
        parseObjectQuery.whereMatches("chatUserId", ParseUser.getCurrentUser().getUsername());
        parseObjectQuery.include("username");
        parseObjectQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseUserChatObjects, ParseException e) {
                dia.dismiss();
                if (e == null) {
                    if (parseUserChatObjects.size() > 0) {
                        userChatList.addAll(parseUserChatObjects);
                        Log.d(LOG, "Size of list of chat users: " + parseUserChatObjects.size());
                        Log.d(LOG, "Username: " + parseUserChatObjects.get(0).getParseUser("username").getUsername());
                        userList = new ArrayList<ParseObject>(userChatList);
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Utilities.showDialog(UserChatList_act.this, "No users for chat were found. Try reloading the page.");
                    }
                } else {
                    Utilities.showDialog(UserChatList_act.this, "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Gets the active chat associated with the current logged in user
     */
    private void getActiveUserChats() {
        final ProgressDialog dia = ProgressDialog.show(this, null,
                getString(R.string.alert_loading));

        final List<ParseObject> userChatList = new ArrayList<ParseObject>();
        ParseQuery<ParseObject> parseObjectQuery = ParseQuery.getQuery("ChatUsers");
        parseObjectQuery.whereMatches("chatUserId", ParseUser.getCurrentUser().getUsername());
        parseObjectQuery.include("username");
        parseObjectQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseUserChatObjects, ParseException e) {
                dia.dismiss();
                if (e == null) {
                    if (parseUserChatObjects.size() > 0) {
                        userChatList.addAll(parseUserChatObjects);
                        Log.d(LOG, "Size of list of chat users: " + parseUserChatObjects.size());
                        Log.d(LOG, "Username: " + parseUserChatObjects.get(0).getParseUser("username").getUsername());
                        loadUserList(userChatList);
                    } else {
                        Utilities.showDialog(UserChatList_act.this, "No users for chat were found. Try reloading the page.");
                    }
                } else {
                    Utilities.showDialog(UserChatList_act.this, "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Loads the users into the list of chat users
     * Sets the adapter
     * Sets the ItemOnClickListener
     */
    private void loadUserList(List<ParseObject> userChats) {
        userList = new ArrayList<ParseObject>(userChats);
        userChatListRecyclerAdapter = new UserChatListRecyclerAdapter(this, userList);
        mRecyclerView.setAdapter(userChatListRecyclerAdapter);
        //listView = (ListView) findViewById(R.id.list);
//        listView.setAdapter(userAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0,
//                                    View arg1, int position, long arg3) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    EventBus.getDefault().postSticky(new MessageUserChat(userList.get(position)));
//                    startActivity(new Intent(UserList_act.this, Chat_act.class).
//                                    putExtra(Constants.EXTRA_DATA, userList.get(position).getParseUser("username").getUsername()),
//                            ActivityOptions.makeSceneTransitionAnimation(UserList_act.this).toBundle());
//                } else {
//                    EventBus.getDefault().postSticky(new MessageUserChat(userList.get(position)));
//                    startActivity(new Intent(UserList_act.this, Chat_act.class));
//                }
//                //removeUser(userList.get(pos));
//                Log.d("VIEW: ", arg1.toString());
//                Log.d("POSITION: ", "" + position);
//                Log.d("LONG: ", "" + arg3);
//            }
//        });
    }

    private class UserAdapter extends BaseAdapter {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return userList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public ParseObject getItem(int arg0) {
            return userList.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            final ViewHolder viewHolder;
            final ParseObject userChat = getItem(position);

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.user_chat_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.username = (TextView) convertView.findViewById(R.id.user_list_username);
                viewHolder.profilePicture = (CircularImageView) convertView.findViewById(R.id.user_list_username_profile_parse_image);
                viewHolder.imageProgressBar = (ProgressBar) convertView.findViewById(R.id.user_list_image_progress_bar);
                viewHolder.lastChatMessage = (TextView) convertView.findViewById(R.id.user_list_recent_chat);
                viewHolder.dateOfLastChatMessage = (TextView) convertView.findViewById(R.id.user_list_message_date);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                Log.d("Recycling...", "ViewHolder recycling user list");
            }

            viewHolder.imageProgressBar.setVisibility(View.VISIBLE);
            Log.d(LOG, "Username: " + userChat.getParseUser("username").getUsername());

            viewHolder.username.setText(userChat.getParseUser("username").getUsername());

            userChat.getRelation(ParseConstants.CHAT_RELATION).getQuery().orderByDescending(ParseConstants.CREATED_AT).getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        Log.d(LOG, "ChatRelation: " + parseObject.getString("sender"));
                        Log.d(LOG, "ChatRelation: " + parseObject.getString("receiver"));
                        /** if the user chatting to, has the last  message in the chat equal to the sender, then show that message */
                        if (userChat.getParseUser("username").getUsername().equals(parseObject.getString("sender"))) {
                            viewHolder.lastChatMessage.setText("Last message received: " + parseObject.getString("message"));
                        }
                        /** else the last message is sent by me and should be showed as the last message seen in the conversation */
                        else if (userChat.getParseUser("username").getUsername().equals(parseObject.getString("receiver"))) {
                            viewHolder.lastChatMessage.setText("You: " + parseObject.getString("message"));
                        }
                        /** no messages has been sent */
                        else {
                            viewHolder.lastChatMessage.setText("");
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });

            viewHolder.dateOfLastChatMessage.setText(DateUtils.getRelativeDateTimeString(UserChatList_act.this, userChat.getUpdatedAt().getTime(),
                    DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));
            //viewHolder.username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chat_list_bitmap, 0);
            Log.d(LOG, "Parsefile Uri: " + userChat.getParseUser("username").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl());
            /** try - catch in order to prevent a crash in case of CircularImageView throwing a null pointer exception during bitmap loading */
            try {
                Picasso.with(UserChatList_act.this).load(userChat.getParseUser("username").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).fit().
                        into(viewHolder.profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {
                                //hide progressbar
                                viewHolder.imageProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                //hide progressbar
                                viewHolder.imageProgressBar.setVisibility(View.GONE);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }

    // ViewHolder to hold the id's of the views
    public static class ViewHolder {
        public TextView username;
        /**
         * Special circular imageView using a framework
         */
        public CircularImageView profilePicture;
        /**
         * ProgressBar for the small profile pictures in the list loading
         */
        public ProgressBar imageProgressBar;

        public TextView lastChatMessage;

        public TextView dateOfLastChatMessage;
    }

//    public static interface ClickListener{
//
//        public void onClick(View view, int position);
//
//        public void onLongClick(View view, int position);
//    }

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------------------
     * Window Transitions
     */
    private void makeWindowTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(makeEnterTransition());
            getWindow().setExitTransition(makeExitTransition());
            getWindow().setReenterTransition(makeReenterTransition());
            getWindow().setReturnTransition(makeReturnTransition());
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        }
    }

    private Transition makeEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet enterTransition = new TransitionSet();

            Transition t = new Slide(Gravity.TOP).setDuration(600);
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            enterTransition.excludeTarget(R.id.toolbar_teal, true);
            enterTransition.addTransition(t);

            Transition tt = new Fade();
            enterTransition.addTransition(tt).setDuration(1000);
            return enterTransition;
        } else return null;
    }

    private Transition makeExitTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet exitTransition = new TransitionSet();

            exitTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            exitTransition.excludeTarget(android.R.id.statusBarBackground, true);
            exitTransition.excludeTarget(R.id.toolbar_teal, true);
            Transition fade = new Fade();
            exitTransition.addTransition(fade);

            exitTransition.setDuration(500);
            return exitTransition;
        } else return null;
    }

    private Transition makeReenterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet enterTransition = new TransitionSet();
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            enterTransition.excludeTarget(R.id.toolbar_teal, true);

            Transition autoTransition = new AutoTransition().setDuration(400);
            Transition fade = new Fade().setDuration(400);

            Transition slideInFromLeft = new Slide(Gravity.LEFT);

            //enterTransition.addTransition(fade);
            //enterTransition.addTransition(autoTransition);
            enterTransition.addTransition(slideInFromLeft);
            return enterTransition;
        } else return null;
    }

    private Transition makeReturnTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet enterTransition = new TransitionSet();

            Transition upperPartSlide = new Slide(Gravity.LEFT);
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            enterTransition.excludeTarget(R.id.toolbar_teal, true);
            enterTransition.addTransition(upperPartSlide);

            Transition fade = new Fade();
            //enterTransition.addTransition(fade);

            enterTransition.setDuration(300);
            return enterTransition;
        } else return null;
    }
}
