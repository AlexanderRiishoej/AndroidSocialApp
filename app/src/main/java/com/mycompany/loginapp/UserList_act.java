package com.mycompany.loginapp;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class UserList_act extends BaseActivity {

    public static final String LOG = UserList_act.class.getSimpleName();
    /**
     * The user.
     */
    public static ParseUser user;
    private UserAdapter userAdapter;
    /**
     * The Chat list.
     */
    private ArrayList<ParseUser> userList;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ParseRelation<ParseUser> friendsRelation;
    private static AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUserStatus(true);
        makeWindowTransition();
        aQuery = new AQuery(this);
        userAdapter = new UserAdapter();
        listView = aQuery.id(R.id.list).getListView();

        friendsRelation = user.getRelation(ParseConstants.FRIENDS);
        loadUserList();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        //addUser();
    }

    private void addUser(){
        ParseUser.getQuery().whereNotEqualTo(ParseConstants.USERNAME, user.getUsername()).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null) {
                    if(parseUsers.size() != 0) {
                        for(ParseUser user : parseUsers){
                            friendsRelation.add(user);
                        }
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    Log.d(LOG, "Saving succeeded");
                                }
                                else {
                                    Log.d(LOG, "Saving failed");
                                }
                            }
                        });
                    }
                }
                else{
                    Utilities.showDialog(
                            UserList_act.this,
                            getString(R.string.err_users) + " "
                                    + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void removeUser(ParseUser p){
        friendsRelation.remove(p);
        user.saveInBackground();
    }
    // fake a network operation's delayed response
    // this is just for demonstration, not real code!
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
        return R.layout.user_list;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
        refreshUserList();
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
        this.finishAfterTransition();
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

    private void refreshUserList(){
        friendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseFriends, ParseException e) {
                if (parseFriends != null) {

                    if (parseFriends.size() == 0) {
                        Toast.makeText(UserList_act.this,
                                R.string.msg_no_user_found,
                                Toast.LENGTH_SHORT).show();
                    }

                    userList = new ArrayList<ParseUser>(parseFriends);
                    userAdapter.notifyDataSetChanged();
                } else {
                    Utilities.showDialog(
                            UserList_act.this,
                            getString(R.string.err_users) + " "
                                    + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Load list of users from Parse
     */
    private void loadUserList() {
        final ProgressDialog dia = ProgressDialog.show(this, null,
                getString(R.string.alert_loading));
        friendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseFriends, ParseException e) {
                dia.dismiss();
                if (parseFriends != null) {

                    if (parseFriends.size() == 0) {
                        Toast.makeText(UserList_act.this,
                                R.string.msg_no_user_found,
                                Toast.LENGTH_SHORT).show();
                    }

                    userList = new ArrayList<ParseUser>(parseFriends);
                    //listView = (ListView) findViewById(R.id.list);
                    listView.setAdapter(userAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0,
                                                View arg1, int pos, long arg3) {
                            startActivity(new Intent(UserList_act.this, Chat_act.class).
                                            putExtra(Constants.EXTRA_DATA, userList.get(pos).getUsername()),
                                    ActivityOptions.makeSceneTransitionAnimation(UserList_act.this).toBundle());
                            //removeUser(userList.get(pos));
                            Log.d("VIEW: ", arg1.toString());
                            Log.d("POSITION: ", "" + pos);
                            Log.d("LONG: ", "" + arg3);
                        }
                    });
                } else {
                    Utilities.showDialog(
                            UserList_act.this,
                            getString(R.string.err_users) + " "
                                    + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

//        ParseUser.getQuery().whereNotEqualTo(ParseConstants.USERNAME, user.getUsername()).setLimit(1000).findInBackground(new FindCallback<ParseUser>() {
//
//            @Override
//            public void done(List<ParseUser> li, ParseException e) {
//                dia.dismiss();
//                if (li != null) {
//
//                    if (li.size() == 0) {
//                        Toast.makeText(UserList.this,
//                                R.string.msg_no_user_found,
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    userList = new ArrayList<ParseUser>(li);
//                    ListView list = (ListView) findViewById(R.id.list);
//                    list.setAdapter(userAdapter);
//                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                        @Override
//                        public void onItemClick(AdapterView<?> arg0,
//                                                View arg1, int pos, long arg3) {
//                            startActivity(new Intent(UserList.this, Chat.class).
//                                            putExtra(Constants.EXTRA_DATA, userList.get(pos).getUsername()),
//                                    ActivityOptions.makeSceneTransitionAnimation(UserList.this).toBundle());
//                            Log.d("VIEW: ", arg1.toString());
//                            Log.d("POSITION: ", "" + pos);
//                            Log.d("LONG: ", "" + arg3);
//                            //Utilities.showDialog(UserList.this, "Hey bro");
//                        }
//                    });
//                } else {
//                    Utilities.showDialog(
//                            UserList.this,
//                            getString(R.string.err_users) + " "
//                                    + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    /**
     * The Class UserAdapter is the adapter class for User ListView. This
     * adapter shows the user name and it's only online status for each item.
     * Propagates items in the ListView and inflates the layout
     */
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
        public ParseUser getItem(int arg0) {
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
            ParseUser parseUser = getItem(position);

            if (convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.chat_item, null);
                viewHolder = new ViewHolder();
                viewHolder.username = (TextView)convertView.findViewById(R.id.user_list_username);
                viewHolder.profilePicture = (CircularImageView)convertView.findViewById(R.id.user_list_username_profile_parse_image);
                viewHolder.imageProgressBar = (ProgressBar)convertView.findViewById(R.id.user_list_image_progress_bar);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
                Log.d("Recycling...", "ViewHolder recycling user list");
            }

             viewHolder.imageProgressBar.setVisibility(View.VISIBLE);
            Log.d(LOG, "Username: " + parseUser.getUsername());

            viewHolder.username.setText(parseUser.getUsername());

            viewHolder.username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chat_list_bitmap, 0);

//            viewHolder.username.setCompoundDrawablesWithIntrinsicBounds(parseUser.getBoolean(ParseConstants.ONLINE) ? R.drawable.ic_online
//                    : R.drawable.ic_offline, 0, R.drawable.arrow, 0);
            Log.d(LOG, "Parsefile Uri: " + parseUser.getParseFile(ParseConstants.PROFILE_PICTURE).getUrl());

            /** try - catch in order to prevent a crash in case of CircularImageView throwing a null pointer exception during bitmap loading */
            try {
                Picasso.with(UserList_act.this).load(parseUser.getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).fit().
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
            }
            catch (Exception e){
                e.printStackTrace();
            }


            return convertView;
        }

    }

    // ViewHolder to hold the id's of the views
    public static class ViewHolder {
        public TextView username;
        /** Special circular imageView using a framework */
        public CircularImageView profilePicture;
        /** ProgressBar for the small profile pictures in the list loading */
        public ProgressBar imageProgressBar;
    }

    private void makeWindowTransition() {
        getWindow().setEnterTransition(makeEnterTransition());
        getWindow().setExitTransition(makeExitTransition());
        getWindow().setReenterTransition(makeReenterTransition());
        getWindow().setReturnTransition(makeReturnTransition());
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
    }

    private Transition makeEnterTransition() {
        TransitionSet enterTransition = new TransitionSet();

        Transition t = new Slide(Gravity.TOP).setDuration(600);
        enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.excludeTarget(R.id.toolbar_teal, true);
        enterTransition.addTransition(t);

        Transition tt = new Fade();
        enterTransition.addTransition(tt).setDuration(1000);
        return enterTransition;
    }

    private Transition makeExitTransition() {
        TransitionSet exitTransition = new TransitionSet();

        exitTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        exitTransition.excludeTarget(android.R.id.statusBarBackground, true);
        exitTransition.excludeTarget(R.id.toolbar_teal, true);
        Transition fade = new Fade();
        exitTransition.addTransition(fade);

        exitTransition.setDuration(500);
        return exitTransition;
    }

    private Transition makeReenterTransition() {
        TransitionSet enterTransition = new TransitionSet();
        enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.excludeTarget(R.id.toolbar_teal, true);

        Transition autoTransition = new AutoTransition().setDuration(400);
        Transition fade = new Fade().setDuration(400);
        enterTransition.addTransition(fade);
        enterTransition.addTransition(autoTransition);
        return enterTransition;
    }

    private Transition makeReturnTransition() {
        TransitionSet enterTransition = new TransitionSet();

        Transition upperPartSlide = new Slide(Gravity.LEFT);
        enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.excludeTarget(R.id.toolbar_teal, true);
        enterTransition.addTransition(upperPartSlide);

        Transition fade = new Fade();
        enterTransition.addTransition(fade);

        enterTransition.setDuration(500);
        return enterTransition;
    }
}
