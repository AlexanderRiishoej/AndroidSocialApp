package com.mycompany.loginapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Class Chat is the Activity class that holds main chat screen. It shows
 * all the conversation messages between two users and also allows the receiver to
 * send and receive messages.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Chat extends BaseActivity
{

    /** The Conversation list. */
    private ArrayList<Conversation> conversationList;

    /** The chat adapter. */
    private ChatAdapter chatAdapter;

    /** The Editext to compose the message. */
    private EditText txt;

    /** The receiver name of receiver. */
    private String receiver;

    /** The date of last message in conversation. */
    private Date lastMsgDate;

    /** Flag to hold if the activity is running or not. */
    private boolean isRunning;

    /** The handler. */
    private static Handler handler;

    private static AQuery aQuery;

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.chat);
        aQuery = new AQuery(this);

        conversationList = new ArrayList<Conversation>();
        ListView list = (ListView) findViewById(R.id.list);
        chatAdapter = new ChatAdapter();
        list.setAdapter(chatAdapter);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setStackFromBottom(true);

        txt = (EditText) findViewById(R.id.txt);
        txt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        receiver = getIntent().getStringExtra(Constants.EXTRA_DATA);
        aQuery.id(R.id.toolbar_title).text(receiver);

        handler = new Handler();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.chat;
    }

    public void sendMessage(View view){
        if (view.getId() == R.id.btnSend)
        {
            sendMessage();
        }
    }
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        isRunning = true;
        loadConversationList();
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        isRunning = false;
    }

    /**
     * Call this method to Send message to opponent. It does nothing if the text
     * is empty otherwise it creates a Parse object for Chat message and send it
     * to Parse server.
     */
    private void sendMessage()
    {
        if (txt.length() == 0)
            return;

//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);

        String message = txt.getText().toString();
        final Conversation c = new Conversation(message, new Date(), UserList.user.getUsername());
        c.setStatus(Conversation.STATUS_SENDING);
        conversationList.add(c);
        chatAdapter.notifyDataSetChanged();
        txt.setText(null);

        ParseObject po = new ParseObject("Chat");
        po.put("sender", UserList.user.getUsername());
        po.put("receiver", receiver);
        // po.put("createdAt", "");
        po.put("message", message);
        po.saveEventually(new SaveCallback() {

            @Override
            public void done(ParseException e)
            {
                if (e == null)
                    c.setStatus(Conversation.STATUS_SENT);
                else
                    c.setStatus(Conversation.STATUS_FAILED);

                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Load the conversation list from Parse server and save the date of last
     * message that will be used to load only recent new messages
     */
    private void loadConversationList()
    {
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Chat");
        if (conversationList.size() == 0)
        {
            // load all messages...
            ArrayList<String> al = new ArrayList<String>();
            al.add(receiver);
            al.add(UserList.user.getUsername());
            q.whereContainedIn("sender", al);
            q.whereContainedIn("receiver", al);
        }
        else
        {
            // load only newly received message..
            if (lastMsgDate != null)
                q.whereGreaterThan("createdAt", lastMsgDate);
            q.whereEqualTo("sender", receiver);
            q.whereEqualTo("receiver", UserList.user.getUsername());
        }
        q.orderByDescending("createdAt");
        q.setLimit(30);
        q.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> li, ParseException e)
            {
                if (li != null && li.size() > 0)
                {
                    for (int i = li.size() - 1; i >= 0; i--)
                    {
                        ParseObject po = li.get(i);
                        Conversation c = new Conversation(po.getString("message"), po.getCreatedAt(), po.getString("sender"));
                        conversationList.add(c);

                        if (lastMsgDate == null || lastMsgDate.before(c.getDate()))
                            lastMsgDate = c.getDate();

                        chatAdapter.notifyDataSetChanged();
                    }
                }
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run()
                    {
                        if (isRunning)
                            loadConversationList();
                    }
                }, 1000);
            }
        });

    }

    /**
     * The Class ChatAdapter is the adapter class for Chat ListView. This
     * adapter shows the Sent or Receieved Chat message in each list item.
     */
    private class ChatAdapter extends BaseAdapter
    {
        ViewHolder viewHolder;
        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount()
        {
            return conversationList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Conversation getItem(int arg0)
        {
            return conversationList.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View convertView, ViewGroup arg2)
        {
            Conversation conversationItem = getItem(pos);
            if(convertView == null){
                if (conversationItem.isSent()) {
                    convertView = getLayoutInflater().inflate(R.layout.chat_item_sent, null);
                }
                else {
                    convertView = getLayoutInflater().inflate(R.layout.chat_item_receive, null);
                }
                viewHolder = new ViewHolder();
                viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.lbl1);
                viewHolder.messageTextView = (TextView) convertView.findViewById(R.id.lbl2);
                viewHolder.statusTextView = (TextView) convertView.findViewById(R.id.lbl3);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.timeTextView.setText(DateUtils.getRelativeDateTimeString(Chat.this, conversationItem
                    .getDate().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));

            viewHolder.messageTextView.setText(conversationItem.getMsg());

            if (conversationItem.isSent())
            {
                if (conversationItem.getStatus() == Conversation.STATUS_SENT)
                    viewHolder.statusTextView.setText("Delivered");
                else if (conversationItem.getStatus() == Conversation.STATUS_SENDING)
                    viewHolder.statusTextView.setText("Sending...");
                else
                    viewHolder.statusTextView.setText("Failed");
            }
            else
                viewHolder.statusTextView.setText("");

            return convertView;
        }

    }

    public static class ViewHolder {
        public TextView timeTextView;
        public TextView messageTextView;
        public TextView statusTextView;
}
}