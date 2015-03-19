package com.mycompany.loginapp;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Chat_act extends BaseActivity {

    /**
     * The Conversation list.
     */
    private ArrayList<Conversation_act> conversationList;

    /**
     * The chat adapter.
     */
    private ChatAdapter chatAdapter;

    /**
     * The Editext to compose the message.
     */
    private EditText txt;

    /**
     * The receiver name of receiver.
     */
    private String receiver;

    /**
     * The date of last message in conversation.
     */
    private Date lastMsgDate;

    /**
     * Flag to hold if the activity is running or not.
     */
    private boolean isRunning;

    /**
     * The handler.
     */
    private static Handler handler;

    private static AQuery aQuery;

    private SwipeRefreshLayout swipeRefreshLayout;

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeWindowTransition();
        aQuery = new AQuery(this);

        conversationList = new ArrayList<Conversation_act>();
        ListView list = (ListView) findViewById(R.id.list);
        chatAdapter = new ChatAdapter();
        list.setAdapter(chatAdapter);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setStackFromBottom(true);

        txt = (EditText) findViewById(R.id.txt);
        //txt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        receiver = getIntent().getStringExtra(Constants.EXTRA_DATA);
        aQuery.id(R.id.toolbar_title).text(receiver);

        handler = new Handler();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.chat;
    }

    public void sendMessage(View view) {
        if (view.getId() == R.id.btnSend) {
            sendMessage();
        }
    }

    private void refreshContent(){
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
        loadConversationList();
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Finishes this activity
     * @param event - received when user presses Log Out
     */
    public void onEvent(MessageFinishActivities event){
        //Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
        Log.d("CLOSE EVENT RECEIVED: ", "FINISHING CHAT");
        this.finishAfterTransition();
    }

    /**
     * Call this method to Send message to opponent. It does nothing if the text
     * is empty otherwise it creates a Parse object for Chat message and send it
     * to Parse server.
     */
    private void sendMessage() {
        if (txt.length() == 0)
            return;

//        InputMethodManager imm = (InputMethodManager) getSystemService(Chat.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);

        String message = txt.getText().toString();
        final Conversation_act c = new Conversation_act(message, Calendar.getInstance().getTime(), UserList_act.user.getUsername());
        c.setStatus(Conversation_act.STATUS_SENDING);
        conversationList.add(c);
        chatAdapter.notifyDataSetChanged();
        Log.d("Notify: ", "NotifyDataSetChanged called");
        txt.setText(null);

        ParseObject po = new ParseObject(ParseConstants.CHAT);
        po.put(ParseConstants.CHAT_SENDER, UserList_act.user.getUsername());
        po.put(ParseConstants.CHAT_RECEIVER, receiver);
        // po.put("createdAt", "");
        po.put(ParseConstants.MESSAGE, message);
        po.saveEventually(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null)
                    c.setStatus(Conversation_act.STATUS_SENT);
                else
                    c.setStatus(Conversation_act.STATUS_FAILED);

                chatAdapter.notifyDataSetChanged();
                Log.d("Notify: ", "NotifyDataSetChanged called");
            }
        });
    }

    /**
     * Load the conversation list from Parse server and save the date of last
     * message that will be used to load only recent new messages
     */
    private void loadConversationList() {
        ParseQuery<ParseObject> q = ParseQuery.getQuery(ParseConstants.CHAT);
        //in the start, the list will always be 0
        if (conversationList.size() == 0) {
            // load all messages...
            ArrayList<String> al = new ArrayList<String>();
            al.add(receiver);
            al.add(UserList_act.user.getUsername());
            q.whereContainedIn(ParseConstants.CHAT_SENDER, al);
            q.whereContainedIn(ParseConstants.CHAT_RECEIVER, al);
        } else {
            // load only newly received message..
            if (lastMsgDate != null)
                q.whereGreaterThan(ParseConstants.CREATED_AT, lastMsgDate);
            // get the messages the receiver sends from parse
            q.whereEqualTo(ParseConstants.CHAT_SENDER, receiver);
            q.whereEqualTo(ParseConstants.CHAT_RECEIVER, UserList_act.user.getUsername());
        }
        q.orderByDescending(ParseConstants.CREATED_AT);
        q.setLimit(30);
        q.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> li, ParseException e) {
                if (li != null && li.size() > 0) {
//                    aQuery.id(R.id.progress).visibility(View.VISIBLE);
                    for (int i = li.size() - 1; i >= 0; i--) {
                        ParseObject po = li.get(i);
                        Conversation_act c = new Conversation_act(po.getString(ParseConstants.MESSAGE), po.getCreatedAt(), po.getString(ParseConstants.CHAT_SENDER));
                        conversationList.add(c);

                        if (lastMsgDate == null || lastMsgDate.before(c.getDate()))
                            lastMsgDate = c.getDate();

                        chatAdapter.notifyDataSetChanged();
                        Log.d("Notify: ", "NotifyDataSetChanged called");
                    }
                }
                aQuery.id(R.id.progress).visibility(View.GONE);
                //progressBar.setVisibility(View.GONE);
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (isRunning)
                            loadConversationList();
                        //Log.d("PostDelayed: ", "loadConversationList called");
                    }
                }, 1000);
            }
        });

    }

    /**
     * The Class ChatAdapter is the adapter class for Chat ListView. This
     * adapter shows the Sent or Received Chat message in each list item.
     */
    private class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return conversationList.size();
        }

        @Override
        public Conversation_act getItem(int arg0) {
            return conversationList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        /**
         * Amount of different layouts to inflate
         * In this case 2 layouts, chat_item_sent and chat_item_receive
         * @return - returns the number of types of Views that will be created by getView
         */
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        /**
         * @param position - the item contained in the list of Conversations
         * @return - if isSent() = true, then return chat_item_sent else return chat_item_receive
         */
        @Override
        public int getItemViewType(int position) {
            return getItem(position).isSent() ? Constants.CHAT_ITEM_SENT : Constants.CHAT_ITEM_RECEIVE;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            ViewHolder viewHolder;
            Conversation_act conversationItem = getItem(position);

            // If the layout to inflate is the chat item send.
            if (getItemViewType(position) == Constants.CHAT_ITEM_SENT) {
                if (convertView == null) {
                    Log.d("ConvertView: ", "is null for chat_item_send");
                    convertView = getLayoutInflater().inflate(R.layout.chat_item_sent, null);
                    viewHolder = new ViewHolder();
                    viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.lbl1);
                    viewHolder.messageTextView = (TextView) convertView.findViewById(R.id.lbl2);
                    viewHolder.statusTextView = (TextView) convertView.findViewById(R.id.lbl3);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                    Log.d("Recycling...", "ViewHolder recycling chat_item_send");
                }

                viewHolder.timeTextView.setText(DateUtils.getRelativeDateTimeString(Chat_act.this, conversationItem
                        .getDate().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));

                viewHolder.messageTextView.setText(conversationItem.getMsg());


                if (conversationItem.getStatus() == Conversation_act.STATUS_SENT)
                    viewHolder.statusTextView.setText("Delivered");
                else if (conversationItem.getStatus() == Conversation_act.STATUS_SENDING)
                    viewHolder.statusTextView.setText("Sending...");
                else
                    viewHolder.statusTextView.setText("Failed");

                return convertView;

            }
            // If the layout to inflate is the chat item receive.
            else if (getItemViewType(position) == Constants.CHAT_ITEM_RECEIVE) {
                if (convertView == null) {
                    Log.d("ConvertView: ", "is null for chat_item_receive");
                    convertView = getLayoutInflater().inflate(R.layout.chat_item_receive, null);
                    viewHolder = new ViewHolder();
                    viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.lbl1);
                    viewHolder.messageTextView = (TextView) convertView.findViewById(R.id.lbl2);
                    viewHolder.statusTextView = (TextView) convertView.findViewById(R.id.lbl3);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                    Log.d("Recycling...", "ViewHolder recycling chat_item_receive");
                }
                viewHolder.timeTextView.setText(DateUtils.getRelativeDateTimeString(Chat_act.this, conversationItem
                        .getDate().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));

                viewHolder.messageTextView.setText(conversationItem.getMsg());

                viewHolder.statusTextView.setText("");

                return convertView;
            }

            return null;
        }

    }

    // ViewHolder to hold the id's of the views
    public static class ViewHolder {
        public TextView timeTextView;
        public TextView messageTextView;
        public TextView statusTextView;
    }

    private void makeWindowTransition(){
        getWindow().setEnterTransition(makeEnterTransition());
        getWindow().setReturnTransition(makeReturnTransition());
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
    }

    private Transition makeEnterTransition() {
        TransitionSet enterTransition = new TransitionSet();

        Transition groupTransition = new Slide(Gravity.LEFT).setDuration(500);
        Transition fadeIn = new Fade().setDuration(1000);


        LinearLayout chatLayoutMain = (LinearLayout)findViewById(R.id.chat_layout_main);
        chatLayoutMain.setTransitionGroup(true);
        groupTransition.addTarget(chatLayoutMain);

        enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.excludeTarget(R.id.toolbar_teal, true);
        enterTransition.addTransition(fadeIn);
        enterTransition.addTransition(groupTransition);

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

        Transition autoTransition = new AutoTransition().setDuration(700);
        Transition fade = new Fade().setDuration(800);
        enterTransition.addTransition(fade);
        enterTransition.addTransition(autoTransition);
        return enterTransition;
    }

    private Transition makeReturnTransition() {
        TransitionSet enterTransition = new TransitionSet();

        Transition upperPartSlide = new Slide(Gravity.LEFT);
        enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
//        enterTransition.excludeTarget(R.id.toolbar_teal, true);
        enterTransition.addTransition(upperPartSlide);

        Transition fade = new Fade();
        enterTransition.addTransition(fade);

        enterTransition.setDuration(500);
        return enterTransition;
    }
}