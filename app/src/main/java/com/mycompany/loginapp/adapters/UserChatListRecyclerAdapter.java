package com.mycompany.loginapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.chat.UserChatList_act;
import com.mycompany.loginapp.constants.ParseConstants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Alexander on 11-04-2015.
 */
public class UserChatListRecyclerAdapter extends RecyclerView.Adapter<UserChatListRecyclerAdapter.MyUserChatListViewHolder> {
    public static final String LOG = UserChatList_act.class.getSimpleName();
    private Context activityContext;
    private List<ParseObject> userChatList;
    private LayoutInflater layoutInflater;

    public UserChatListRecyclerAdapter(Context actContext, List<ParseObject> userChatList){
        this.activityContext = actContext;
        this.userChatList = userChatList;
        this.layoutInflater = LayoutInflater.from(actContext);
    }

    @Override
    public MyUserChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Initialize the ViewHolder
        MyUserChatListViewHolder myUserChatListViewHolder;
        //Get the layout for this Recycler item
        View convertView = layoutInflater.inflate(R.layout.user_chat_list_item, parent, false);
        //Create a new ViewHolder with the Recycler item view
        myUserChatListViewHolder = new MyUserChatListViewHolder(convertView);

        return myUserChatListViewHolder;
    }

    @Override
    public void onBindViewHolder(MyUserChatListViewHolder viewHolder, int position) {
        //Get the current object from the list of chat users
        final ParseObject userChat = userChatList.get(position);
        //Create a final ViewHolder in order to being able to use it in an inner class
        final MyUserChatListViewHolder myUserChatListViewHolder = viewHolder;

        //Set the progressbar for the image to Visible since its going to get fetched soon
        myUserChatListViewHolder.imageProgressBar.setVisibility(View.VISIBLE);
        Log.d(LOG, "Username: " + userChat.getParseUser("username").getUsername());

        //Get the name of the chat user from the userChat object
        myUserChatListViewHolder.username.setText(userChat.getParseUser("username").getUsername());

        //Get the relation of this chat user object and perform a query that gets the chat represented by current user and another user
        userChat.getRelation(ParseConstants.CHAT_RELATION).getQuery().orderByDescending(ParseConstants.CREATED_AT).getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    Log.d(LOG, "ChatRelation: " + parseObject.getString("sender"));
                    Log.d(LOG, "ChatRelation: " + parseObject.getString("receiver"));
                    /** if the user chatting to, has the last  message in the chat equal to the sender, then show that message */
                    if (userChat.getParseUser("username").getUsername().equals(parseObject.getString("sender"))) {
                        myUserChatListViewHolder.lastChatMessage.setText("Last message received: " + parseObject.getString("message"));
                    }
                    /** else the last message is sent by me and should be showed as the last message seen in the conversation */
                    else if (userChat.getParseUser("username").getUsername().equals(parseObject.getString("receiver"))) {
                        myUserChatListViewHolder.lastChatMessage.setText("You: " + parseObject.getString("message"));
                    }
                    /** no messages has been sent */
                    else {
                        myUserChatListViewHolder.lastChatMessage.setText("");
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

        //Set the time to when the last message was sent/received
        myUserChatListViewHolder.dateOfLastChatMessage.setText(DateUtils.getRelativeDateTimeString(activityContext, userChat.getUpdatedAt().getTime(),
                DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));
        //viewHolder.username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chat_list_bitmap, 0);
        Log.d(LOG, "Parsefile Uri: " + userChat.getParseUser("username").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl());
        /** try - catch in order to prevent a crash in case of CircularImageView throwing a null pointer exception during bitmap loading */
        try {
            Picasso.with(activityContext).load(userChat.getParseUser("username").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).fit().
                    into(viewHolder.profilePicture, new Callback() {
                        @Override
                        public void onSuccess() {
                            //hide progressbar
                            myUserChatListViewHolder.imageProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            //hide progressbar
                            myUserChatListViewHolder.imageProgressBar.setVisibility(View.GONE);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return userChatList.size();
    }

    public static class MyUserChatListViewHolder extends RecyclerView.ViewHolder{
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

        public MyUserChatListViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_list_username);
            profilePicture = (CircularImageView) itemView.findViewById(R.id.user_list_username_profile_parse_image);
            imageProgressBar = (ProgressBar) itemView.findViewById(R.id.user_list_image_progress_bar);
            lastChatMessage = (TextView) itemView.findViewById(R.id.user_list_recent_chat);
            dateOfLastChatMessage = (TextView) itemView.findViewById(R.id.user_list_message_date);
        }
    }
}
