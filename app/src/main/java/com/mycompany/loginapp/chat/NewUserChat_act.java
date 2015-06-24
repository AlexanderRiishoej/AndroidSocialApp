package com.mycompany.loginapp.chat;

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

import com.androidquery.AQuery;
import com.google.common.collect.ComparisonChain;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageUserChat;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;


public class NewUserChat_act extends BaseActivity {

    private AQuery aQuery;
    private ParseUser currentUser;
    private UserAdapter userAdapter;
    private ListView listView;
    private List<ParseUser> userList;
    private ParseRelation<ParseUser> friendsRelation;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aQuery = new AQuery(this);

        aQuery.id(R.id.toolbar_title).text("New Chat");
        currentUser = ParseUser.getCurrentUser();

        friendsRelation = currentUser.getRelation(ParseConstants.FRIENDS);
        listView = aQuery.id(R.id.user_list).getListView();
        userAdapter = new UserAdapter();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        getFriends();
        //getAllUsers();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.new_user_chat;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_user_chat, menu);
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

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getFriends();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private void getAllUsers(){
        ParseUser.getQuery().whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername()).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                for (ParseUser p : list) {
                    friendsRelation.add(p);
                }
                ParseUser.getCurrentUser().saveInBackground();
            }
        });
    }

    /** Gets all the friends related to this current user */
    private void getFriends(){
        friendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null){
                    if(parseUsers.size() > 0){
                        userList = new ArrayList<ParseUser>(parseUsers);
                        listView.setAdapter(userAdapter);
                        setActionOnClick();
                    }
                    else {
                        Utilities.showDialog(NewUserChat_act.this, "No friends found");
                    }
                }
                else {
                    Utilities.showDialog(NewUserChat_act.this, "Error loading friends. Try again.");
                    e.printStackTrace();
                }
            }
        });
    }

    /** Sets the ItemClickListener and gets the active user chats OnClick */
    private void setActionOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override //return parseUser clicked. Retrieve
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActiveUserChats(userList.get(position));
            }
        });
    }

    /** Compares two strings alphabetically */
    private static Comparator<String> stringAlphabeticalComparator = new Comparator<String>() {
        public int compare(String str1, String str2) {
            return ComparisonChain.start().
                    compare(str1, str2, String.CASE_INSENSITIVE_ORDER).
                    compare(str1, str2).
                    result();
        }
    };

    /**
     * Creates a unique id with the current user id and the receiver id
     */
    private String addUniqueId(String currentUserId, String receiverUserId) {
        List<String> userIds = new ArrayList<>();
        userIds.add(currentUserId);
        userIds.add(receiverUserId);

        Collections.sort(userIds, stringAlphabeticalComparator);
        StringBuilder comparedId = new StringBuilder();
        for (String s : userIds) {
            comparedId.append(s);
        }

        return comparedId.toString();
    }

    /**
     * Checks whether there already exists a chat with the current user and receiver.
     * If it exists just get the user chat object from parse, and give it to the new chat activity
     * If it does not exist, create a new user chat object and save it to parse, and give it to the new chat activity
     */
    private void getActiveUserChats(ParseUser rcvUser) {
        final ProgressDialog dia = ProgressDialog.show(this, null,
                getString(R.string.alert_loading));
        final ParseUser receiverUser = rcvUser;

        ParseQuery<ParseObject> parseObjectQuery = ParseQuery.getQuery("ChatUsers");
//        parseObjectQuery.whereMatches("chatUserId", addUniqueId(currentUser.getUsername(), receiverUser.getUsername()));
        parseObjectQuery.whereMatches("chatId", addUniqueId(currentUser.getObjectId(), receiverUser.getObjectId()));
        parseObjectQuery.include("username");
        parseObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject chatUserObject, ParseException e) {
                dia.dismiss();
                if (e == null || chatUserObject == null) {
                    if (chatUserObject != null) {
                        Log.d(LOG, "Username: " + chatUserObject.getParseUser("username").getUsername());
//                        chatUserObject.put("chatId", addUniqueId(currentUser.getObjectId(), receiverUser.getObjectId()));
//                        chatUserObject.saveInBackground();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            EventBus.getDefault().postSticky(new MessageUserChat(chatUserObject));
                            startActivity(new Intent(NewUserChat_act.this, Chat_act.class),
                                    ActivityOptions.makeSceneTransitionAnimation(NewUserChat_act.this).toBundle());
                        } else {
                            EventBus.getDefault().postSticky(new MessageUserChat(chatUserObject));
                            startActivity(new Intent(NewUserChat_act.this, Chat_act.class));
                        }

                    } else {
                        final ParseObject newChat = new ParseObject(ParseConstants.CHAT);
                        newChat.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    final ParseObject newChatUserPair = new ParseObject(ParseConstants.CHAT_USERS);
                                    final ParseRelation<ParseObject> chatRelation = newChatUserPair.getRelation(ParseConstants.CHAT_RELATION);
                                    newChatUserPair.put("createdBy", currentUser);
                                    newChatUserPair.put("username", receiverUser);
                                    newChatUserPair.put("chatUserId", addUniqueId(currentUser.getUsername(), receiverUser.getUsername()));
                                    newChatUserPair.put("chatId", addUniqueId(currentUser.getObjectId(), receiverUser.getObjectId()));
                                    //newChatUserPair.put(ParseConstants.CHAT_RELATION, newChat);
                                    chatRelation.add(newChat);
                                    newChatUserPair.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e == null){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    EventBus.getDefault().postSticky(new MessageUserChat(newChatUserPair));
                                                    startActivity(new Intent(NewUserChat_act.this, Chat_act.class),
                                                            ActivityOptions.makeSceneTransitionAnimation(NewUserChat_act.this).toBundle());
                                                } else {
                                                    EventBus.getDefault().postSticky(new MessageUserChat(newChatUserPair));
                                                    startActivity(new Intent(NewUserChat_act.this, Chat_act.class));
                                                }
                                            }
                                            else{
                                                Utilities.showDialog(NewUserChat_act.this, e.getMessage());
                                            }
                                        }
                                    });
//
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                        EventBus.getDefault().postSticky(new MessageUserChat(newChatUserPair));
//                                        startActivity(new Intent(NewUserChat_act.this, Chat_act.class),
//                                                ActivityOptions.makeSceneTransitionAnimation(NewUserChat_act.this).toBundle());
//                                    } else {
//                                        EventBus.getDefault().postSticky(new MessageUserChat(newChatUserPair));
//                                        startActivity(new Intent(NewUserChat_act.this, Chat_act.class));
//                                    }
                                }
                                else {
                                    e.printStackTrace();
                                    Utilities.showDialog(NewUserChat_act.this, "Failed loading chat. Try again.");
                                }
                            }
                        });
                    }
                } else {
                    Utilities.showDialog(NewUserChat_act.this, "Error: " + e.getMessage());
                }
            }
        });
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
            final NewUserChatViewHolder viewHolder;
            final ParseUser user = getItem(position);

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.user_new_chat_list_item, null);
                viewHolder = new NewUserChatViewHolder();
                viewHolder.username = (TextView) convertView.findViewById(R.id.user_list_username);
                viewHolder.profilePicture = (ImageView) convertView.findViewById(R.id.user_list_username_profile_parse_image);
                viewHolder.userImageProgressBar = (ProgressBar) convertView.findViewById(R.id.new_user_list_image_progress_bar);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (NewUserChatViewHolder) convertView.getTag();
                Log.d("Recycling...", "ViewHolder recycling user list");
            }

            viewHolder.userImageProgressBar.setVisibility(View.VISIBLE);
            Log.d(LOG, "Username: " + user.getUsername());

            viewHolder.username.setText(user.getUsername());

            viewHolder.username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chat_list_bitmap, 0);
            Log.d(LOG, "Parsefile Uri: " + user.getParseFile(ParseConstants.PROFILE_PICTURE).getUrl());
            /** try - catch in order to prevent a crash in case of CircularImageView throwing a null pointer exception during bitmap loading */
            try {
                Picasso.with(NewUserChat_act.this).load(user.getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).fit().
                        into(viewHolder.profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {
                                //hide progressbar
                                viewHolder.userImageProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                //hide progressbar
                                viewHolder.userImageProgressBar.setVisibility(View.GONE);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }

    // ViewHolder to hold the id's of the views
    public static class NewUserChatViewHolder {
        public TextView username;
        /**
         * Special circular imageView using a framework
         */
        public ImageView profilePicture;
        /**
         * ProgressBar for the small profile pictures in the list loading
         */
        public ProgressBar userImageProgressBar;

        //public TextView lastChatMessage;
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
            enterTransition.addTransition(fade);
            enterTransition.addTransition(autoTransition);
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
            enterTransition.addTransition(fade);

            enterTransition.setDuration(500);
            return enterTransition;
        } else return null;
    }
}
