package com.mycompany.loginapp.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.chat.Chat_act;
import com.mycompany.loginapp.chat.Conversation;
import com.mycompany.loginapp.chat.UserChatList_act;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 11-04-2015.
 */
public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.MyChatViewHolder> {
    public static final String LOG = Chat_act.class.getSimpleName();
    private Context activityContext;
    private LayoutInflater layoutInflater;
    private Picasso picasso;
    private ArrayList<Conversation> conversationList;

    public ChatRecyclerAdapter(Context actContext, List<Conversation> conversationList) {
        this.activityContext = actContext;
        this.conversationList = new ArrayList<>(conversationList);
        this.layoutInflater = LayoutInflater.from(actContext);
        this.picasso = Picasso.with(actContext);
    }

    public void setChatList(List<Conversation> conversationList) {
        this.conversationList = new ArrayList<>(conversationList);
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Initialize the ViewHolder
        final MyChatViewHolder myChatViewHolder;

        if (viewType == Constants.CHAT_ITEM_SENT) {
            //Get the layout for this Recycler item
            View convertView = layoutInflater.inflate(R.layout.chat_item_sent, parent, false);
            //Create a new ViewHolder with the Recycler item view
            myChatViewHolder = new MyChatViewHolder(convertView, Constants.TYPE_HEADER);
            return myChatViewHolder;
        } else {
            //Get the layout for this Recycler item
            View convertView = layoutInflater.inflate(R.layout.chat_item_receive, parent, false);
            //Create a new ViewHolder with the Recycler item view
            myChatViewHolder = new MyChatViewHolder(convertView, Constants.TYPE_ITEM);

            return myChatViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(MyChatViewHolder chatViewHolder, final int position) {
        if (getItemViewType(position) == Constants.CHAT_ITEM_SENT) {
            chatViewHolder.timeTextView.setText(DateUtils.getRelativeDateTimeString(activityContext, conversationList.get(position)
                    .getDate().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));

            chatViewHolder.messageTextView.setText(conversationList.get(position).getMsg());


            if (conversationList.get(position).getStatus() == Conversation.STATUS_SENT) {
                chatViewHolder.statusTextView.setText("Delivered");
            } else if (conversationList.get(position).getStatus() == Conversation.STATUS_SENDING) {
                chatViewHolder.statusTextView.setText("Sending...");
            } else {
                chatViewHolder.statusTextView.setText("Failed");
            }
        } else {
            chatViewHolder.timeTextView.setText(DateUtils.getRelativeDateTimeString(activityContext, conversationList.get(position)
                    .getDate().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));

            chatViewHolder.messageTextView.setText(conversationList.get(position).getMsg());

            chatViewHolder.statusTextView.setText("");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return conversationList.get(position).isSent() ? Constants.CHAT_ITEM_SENT : Constants.CHAT_ITEM_RECEIVE;
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public static class MyChatViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTextView;
        public TextView messageTextView;
        public TextView statusTextView;

        public MyChatViewHolder(View itemView, int ViewType) {
            super(itemView);
            timeTextView = (TextView) itemView.findViewById(R.id.lbl1);
            messageTextView = (TextView) itemView.findViewById(R.id.lbl2);
            statusTextView = (TextView) itemView.findViewById(R.id.lbl3);

        }
    }
}
