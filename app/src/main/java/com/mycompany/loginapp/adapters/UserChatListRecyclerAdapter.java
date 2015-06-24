package com.mycompany.loginapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
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
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.chat.UserChatList_act;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

/**
 * Created by Alexander on 11-04-2015.
 */
public class UserChatListRecyclerAdapter extends RecyclerView.Adapter<UserChatListRecyclerAdapter.MyUserChatListViewHolder> {
    public static final String LOG = UserChatList_act.class.getSimpleName();
    private Context activityContext;
    private List<ParseObject> userChatList;
    private LayoutInflater layoutInflater;
    private Picasso picasso;

    public UserChatListRecyclerAdapter(Context actContext, List<ParseObject> userChatList) {
        this.activityContext = actContext;
        this.userChatList = userChatList;
        this.layoutInflater = LayoutInflater.from(actContext);
        this.picasso = Picasso.with(actContext);
    }

    public void setUserChatList(List<ParseObject> userChatList) {
        this.userChatList = userChatList;
    }

    /** Gets the chat participant in this chat */
    private String getChatParticipant(ParseObject userChat){
        // Splits the string according to the pattern, which is the current username. Returns the username of the chat participant.
        // If CurrentUser is matched as the first part of the ChatUserId then the first array index will be empty
        String[] splitChatUserArray = userChat.getString("chatUserId").split(ParseUser.getCurrentUser().getUsername());
        String chatParticipantHolder = "";
        for (String match : splitChatUserArray) {
            if (!match.equals("")) {
                chatParticipantHolder = match;
            }
        }

        return chatParticipantHolder;
    }

    @Override
    public MyUserChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Initialize the ViewHolder
        final MyUserChatListViewHolder myUserChatListViewHolder;

        if (viewType == Constants.TYPE_HEADER) {
            //Get the layout for this Recycler item
            View convertView = layoutInflater.inflate(R.layout.recent_chat_header, parent, false);
            //Create a new ViewHolder with the Recycler item view
            myUserChatListViewHolder = new MyUserChatListViewHolder(convertView, Constants.TYPE_HEADER);
            return myUserChatListViewHolder;
        } else {
            //Get the layout for this Recycler item
            View convertView = layoutInflater.inflate(R.layout.user_chat_list_item, parent, false);
            //Create a new ViewHolder with the Recycler item view
            myUserChatListViewHolder = new MyUserChatListViewHolder(convertView, Constants.TYPE_ITEM);

            return myUserChatListViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(MyUserChatListViewHolder viewHolder, final int position) {
        final MyUserChatListViewHolder myUserChatListViewHolder = viewHolder;
        //viewHolder.itemView.setTag();
        if (position == Constants.TYPE_HEADER) {
            Log.d("POS OF RECYCLERVIEW: ", "" + position);
        } else if (position > userChatList.size()) {
            if(userChatList.size() > 0) {
            myUserChatListViewHolder.username.setText(null);
            myUserChatListViewHolder.lastChatMessage.setText(null);
            myUserChatListViewHolder.dateOfLastChatMessage.setText(null);
            myUserChatListViewHolder.imageProgressBar.setVisibility(View.GONE);
            myUserChatListViewHolder.profilePicture.setImageBitmap(null);


            Log.d("POS OF RECYCLERVIEW: ", "" + position);
            final ParseObject userChat = userChatList.get(0); // Minus 1 due to Header being position 0
            //Create a final ViewHolder in order to being able to use it in an inner class
            //final MyUserChatListViewHolder myUserChatListViewHolder = viewHolder;

            //Set the progressbar for the image to Visible since its going to get fetched soon
            myUserChatListViewHolder.imageProgressBar.setVisibility(View.VISIBLE);
            Log.d(LOG, "Username: " + userChat.getParseUser("username").getUsername());

            final String chatParticipant = getChatParticipant(userChat);
            //Get the name of the chat user from the userChat object
            myUserChatListViewHolder.username.setText(chatParticipant);
            //Get the relation of this chat user object and perform a query that gets the chat represented by current user and another user
            userChat.getRelation(ParseConstants.CHAT_RELATION).getQuery().orderByDescending(ParseConstants.CREATED_AT).getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        Log.d(LOG, "ChatRelation: " + parseObject.getString("sender") + "Postition: " + position);
                        Log.d(LOG, "ChatRelation: " + parseObject.getString("receiver") + "Postition: " + position);
                        /** if the user chatting to, has the last  message in the chat equal to the sender, then show that message */
                        if (chatParticipant.equals(parseObject.getString("sender"))) {
                            myUserChatListViewHolder.lastChatMessage.setText("Last message received: " + parseObject.getString("message"));
                        }
                        /** else the last message is sent by me and should be showed as the last message seen in the conversation */
                        else if (chatParticipant.equals(parseObject.getString("receiver"))) {
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
            //viewHolder.profilePicture = null;
            //viewHolder.username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chat_list_bitmap, 0);
            Log.d(LOG, "Parsefile Uri: " + userChat.getParseUser("username").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl());
            /** try - catch in order to prevent a crash in case of CircularImageView throwing a null pointer exception during bitmap loading */
            if (chatParticipant.equals(userChat.getParseUser("username").getUsername())) {
                try {
                    picasso.load(userChat.getParseUser("username").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).noPlaceholder().fit().
                            into(myUserChatListViewHolder.profilePicture, new Callback() {
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
            } else {
                try {
                    picasso.load(userChat.getParseUser("createdBy").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).noPlaceholder().fit().
                            into(myUserChatListViewHolder.profilePicture, new Callback() {
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
            }

        } else {
            if(position <= userChatList.size()) {
                myUserChatListViewHolder.username.setText(null);
                myUserChatListViewHolder.lastChatMessage.setText(null);
                myUserChatListViewHolder.dateOfLastChatMessage.setText(null);
                myUserChatListViewHolder.imageProgressBar.setVisibility(View.GONE);
                myUserChatListViewHolder.profilePicture.setImageBitmap(null);
                myUserChatListViewHolder.seenPicture.setImageBitmap(null);
                myUserChatListViewHolder.sentPicture.setImageDrawable(null);

                //Get the current object from the list of chat users
                final ParseObject userChat = userChatList.get(position - 1); // Minus 1 due to Header being position 0
                //Set the progressbar for the image to Visible since its going to get fetched soon
                myUserChatListViewHolder.imageProgressBar.setVisibility(View.VISIBLE);
                Log.d(LOG, "Username: " + userChat.getParseUser("username").getUsername());
                final String chatParticipant = getChatParticipant(userChat);
                //Get the name of the chat user from the userChat object
                myUserChatListViewHolder.username.setText(chatParticipant);
                //Get the most recent chat object of this userChat relation
                userChat.getRelation(ParseConstants.CHAT_RELATION).getQuery().orderByDescending(ParseConstants.CREATED_AT).getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject chatParseObject, ParseException e) {
                        if (e == null) {
                            Log.d(LOG, "ChatRelation: " + chatParseObject.getString("sender") + "Postition: " + position);
                            Log.d(LOG, "ChatRelation: " + chatParseObject.getString("receiver") + "Postition: " + position);
                            //if the user chatting to, has the last  message in the chat equal to the sender, then show that message as the last message sent
                            if (chatParticipant.equals(chatParseObject.getString(ParseConstants.CHAT_SENDER))) {
//                                myUserChatListViewHolder.lastChatMessage.setText(chatParticipant + ": " + chatParseObject.getString("message"));
                                myUserChatListViewHolder.lastChatMessage.setText(chatParseObject.getString("message"));
                                myUserChatListViewHolder.sentPicture.setImageDrawable(null);
                                // if the current user has not yet seen the message sent, highlight the userChatObject on screen
                                if (!chatParseObject.getBoolean(ParseConstants.SEEN)) {
                                    myUserChatListViewHolder.lastChatMessage.setTextColor(activityContext.getResources().getColor(R.color.black));
                                    myUserChatListViewHolder.username.setTextColor(activityContext.getResources().getColor(R.color.black));
                                    myUserChatListViewHolder.username.setTypeface(myUserChatListViewHolder.username.getTypeface(), Typeface.BOLD);
                                    myUserChatListViewHolder.dateOfLastChatMessage.setTextColor(activityContext.getResources().getColor(R.color.teal_500));
                                }
                            }
                            // else if the last message is sent by me it should be shown as the last message seen in the conversation
                            else if (chatParticipant.equals(chatParseObject.getString(ParseConstants.CHAT_RECEIVER))) {
                                myUserChatListViewHolder.lastChatMessage.setText("You: " + chatParseObject.getString("message"));
                                myUserChatListViewHolder.lastChatMessage.setTextColor(activityContext.getResources().getColor(R.color.secondary_text_icons_light_theme));
                                myUserChatListViewHolder.username.setTextColor(activityContext.getResources().getColor(R.color.black));
                                myUserChatListViewHolder.username.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                                myUserChatListViewHolder.dateOfLastChatMessage.setTextColor(activityContext.getResources().getColor(R.color.secondary_text_icons_light_theme));
                                //if the chatParticipant equals to the receiver of the last message in the chat and the receiver has seen the message, then show that chatParticipants picture
                                if (chatParseObject.getBoolean(ParseConstants.SEEN)) {
                                    picasso.load(userChat.getParseUser("username").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).noPlaceholder().centerCrop().fit().
                                            into(myUserChatListViewHolder.seenPicture);
                                }
                                // else it has not yet been seen and an image indicating that i have send the message should be shown instead
                                else {
                                    picasso.load(R.drawable.ic_checkbox_marked_circle_grey600_24dp).noPlaceholder().centerCrop().fit().
                                            into(myUserChatListViewHolder.sentPicture);
                                }
                            }
                            /** no messages has been sent */
                            else {
                                myUserChatListViewHolder.lastChatMessage.setText("");
                                myUserChatListViewHolder.seenPicture.setImageBitmap(null);
                                myUserChatListViewHolder.sentPicture.setImageDrawable(null);
                            }


                        } else {
                            e.printStackTrace();
                        }
                    }
                });

                //Set the time to when the last message was sent/received
                myUserChatListViewHolder.dateOfLastChatMessage.setText(DateUtils.getRelativeTimeSpanString(activityContext, userChat.getUpdatedAt().getTime(), false));
//                myUserChatListViewHolder.dateOfLastChatMessage.setText(DateUtils.getRelativeDateTimeString(activityContext, userChat.getUpdatedAt().getTime(),
//                        DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));
                Log.d(LOG, "Parsefile Uri: " + userChat.getParseUser("username").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl());
                /** try - catch in order to prevent a crash in case of CircularImageView throwing a null pointer exception during bitmap loading */
                if (chatParticipant.equals(userChat.getParseUser("username").getUsername())) {
                    try {
                        picasso.load(userChat.getParseUser("username").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).noPlaceholder().centerCrop().fit().
                                into(myUserChatListViewHolder.profilePicture, new Callback() {
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
                } else {
                    try {
                        picasso.load(userChat.getParseUser("createdBy").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).noPlaceholder().centerCrop().fit().
                                into(myUserChatListViewHolder.profilePicture, new Callback() {
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
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Constants.TYPE_HEADER;
        }
        return Constants.TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return userChatList.size()+50; // +1 for Header +50 for more users
    }

    public static class MyUserChatListViewHolder extends RecyclerView.ViewHolder {
        int adapterPosition;
        public TextView username;
        /**
         * Special circular imageView using a framework
         */
        public ImageView profilePicture;

        public ImageView seenPicture;
        public ImageView sentPicture;

        /**
         * ProgressBar for the small profile pictures in the list loading
         */
        public ProgressBar imageProgressBar;

        public TextView lastChatMessage;

        public TextView dateOfLastChatMessage;

        public MyUserChatListViewHolder(View itemView, int ViewType) {
            super(itemView);
            if (ViewType == Constants.TYPE_ITEM) {
                username = (TextView) itemView.findViewById(R.id.user_list_username);
                profilePicture = (ImageView) itemView.findViewById(R.id.user_list_username_profile_parse_image);
                imageProgressBar = (ProgressBar) itemView.findViewById(R.id.user_list_image_progress_bar);
                lastChatMessage = (TextView) itemView.findViewById(R.id.user_list_recent_chat);
                dateOfLastChatMessage = (TextView) itemView.findViewById(R.id.user_list_message_date);
                seenPicture = (ImageView) itemView.findViewById(R.id.seen_image);
                sentPicture = (ImageView) itemView.findViewById(R.id.sent_image);
            }
        }
    }
}
