package com.mycompany.loginapp.chat;

import android.app.ActivityOptions;
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
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.ChatRecyclerAdapter;
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
    private List<Conversation> conversationList;
    private EditText txt;
    private String receiver;
    private Date lastMsgDate;
    private boolean isRunning;
    private ParseObject userChatObject;
    private static Handler handler;
    private static AQuery aQuery;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private ImageView mToolbarImageView;
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeWindowTransition();
        EventBus.getDefault().register(this);
        aQuery = new AQuery(this);
        txt = (EditText) findViewById(R.id.txt);
        //receiver = getIntent().getStringExtra(Constants.EXTRA_DATA);
        receiver = getChatReceiver();
        conversationList = new ArrayList<Conversation>();
        initializeRecyclerView();
        chatRecyclerAdapter = new ChatRecyclerAdapter(this, conversationList, userChatObject);
        mRecyclerView.setAdapter(chatRecyclerAdapter);
        aQuery.id(R.id.toolbar_title).text(receiver);
        handler = new Handler();
        this.loadConversationList();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        mToolbarImageView = (ImageView) findViewById(R.id.chat_image);
        MySingleton.getMySingleton().getPicasso().load(userChatObject.getParseUser(ParseConstants.USERNAME).
                getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).centerCrop().fit().noPlaceholder().into(mToolbarImageView);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.chat;
    }

    private String getChatReceiver(){
        //EventBus sticky event getting from UserList_act when pressing a user from chatlist
        userChatObject = EventBus.getDefault().getStickyEvent(MessageUserChat.class).userChatObject;
        String[] splitChatUserArray = userChatObject.getString("chatUserId").split(ParseUser.getCurrentUser().getUsername());
        String chatParticipantHolder = "";
        for (String match : splitChatUserArray) {
            if (!match.equals("")) {
                chatParticipantHolder = match;
            }
        }
        return chatParticipantHolder;
    }

    private void initializeRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_list);
        mRecyclerView.setHasFixedSize(false);                            // Letting the system know that the list objects are of fixed size
        //mRecyclerAdapter = new NavigationRecyclerAdapter(getActivity());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,navigation_header view name, navigation_header view email,
        // and navigation_header view profile picture
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.smoothScrollToPosition(conversationList.size());
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

        mRecyclerView.addOnScrollListener(new ReverseHidingScrollListener() {
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
    }

    private void showViews() {
        getToolbar().animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    public void sendMessage(View view) {
        if (view.getId() == R.id.btnSend) {
            sendMessage();
        }
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        aQuery.id(R.id.progress).visibility(View.VISIBLE);
        //loadConversationList();
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

        InputMethodManager imm = (InputMethodManager) getSystemService(Chat_act.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);
        // scroll to the bottom of the recyclerview
        mRecyclerView.scrollToPosition(conversationList.size());

        String message = txt.getText().toString();
        final Conversation conversation = new Conversation(message, Calendar.getInstance().getTime(), ParseUser.getCurrentUser().getUsername());
        conversation.setStatus(Conversation.STATUS_SENDING);
        conversationList.add(conversation);
        //chatAdapter.notifyDataSetChanged();
        chatRecyclerAdapter.setChatList(conversationList);
        chatRecyclerAdapter.notifyDataSetChanged();
        Log.d("Notify: ", "NotifyDataSetChanged called");
        txt.setText(null);

        final ParseObject chatObject = new ParseObject(ParseConstants.CHAT);
        //ParseObject lastMsgRcvObject =
        chatObject.put(ParseConstants.CHAT_SENDER, ParseUser.getCurrentUser().getUsername());
        chatObject.put(ParseConstants.CHAT_RECEIVER, receiver);
        // po.put("createdAt", "");
        chatObject.put(ParseConstants.MESSAGE, message);
        chatObject.put(ParseConstants.SEEN, false);

        chatObject.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    conversation.setStatus(Conversation.STATUS_SENT);
                } else {
                    conversation.setStatus(Conversation.STATUS_FAILED);
                }
                userChatObject.getRelation(ParseConstants.CHAT_RELATION).add(chatObject);
                userChatObject.saveInBackground();
                //chatAdapter.notifyDataSetChanged();
                chatRecyclerAdapter.setChatList(conversationList);
                chatRecyclerAdapter.notifyDataSetChanged();
                Log.d("Notify: ", "NotifyDataSetChanged called");
            }
        });
    }

    /** Sets the field "seen" in the chat to true. This is set to true, whenever a user starts this activity, or leaves it. */
    private void setSeenTrue(){
        ParseQuery<ParseObject> chatQuery = userChatObject.getRelation(ParseConstants.CHAT_RELATION).getQuery();
        chatQuery.orderByDescending(ParseConstants.CREATED_AT).getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject chatObject, ParseException e) {
                if (e == null) {
                    chatObject.put(ParseConstants.SEEN, true);
                    chatObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                ParseRelation<ParseObject> chatRelation = userChatObject.getRelation(ParseConstants.CHAT_RELATION);
                                chatRelation.add(chatObject);
                                userChatObject.saveInBackground();
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
        ParseQuery<ParseObject> chatQuery = userChatObject.getRelation(ParseConstants.CHAT_RELATION).getQuery();
        //in the start, the list will always be 0
        if (conversationList.size() == 0) {
            // load all messages...
            ArrayList<String> chatQueryList = new ArrayList<String>();
            chatQueryList.add(receiver);
            chatQueryList.add(ParseUser.getCurrentUser().getUsername());
            chatQuery.whereContainedIn(ParseConstants.CHAT_SENDER, chatQueryList);
            chatQuery.whereContainedIn(ParseConstants.CHAT_RECEIVER, chatQueryList);
        } else {
            // load only newly received message..
            if (lastMsgDate != null)
                chatQuery.whereGreaterThan(ParseConstants.CREATED_AT, lastMsgDate);
            // get the messages the receiver sends to parse
            chatQuery.whereEqualTo(ParseConstants.CHAT_SENDER, receiver);
            chatQuery.whereEqualTo(ParseConstants.CHAT_RECEIVER, ParseUser.getCurrentUser().getUsername());
        }
        chatQuery.orderByDescending(ParseConstants.CREATED_AT);
        chatQuery.setLimit(30);
        chatQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> chatMessages, ParseException e) {
                //chatMessages size will be 0 after first run if no new messages has been put in the chat
                if (e == null && chatMessages.size() > 0) {
                    // lastMsgDate will be null on the first run of this method
                    if(lastMsgDate != null){
                        if(chatMessages.get(0).getCreatedAt().after(lastMsgDate)){
                            // If the newly received chat message date is after the previous lastMsgDate, then it means that a new message has been added to the
                            // chat, and this will be the new
                            lastMsgDate = chatMessages.get(0).getCreatedAt();
                            // if current user is equal to the receiver of this chat message and the "seen" field is false, then set the field "seen" to true
                            if(ParseUser.getCurrentUser().getUsername().equals(chatMessages.get(0).get(ParseConstants.CHAT_RECEIVER)) &&
                                    !chatMessages.get(0).getBoolean(ParseConstants.SEEN)){
                                setSeenTrue();
                            }
                            // create all the conversation objects from the chatMessages
                            for (int i = chatMessages.size() - 1; i >= 0; i--) {
                                ParseObject po = chatMessages.get(i);
                                Conversation conversation = new Conversation(po.getString(ParseConstants.MESSAGE), po.getCreatedAt(), po.getString(ParseConstants.CHAT_SENDER));
                                conversationList.add(conversation);
                            }
                            //aQuery.id(R.id.progress).visibility(View.GONE);
                            chatRecyclerAdapter.setChatList(conversationList);
                            chatRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    else {
                        lastMsgDate = chatMessages.get(0).getCreatedAt();
                        if(ParseUser.getCurrentUser().getUsername().equals(chatMessages.get(0).get(ParseConstants.CHAT_RECEIVER)) &&
                                !chatMessages.get(0).getBoolean(ParseConstants.SEEN)){
                            setSeenTrue();
                        }

                        // create all the conversation objects from the chatMessages
                        for (int i = chatMessages.size() - 1; i >= 0; i--) {
                            ParseObject po = chatMessages.get(i);
                            Conversation conversation = new Conversation(po.getString(ParseConstants.MESSAGE), po.getCreatedAt(), po.getString(ParseConstants.CHAT_SENDER));
                            conversationList.add(conversation);
                        }
                        aQuery.id(R.id.progress).visibility(View.GONE);
                        chatRecyclerAdapter.setChatList(conversationList);
                        chatRecyclerAdapter.notifyDataSetChanged();
                    }
                }
//
//                aQuery.id(R.id.progress).visibility(View.GONE);
//                chatRecyclerAdapter.setChatList(conversationList);
//                chatRecyclerAdapter.notifyDataSetChanged();
//                //progressBar.setVisibility(View.GONE);
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

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------------------
     * Window Transitions
     */

    private void makeWindowTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(makeEnterTransition());
            getWindow().setReturnTransition(makeReturnTransition());
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        }
    }

    private Transition makeEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet enterTransition = new TransitionSet();

            Transition groupTransition = new Slide(Gravity.RIGHT);


            RelativeLayout chatLayoutMain = (RelativeLayout) findViewById(R.id.chat_layout_main);
//            FrameLayout chatLayoutMain = (FrameLayout) findViewById(R.id.chat_layout_main);
//            LinearLayout chatLayoutMain = (LinearLayout) findViewById(R.id.chat_layout_main);

            chatLayoutMain.setTransitionGroup(true);
            groupTransition.addTarget(chatLayoutMain);

            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            enterTransition.excludeTarget(R.id.toolbar_teal, true);
            //enterTransition.addTransition(fadeIn);
            enterTransition.addTransition(groupTransition).setDuration(300);

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

            Transition autoTransition = new AutoTransition().setDuration(700);
            Transition fade = new Fade().setDuration(800);
            enterTransition.addTransition(fade);
            enterTransition.addTransition(autoTransition);
            return enterTransition;
        } else return null;
    }

    private Transition makeReturnTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet enterTransition = new TransitionSet();

            Transition upperPartSlide = new Slide(Gravity.RIGHT);
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            enterTransition.excludeTarget(R.id.toolbar_teal, true);
            enterTransition.addTransition(upperPartSlide);

            Transition fade = new Fade();
            //enterTransition.addTransition(fade);

            enterTransition.setDuration(300);
            return enterTransition;
        }
        return null;
    }
}