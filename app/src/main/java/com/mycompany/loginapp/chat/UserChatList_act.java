package com.mycompany.loginapp.chat;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.UserChatListRecyclerAdapter;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
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

//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class UserChatList_act extends BaseActivity {

    public static final String LOG = UserChatList_act.class.getSimpleName();
    /**
     * The user.
     */
    public static ParseUser user;
    private UserChatListRecyclerAdapter userChatListRecyclerAdapter;
    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AQuery aQuery;
    private ArrayList<ParseObject> userList;
    private ImageButton mFabButton;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeWindowTransition();
        aQuery = new AQuery(this);
        aQuery.id(R.id.toolbar_title).text("Chat users");
        mFabButton = (ImageButton) findViewById(R.id.fabButton);
        initializeRecyclerView();
        userList = new ArrayList<>();
        userChatListRecyclerAdapter = new UserChatListRecyclerAdapter(this, userList);
        mRecyclerView.setAdapter(userChatListRecyclerAdapter);
        initializeSwipeRefreshLayout();
    }

    private void initializeSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        swipeRefreshLayout.setProgressViewOffset(false, 180, 300);
    }

    private void initializeRecyclerView(){
        mRecyclerView = (RecyclerView)findViewById(R.id.userChatListRecyclerView);
        mRecyclerView.setHasFixedSize(false);                            // Letting the system know that the list objects are of fixed size
        //mRecyclerAdapter = new NavigationRecyclerAdapter(getActivity());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,navigation_header view name, navigation_header view email,
        // and navigation_header view profile picture
        mLayoutManager = new LinearLayoutManager(this);         // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(this, mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == Constants.TYPE_HEADER) {
                    return;
                }
                // only for testing purposes
                if (position > 2) {
                    return;
                }
                final int childViewPosition = position - 1; // minus position of header

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

        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
    }

    private void hideViews() {
        getToolbar().animate().translationY(-getToolbar().getHeight()).setInterpolator(new AccelerateInterpolator(2));

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        getToolbar().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    /** Refreshes the content  */
    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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
                    startActivity(new Intent(UserChatList_act.this, NewUserChat_act.class),
                            ActivityOptions.makeSceneTransitionAnimation(UserChatList_act.this).toBundle());
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
        isRunning = true;
        this.getActiveUserChats();
        //refreshUserList();
        //refreshUserList();
        //loadUserList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
//        final ProgressDialog dia = ProgressDialog.show(this, null,
//                getString(R.string.alert_loading));
        final List<ParseObject> userChatList = new ArrayList<ParseObject>();
        getUserChatListQuery().orderByDescending(ParseConstants.UPDATED_AT).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseUserChatObjects, ParseException e) {
//                dia.dismiss();
                if (e == null) {
                    if (parseUserChatObjects.size() > 0) {
                        userChatList.addAll(parseUserChatObjects);
                        userList = new ArrayList<ParseObject>(userChatList);
                        userChatListRecyclerAdapter.setUserChatList(userList);
                        //userChatListRecyclerAdapter.notifyItemRangeChanged(1, userList.size());
                        userChatListRecyclerAdapter.notifyDataSetChanged();
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
//        final ProgressDialog dia = ProgressDialog.show(this, null,
//                getString(R.string.alert_loading));

        final List<ParseObject> userChatList = new ArrayList<ParseObject>();
        getUserChatListQuery().orderByDescending(ParseConstants.UPDATED_AT).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseUserChatObjects, ParseException e) {
//                dia.dismiss();
                if (e == null) {
                    if (parseUserChatObjects.size() > 0) {
                        //userChatList.addAll(parseUserChatObjects);
                        Log.d(LOG, "Size of list of chat users: " + parseUserChatObjects.size());
                        Log.d(LOG, "Username: " + parseUserChatObjects.get(0).getParseUser("username").getUsername());
                        loadUserList(parseUserChatObjects);
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
     */
    private void loadUserList(List<ParseObject> userChats) {
        // If the size of the two data sets are different it means that x-number of users has requested a new chat hence
        // the whole data set has to be notified, since i do not know how many is actively requesting to start a new chat
        if(userChats.size() != this.userList.size()) {
            userList = new ArrayList<ParseObject>(userChats);
            userChatListRecyclerAdapter.setUserChatList(userList);
            userChatListRecyclerAdapter.notifyDataSetChanged();
        }
        // Check if any of the chats has been updated recently typically by messaging. If any updates has been made
        // notify the item at the given position of the iterator, and move the item to the top of the chat list
        // Next, update the adapters list of chats, so it can
        else {
            for(int i = 0; i < userChats.size(); i++){
                Date chatUpdatedDate = userChats.get(i).getUpdatedAt(); //The updated date of the chat
//                Log.d("userChatTime: " , d.toString());
//                Log.d("userListTime: ", userList.get(i).getUpdatedAt().toString());
                if(chatUpdatedDate.before(userList.get(i).getUpdatedAt())){ // If the updated date is newer than the previous date, notify the adapter
                    userChatListRecyclerAdapter.notifyItemChanged(i + 1);
                    userChatListRecyclerAdapter.notifyItemMoved(i + 1, 1);
                }
            }
            // Update the adapters list to its current status. If this is not done, the list of the adapter is left unchanged.
            userList = new ArrayList<ParseObject>(userChats);
            userChatListRecyclerAdapter.setUserChatList(userList);
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isRunning) {
                    getActiveUserChats();
                }
            }
        }, 1000);
    }

    /** The query for the list of chats */
    private ParseQuery<ParseObject> getUserChatListQuery(){
        ParseQuery<ParseObject> parseObjectQuery = ParseQuery.getQuery("ChatUsers");
        parseObjectQuery.whereMatches("chatUserId", ParseUser.getCurrentUser().getUsername());
        parseObjectQuery.include("username");
        parseObjectQuery.include("createdBy");

        return parseObjectQuery;
    }

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
