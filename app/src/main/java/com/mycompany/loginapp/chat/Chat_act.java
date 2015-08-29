package com.mycompany.loginapp.chat;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.ChatRecyclerAdapter;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
import com.mycompany.loginapp.eventMessages.MessageUserChat;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * The Class Chat is the Activity class that holds main chat screen. It shows
 * all the conversation messages between two users and also allows the receiver to
 * send and receive messages.
 */
//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Chat_act extends BaseActivity {

    public static final String LOG = Chat_act.class.getSimpleName();
    private List<Conversation> mConversationList;
    private EditText txt;
    private ParseUser receiver;
    private Date mostRecentMsgDate;
    private Date oldestMsgDate;
    private boolean isRunning;
    private ParseObject mUserChatObject;
    private Handler handler;
    private static AQuery aQuery;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ChatRecyclerAdapter mChatRecyclerAdapter;
    private ImageView mToolbarImageView;
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        aQuery = new AQuery(this);
        txt = (EditText) findViewById(R.id.txt);
        //receiver = getIntent().getStringExtra(Constants.EXTRA_DATA);
        mUserChatObject = EventBus.getDefault().getStickyEvent(MessageUserChat.class).mUserChatObject;
        receiver = this.getChatReceiver();
        mConversationList = new ArrayList<Conversation>();
        this.initializeRecyclerView();
        mChatRecyclerAdapter = new ChatRecyclerAdapter(this, mConversationList, mUserChatObject);
        mRecyclerView.setAdapter(mChatRecyclerAdapter);
        aQuery.id(R.id.toolbar_title).text(receiver.getUsername());
        handler = new Handler();

        mToolbarImageView = (ImageView) findViewById(R.id.chat_image);

        this.setToolbarPhoto();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    /** Sets the Toolbar picture to the receiver */
    private void setToolbarPhoto() {
        if(mUserChatObject.getParseUser(ParseConstants.USERNAME).getUsername().equals(receiver.getUsername())) {
            if(mUserChatObject.getParseUser(ParseConstants.USERNAME).getParseFile(ParseConstants.PROFILE_PICTURE) != null) {
                MySingleton.getMySingleton().getPicasso().load(mUserChatObject.getParseUser(ParseConstants.USERNAME).
                        getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).centerCrop().fit().noPlaceholder().into(mToolbarImageView);
            }

            else {
                MySingleton.getMySingleton().getPicasso().load(R.drawable.com_facebook_profile_picture_blank_portrait).centerCrop().fit().into(mToolbarImageView);
                final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
                mToolbarImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
        else {
            if(mUserChatObject.getParseUser(ParseConstants.CREATED_BY).getParseFile(ParseConstants.PROFILE_PICTURE) != null) {
                MySingleton.getMySingleton().getPicasso().load(mUserChatObject.getParseUser(ParseConstants.CREATED_BY).
                        getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).centerCrop().fit().noPlaceholder().into(mToolbarImageView);
            }
            else {
                MySingleton.getMySingleton().getPicasso().load(R.drawable.com_facebook_profile_picture_blank_portrait).centerCrop().fit().into(mToolbarImageView);
                final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
                mToolbarImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.chat;
    }

    private ParseUser getChatReceiver() {
        final ParseUser createdByUserObject = mUserChatObject.getParseUser(ParseConstants.CREATED_BY);
        final ParseUser usernameUserObject = mUserChatObject.getParseUser(ParseConstants.USERNAME);

        final ParseUser chatParticipant;
        if(ApplicationMain.mCurrentParseUser.getUsername().equals(createdByUserObject.getUsername())){
            chatParticipant = usernameUserObject;
        }
        else {
            chatParticipant = createdByUserObject;
        }
        return chatParticipant;
    }

    private void initializeRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_list);
        mRecyclerView.setHasFixedSize(false);                            // Letting the system know that the list objects are of fixed size
        //mRecyclerAdapter = new NavigationRecyclerAdapter(getActivity());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,navigation_header view name, navigation_header view email,
        // and navigation_header view profile_image picture
        mLinearLayoutManager = new LinearLayoutManager(this);
        //reverses the data-layout direction of the adapter
        mLinearLayoutManager.setStackFromEnd(true);
        //mLinearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.smoothScrollToPosition(mConversationList.size());
        //recyclerViewAddOnItemClickListener();
        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(this, mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final int childViewPosition = position - 1; // minus position of header

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                } else {

                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //the scrollListener handling the logic for loading older messages when scrolling
        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        //add progress item
                        Conversation progressBarConversation = new Conversation("", null, null, null);
                        progressBarConversation.setProgress(true);
                        //mConversationList.add(0, progressBarConversation);
                        //and notify the adapter
                        mChatRecyclerAdapter.addProgressItem(progressBarConversation, 0);
                        //scroll to the position of progress item
                        mRecyclerView.smoothScrollToPosition(0);

                        //get the messages
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getOldChatMessages();
                            }
                        }, 1000);
                    }
                });
            }
        };
        //add scrollListener to the recyclerView
        mRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);
    }

    //Gets all the old chat messages that is older than the oldestMsgDate variable
    private void getOldChatMessages() {
        ParseQuery<ParseObject> chatQuery = mUserChatObject.getRelation(ParseConstants.CHAT_RELATION).getQuery();

        if (oldestMsgDate == null) {
            return;
        }

        chatQuery.whereLessThan(ParseConstants.CREATED_AT, oldestMsgDate);
        chatQuery.orderByDescending(ParseConstants.CREATED_AT);

        final int chatLimit = 20;
        chatQuery.setLimit(chatLimit);
        chatQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> chatMessages, ParseException e) {
                //remove progress item from the current mConversationList
                //mConversationList.remove(0);
                //notify adapter of the removed item
                mChatRecyclerAdapter.removeItemAtPosition(0);

                if (e == null) {
                    if (chatMessages.size() > 0) {
                        // if the oldest date of the chat items is older than oldestMsgDate then load the items in the lists else notify user that no more messages are left to load
                        if (chatMessages.get(0).getCreatedAt().before(oldestMsgDate)) {
                            oldestMsgDate = chatMessages.get(chatMessages.size() - 1).getCreatedAt();

                            //populate the mConversationList
//                            for (int i = 0; i < chatMessages.size(); i++) {
//                                ParseObject po = chatMessages.get(i);
//                                Conversation conversation = new Conversation(po.getString(ParseConstants.MESSAGE), po.getCreatedAt(), po.getString(ParseConstants.CHAT_SENDER), po.getObjectId());
//                                mConversationList.add(0, conversation);
//                            }

                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mChatRecyclerAdapter.addRangeList(addOldConversationObjects(chatMessages));
                                    //scroll to the last position of the newly added chat items
                                    final int posToScroll = chatMessages.size() - 1;
                                    mRecyclerView.smoothScrollToPosition(posToScroll);
                                }
                            });

                            mEndlessRecyclerOnScrollListener.setLoading(false);
                        }
                    } else {
                        new MaterialDialog.Builder(Chat_act.this)
                                .title("Chat")
                                .content("There are no more chat messages...")
                                .show();
                    }
                }
            }
        });
    }

    public void sendMessage(View view) {
        if (view.getId() == R.id.btnSend) {
            this.sendMessage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        aQuery.id(R.id.main_progressBar).visibility(View.VISIBLE);
        isRunning = true;
        this.loadConversationList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Finishes this activity
     *
     * @param event - received when user presses Log Out
     */
    public void onEvent(MessageFinishActivities event) {
        //Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
        Log.d("CLOSE EVENT RECEIVED: ", "FINISHING CHAT");
        this.finish();
    }

    /**
     * Call this method to Send message to opponent. It does nothing if the text
     * is empty otherwise it creates a Parse object for Chat message and send it
     * to Parse server.
     */
    private void sendMessage() {
        if (txt.length() == 0)
            return;

//        InputMethodManager imm = (InputMethodManager) getSystemService(Chat_act.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);
        final String message = txt.getText().toString();
        final Conversation conversation = new Conversation(message, Calendar.getInstance().getTime(), ApplicationMain.mCurrentParseUser, receiver);
        conversation.setStatus(Conversation.STATUS_SENDING);
        //mConversationList.add(conversation);
        mChatRecyclerAdapter.addItem(conversation);
        // scroll to the bottom of the recyclerView
        mRecyclerView.scrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
        txt.setText(null);

        //create the chat parseObject
        final ParseObject chatObject = new ParseObject(ParseConstants.CHAT);
        chatObject.put(ParseConstants.CHAT_SENDER, ApplicationMain.mCurrentParseUser);
        chatObject.put(ParseConstants.CHAT_RECEIVER, receiver);
        chatObject.put(ParseConstants.MESSAGE, message);
        chatObject.put(ParseConstants.SEEN, false);

        chatObject.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    conversation.setStatus(Conversation.STATUS_SENT);
                    mChatRecyclerAdapter.notifyItemChanged(mChatRecyclerAdapter.getItemCount() - 1);
                } else {
                    conversation.setStatus(Conversation.STATUS_FAILED);
                    mChatRecyclerAdapter.notifyItemChanged(mChatRecyclerAdapter.getItemCount() - 1);
                }
                mUserChatObject.getRelation(ParseConstants.CHAT_RELATION).add(chatObject);
                mUserChatObject.saveInBackground();
            }
        });
    }

    /**
     * Sets the field "seen" in the chat to true. This is set to true, whenever a user starts this activity, or leaves it.
     */
    private void setSeenTrue() {
            ParseQuery<ParseObject> chatQuery = mUserChatObject.getRelation(ParseConstants.CHAT_RELATION).getQuery();
            chatQuery.orderByDescending(ParseConstants.CREATED_AT).getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject chatObject, ParseException e) {
                    if (e == null) {
                        chatObject.put(ParseConstants.SEEN, true);
                        chatObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseRelation<ParseObject> chatRelation = mUserChatObject.getRelation(ParseConstants.CHAT_RELATION);
                                    chatRelation.add(chatObject);
                                    mUserChatObject.saveInBackground();
                                    //notify that the last message in chat should update its image to the seen one
                                    mChatRecyclerAdapter.notifyItemChanged(mChatRecyclerAdapter.getItemCount() - 1);
                                }
                            }
                        });
                    }
                }
            });
    }

    /**
     * Load the conversation list from Parse server and save the date of last
     * message that will be used to load only recent new messages
     */
    private void loadConversationList() {
        ParseQuery<ParseObject> chatQuery = mUserChatObject.getRelation(ParseConstants.CHAT_RELATION).getQuery();


        //in the start, the list will always be zero, hence load all messages from sender and receiver
        if (mConversationList.size() == 0) {
            //the main progressbar when loading first time
            //aQuery.id(R.id.main_progressBar).visibility(View.VISIBLE);
            // load all messages...
            ArrayList<ParseUser> chatQueryList = new ArrayList<ParseUser>();
            chatQueryList.add(receiver);
            chatQueryList.add(ApplicationMain.mCurrentParseUser);
            chatQuery.whereContainedIn(ParseConstants.CHAT_SENDER, chatQueryList);
            chatQuery.whereContainedIn(ParseConstants.CHAT_RECEIVER, chatQueryList);
            chatQuery.include(ParseConstants.CHAT_SENDER);
            chatQuery.include(ParseConstants.CHAT_RECEIVER);
        } else {
            // load only newly received message..
            if (mostRecentMsgDate != null) {
                chatQuery.whereGreaterThan(ParseConstants.CREATED_AT, mostRecentMsgDate);
            }
            // get the messages the receiver sends to parse
            chatQuery.whereEqualTo(ParseConstants.CHAT_SENDER, receiver);
            chatQuery.whereEqualTo(ParseConstants.CHAT_RECEIVER, ApplicationMain.mCurrentParseUser.getUsername());
            chatQuery.include(ParseConstants.CHAT_RECEIVER);
        }
        chatQuery.orderByDescending(ParseConstants.CREATED_AT);

        final int chatLimit = 20;
        chatQuery.setLimit(chatLimit);
        chatQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> chatMessages, ParseException e) {
                //chatMessages size will be 0 after first run if no new messages has been put in the chat
                if (e == null && chatMessages.size() > 0) {
                    final ParseObject recentChatItemMessage = chatMessages.get(0);

                    // mostRecentMsgDate will be null on the first run of this method = first load of chat messages
                    if (mostRecentMsgDate != null) {
                        // if the message date of the most recent message in the chatMessages is newer than the one recorded before
                        if (recentChatItemMessage.getCreatedAt().after(mostRecentMsgDate)) {
                            // If the newly received chat message date is after the previous mostRecentMsgDate, then it means that a new message has been added to the
                            // chat, and this will be the new updated date
                            mostRecentMsgDate = recentChatItemMessage.getCreatedAt();
                            // if current user is equal to the receiver of this chat message and the "seen" field is false, then set the field "seen" to true
                            if (ApplicationMain.mCurrentParseUser.getUsername().equals(recentChatItemMessage.getParseUser(ParseConstants.CHAT_RECEIVER).getUsername()) &&
                                    !recentChatItemMessage.getBoolean(ParseConstants.SEEN)) {
                                setSeenTrue();
                            }
                            // create all the conversation objects from the chatMessages
                            //addAllConversationObjects(chatMessages);
                            // update the adapters list too with the new messages
                            mChatRecyclerAdapter.addNewMessages(addNewConversationObjects(chatMessages));
                            //scroll to the position of the newly added message
                            mRecyclerView.scrollToPosition(mConversationList.size() - 1);
                            //mChatRecyclerAdapter.setChatList(mConversationList);
                        }
                    } else {
                        mostRecentMsgDate = recentChatItemMessage.getCreatedAt();
                        oldestMsgDate = chatMessages.get(chatMessages.size() - 1).getCreatedAt();

                        if (ApplicationMain.mCurrentParseUser.getUsername().equals(recentChatItemMessage.getParseUser(ParseConstants.CHAT_RECEIVER).getUsername()) &&
                                !recentChatItemMessage.getBoolean(ParseConstants.SEEN)) {
                            setSeenTrue();
                        }
                        // create all the conversation objects from the chatMessages in an opposite order
                        //addAllConversationObjects(chatMessages);
                        mChatRecyclerAdapter.setChatList(addOldConversationObjects(chatMessages));

                    }
                }
                aQuery.id(R.id.main_progressBar).visibility(View.GONE);

                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (isRunning) {
                            loadConversationList();
                        }
                    }
                }, 1000);
            }
        });
    }

    //adds newly created chat messages to the conversation list
    private List<Conversation> addNewConversationObjects(List<ParseObject> chatMessages) {
        final List<Conversation> newConversationList = new ArrayList<>();
        for (int i = chatMessages.size(); i < chatMessages.size(); i++) {
            ParseObject po = chatMessages.get(i);
            Conversation conversation = new Conversation(po.getString(ParseConstants.MESSAGE), po.getCreatedAt(), po.getParseUser(ParseConstants.CHAT_SENDER),
                    po.getParseUser(ParseConstants.CHAT_RECEIVER));

            //to check if the last message has been seen
            if(i == 0 && chatMessages.get(i).getBoolean(ParseConstants.SEEN)){
                conversation.isSeen = true;
            }
            newConversationList.add(conversation);
        }

        return newConversationList;
    }

    // create all the conversation objects from the chatMessages and add them to the mConversationList
    // this method is performed first to retrieve the whole chat
    private void addAllConversationObjects(List<ParseObject> chatMessages) {
        for (int i = chatMessages.size() - 1; i >= 0; i--) {
            ParseObject po = chatMessages.get(i);
            Conversation conversation = new Conversation(po.getString(ParseConstants.MESSAGE), po.getCreatedAt(), po.getParseUser(ParseConstants.CHAT_SENDER),
                    po.getParseUser(ParseConstants.CHAT_RECEIVER));
            this.mConversationList.add(conversation);
        }
//        final List<Conversation> allConversationList = new ArrayList<>();
//        for (int i = chatMessages.size() - 1; i >= 0; i--) {
//            ParseObject po = chatMessages.get(i);
//            Conversation conversation = new Conversation(po.getString(ParseConstants.MESSAGE), po.getCreatedAt(), po.getString(ParseConstants.CHAT_SENDER), po.getObjectId());
//            allConversationList.add(conversation);
//        }
//
//        return allConversationList;
    }

    //adds newly created chat messages to the conversation list
    private List<Conversation> addOldConversationObjects(List<ParseObject> chatMessages) {
        final List<Conversation> oldConversationList = new ArrayList<>();

        for (int i = 0; i < chatMessages.size(); i++) {
            ParseObject po = chatMessages.get(i);
            Conversation conversation = new Conversation(po.getString(ParseConstants.MESSAGE), po.getCreatedAt(), po.getParseUser(ParseConstants.CHAT_SENDER),
                    po.getParseUser(ParseConstants.CHAT_RECEIVER));

            //to check if the last message has been seen
            if(i == 0 && chatMessages.get(i).getBoolean(ParseConstants.SEEN)){
                conversation.isSeen = true;
            }
            oldConversationList.add(0, conversation);
        }

        return oldConversationList;
    }
}