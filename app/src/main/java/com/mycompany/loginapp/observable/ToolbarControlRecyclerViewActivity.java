package com.mycompany.loginapp.observable;

/**
 * Created by Alexander on 22-05-2015.
 */
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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

import com.androidquery.AQuery;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.UserChatListRecyclerAdapter;
import com.mycompany.loginapp.chat.Chat_act;
import com.mycompany.loginapp.chat.NewUserChat_act;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
import com.mycompany.loginapp.eventMessages.MessageUserChat;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ToolbarControlRecyclerViewActivity extends ToolbarControlBaseActivity<ObservableRecyclerView> {

    private static final String TAG = ToolbarControlRecyclerViewActivity.class.getSimpleName();

    private UserChatListAdapter userChatListAdapter;
    private ObservableRecyclerView mObservableRecyclerView;                           // Declaring RecyclerView
    private SwipeRefreshLayout swipeRefreshLayout;
    private static AQuery aQuery;
    private ArrayList<ParseObject> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeWindowTransition();
        aQuery = new AQuery(this);
        aQuery.id(R.id.toolbar_title).text("Chat users");
        // Need to get swipe refresh to work with the scrollview
        //setSwipeRefreshLayout();
        getActiveUserChats();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.observable_toolbar;
    }

    public static ArrayList<String> getDummyData(int num) {
        ArrayList<String> items = new ArrayList<String>();
        for (int i = 1; i <= num; i++) {
            items.add("Item " + i);
        }
        return items;
    }

    @Override
    protected ObservableRecyclerView createScrollable() {
        mObservableRecyclerView = (ObservableRecyclerView) findViewById(R.id.scrollableRecyclerView);
        mObservableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mObservableRecyclerView.setHasFixedSize(false);
        //mObservableRecyclerView.setAdapter(new SimpleRecyclerAdapter(this, getDummyData(100)));
        //mObservableRecyclerView.setAdapter(new UserChatListRecyclerAdapter(this, null));
        setObservableRecyclerViewOnTouchListener();

        return mObservableRecyclerView;
    }

    private void setObservableRecyclerViewOnTouchListener(){
        mObservableRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(this, mObservableRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(position == Constants.TYPE_HEADER){
                    return;
                }

                final int childViewPosition = position - 1; // minus position of header

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    EventBus.getDefault().postSticky(new MessageUserChat(userList.get(childViewPosition)));
                    startActivity(new Intent(ToolbarControlRecyclerViewActivity.this, Chat_act.class).
                                    putExtra(Constants.EXTRA_DATA, userList.get(childViewPosition).getParseUser("username").getUsername()),
                            ActivityOptions.makeSceneTransitionAnimation(ToolbarControlRecyclerViewActivity.this).toBundle());
                } else {
                    EventBus.getDefault().postSticky(new MessageUserChat(userList.get(childViewPosition)));
                    startActivity(new Intent(ToolbarControlRecyclerViewActivity.this, Chat_act.class));
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void setSwipeRefreshLayout(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    /** Refreshes the content  */
    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
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
                    startActivity(new Intent(ToolbarControlRecyclerViewActivity.this, NewUserChat_act.class),
                            ActivityOptions.makeSceneTransitionAnimation(ToolbarControlRecyclerViewActivity.this).toBundle());
                }
                else {
                    startActivity(new Intent(ToolbarControlRecyclerViewActivity.this, NewUserChat_act.class));
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
        //updateUserStatus(false);
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
                        Utilities.showDialog(ToolbarControlRecyclerViewActivity.this, "No users for chat were found. Try reloading the page.");
                    }
                } else {
                    Utilities.showDialog(ToolbarControlRecyclerViewActivity.this, "Error: " + e.getMessage());
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
        userChatListAdapter = new UserChatListAdapter(this, userList);
        mObservableRecyclerView.setAdapter(userChatListAdapter);
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