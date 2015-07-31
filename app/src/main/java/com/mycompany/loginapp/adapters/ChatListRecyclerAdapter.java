package com.mycompany.loginapp.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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
import com.mycompany.loginapp.chat.Conversation;
import com.mycompany.loginapp.chat.oldNotUsed.UserChatList_act;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Alexander on 11-04-2015.
 */
public class ChatListRecyclerAdapter extends RecyclerView.Adapter<ChatListRecyclerAdapter.MyUserChatListViewHolder> {
    public static final String LOG = UserChatList_act.class.getSimpleName();
    private Context activityContext;
    private List<ParseObject> mUserChatObjectList;
    private LayoutInflater layoutInflater;
    private Picasso picasso;

    public ChatListRecyclerAdapter(Context actContext, List<ParseObject> mUserChatObjectList) {
        this.activityContext = actContext;
        this.mUserChatObjectList = mUserChatObjectList;
        this.layoutInflater = LayoutInflater.from(actContext);
        this.picasso = Picasso.with(actContext);
    }

    public void setUserChatObjectList(List<ParseObject> mUserChatObjectList) {
        this.mUserChatObjectList.addAll(mUserChatObjectList);
    }

    //inserts conversation objects one-by-one at position zero
    public void addRangeList(List<ParseObject> userChatObjects) {
        //final int positionStart = this.conversationList.size();
        for (ParseObject p : userChatObjects) {
            this.mUserChatObjectList.add(0, p);
            this.notifyItemInserted(0);
        }
    }

    //used to add sending messages at the start of the list
    public void addItemAtIndex(int index, ParseObject parseObject) {
        this.mUserChatObjectList.add(index, parseObject);
        this.notifyItemInserted(index);
    }

    /**
     * Gets the chat participant in this chat
     */
    private String getChatReceiver(ParseObject userChat) {
        // Splits the string according to the pattern, which is the current username. Returns the username of the chat participant.
        // If CurrentUser is matched as the first part of the ChatUserId then the first array index will be empty
        String[] splitChatUserArray = userChat.getString("chatUserId").split(ApplicationMain.mCurrentParseUser.getUsername());
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
        //Get the layout for this Recycler item
        View convertView = layoutInflater.inflate(R.layout.user_chat_list_item, parent, false);
        //Create a new ViewHolder with the Recycler item view
        myUserChatListViewHolder = new MyUserChatListViewHolder(convertView, Constants.TYPE_ITEM);

        return myUserChatListViewHolder;
    }

    @Override
    public void onBindViewHolder(MyUserChatListViewHolder viewHolder, final int position) {
        final MyUserChatListViewHolder myUserChatListViewHolder = viewHolder;
        if (position < mUserChatObjectList.size() && mUserChatObjectList.size() > 0) {
            myUserChatListViewHolder.username.setText(null);
            myUserChatListViewHolder.lastChatMessage.setText(null);
            myUserChatListViewHolder.dateOfLastChatMessage.setText(null);
            myUserChatListViewHolder.imageProgressBar.setVisibility(View.GONE);
            myUserChatListViewHolder.profilePicture.setImageBitmap(null);
            myUserChatListViewHolder.seenPicture.setImageBitmap(null);
            myUserChatListViewHolder.seenPicture.setImageDrawable(null);
            myUserChatListViewHolder.sentPicture.setImageDrawable(null);
            myUserChatListViewHolder.sentPicture.setImageBitmap(null);

            //Get the current object from the list of chat users
            final ParseObject userChatObject = mUserChatObjectList.get(position); // Minus 1 due to Header being position 0
            //Set the progressbar for the image to Visible since its going to get fetched soon
            myUserChatListViewHolder.imageProgressBar.setVisibility(View.VISIBLE);
            Log.d(LOG, "Username: " + userChatObject.getParseUser("username").getUsername());
            //Get the name of the chat user from the userChatObject object
            final String chatParticipant = getChatReceiver(userChatObject);
            myUserChatListViewHolder.username.setText(chatParticipant);
            //Get the most recent chat object of this userChatObject relation
            userChatObject.getRelation(ParseConstants.CHAT_RELATION).getQuery().orderByDescending(ParseConstants.CREATED_AT).getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject chatParseObject, ParseException e) {
                    if (e == null) {
                        Log.d(LOG, "ChatRelation: " + chatParseObject.getString("sender") + "Postition: " + position);
                        Log.d(LOG, "ChatRelation: " + chatParseObject.getString("receiver") + "Postition: " + position);
                        //if the user chatting to, has the last  message in the chat equal to the sender, then show that message as the last message sent
                        if (chatParticipant.equals(chatParseObject.getString(ParseConstants.CHAT_SENDER))) {
//                                myUserChatListViewHolder.lastChatMessage.setText(chatParticipant + ": " + chatParseObject.getString("message"));
                            final String chatMessage = chatParseObject.getString(ParseConstants.MESSAGE);
                            myUserChatListViewHolder.lastChatMessage.setText(chatMessage);
                            // if the current user has not yet seen the message sent, highlight the mUserChatObject on screen
                            if (!chatParseObject.getBoolean(ParseConstants.SEEN)) {
                                myUserChatListViewHolder.lastChatMessage.setTextColor(activityContext.getResources().getColor(R.color.black));
                                myUserChatListViewHolder.username.setTextColor(activityContext.getResources().getColor(R.color.black));
                                myUserChatListViewHolder.username.setTypeface(myUserChatListViewHolder.username.getTypeface(), Typeface.BOLD);
                                myUserChatListViewHolder.dateOfLastChatMessage.setTextColor(activityContext.getResources().getColor(R.color.teal_500));
                            }
                        }
                        // else if the last message is sent by me my message should be shown as the last message seen in the conversation
                        else if (chatParticipant.equals(chatParseObject.getString(ParseConstants.CHAT_RECEIVER))) {
                            final String chatMessage = "You: " + chatParseObject.getString(ParseConstants.MESSAGE);
                            myUserChatListViewHolder.lastChatMessage.setText(chatMessage);
                            myUserChatListViewHolder.lastChatMessage.setTextColor(activityContext.getResources().getColor(R.color.secondary_text_icons_light_theme));
                            myUserChatListViewHolder.username.setTextColor(activityContext.getResources().getColor(R.color.black));
                            myUserChatListViewHolder.username.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                            myUserChatListViewHolder.dateOfLastChatMessage.setTextColor(activityContext.getResources().getColor(R.color.secondary_text_icons_light_theme));

                            //if the chatParticipant equals to the receiver of the last message in the chat and the receiver has seen the message, then show that chatParticipants picture as
                            //an indicator of the receiver has seen the message
                            if (chatParseObject.getBoolean(ParseConstants.SEEN)) {
                                //check if the receiver equals to either the creator of the chat or participator in order to determining which picture to load
                                if (userChatObject.getParseUser(ParseConstants.USERNAME).getUsername().equals(chatParticipant)) { //if the receiver equals to the participator of the chat
                                    picasso.load(userChatObject.getParseUser(ParseConstants.USERNAME).getParseFile(ParseConstants.PROFILE_PICTURE).
                                            getUrl()).noPlaceholder().centerCrop().fit().
                                            into(myUserChatListViewHolder.seenPicture);
                                }
                                else { //if the receiver equals to the creator of the chat
                                    MySingleton.getMySingleton().getPicasso().load(userChatObject.getParseUser("createdBy").
                                            getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).centerCrop().fit().noPlaceholder().into(myUserChatListViewHolder.seenPicture);
                                }
                            }
                            // else it has not yet been seen and an image indicating that i have send the message should be shown instead
                            else {
                                picasso.load(R.drawable.ic_checkbox_marked_circle_grey600_24dp).noPlaceholder().centerCrop().fit().
                                        into(myUserChatListViewHolder.seenPicture);
                                final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
                                myUserChatListViewHolder.seenPicture.setColorFilter(color, PorterDuff.Mode.SRC_IN);

                                if (chatParticipant.equals("Ib")) {
                                    Log.d(LOG, "IB SEEN == FALSE");
                                }
                            }
                        }
                        /** no messages has been sent */
                        else {
                            myUserChatListViewHolder.lastChatMessage.setText("");
                            myUserChatListViewHolder.seenPicture.setImageBitmap(null);
                            myUserChatListViewHolder.seenPicture.setImageDrawable(null);

                            myUserChatListViewHolder.sentPicture.setImageDrawable(null);
                            myUserChatListViewHolder.sentPicture.setImageBitmap(null);
                        }


                    } else {
                        e.printStackTrace();
                    }
                }
            });

            //Set the time to when the last message was sent/received
            final CharSequence date = DateUtils.getRelativeTimeSpanString(activityContext, userChatObject.getUpdatedAt().getTime(), false);
            if (myUserChatListViewHolder.dateOfLastChatMessage != null && !date.equals(myUserChatListViewHolder.dateOfLastChatMessage.getText())) {
                myUserChatListViewHolder.dateOfLastChatMessage.setText(date);
            }

//                myUserChatListViewHolder.dateOfLastChatMessage.setText(DateUtils.getRelativeDateTimeString(activityContext, userChatObject.getUpdatedAt().getTime(),
//                        DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));
//            Log.d(LOG, "Parsefile Uri: " + userChatObject.getParseUser("username").getParseFile(ParseConstants.PROFILE_PICTURE).getUrl());
            /** try - catch in order to prevent a crash in case of CircularImageView throwing a null pointer exception during bitmap loading */
            if (chatParticipant.equals(userChatObject.getParseUser(ParseConstants.USERNAME).getUsername())) {
                try {
                    if(userChatObject.getParseUser(ParseConstants.USERNAME).getParseFile(ParseConstants.PROFILE_PICTURE) != null) {
                        picasso.load(userChatObject.getParseUser(ParseConstants.USERNAME).getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).noPlaceholder().centerCrop().fit().
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
                    }
                    else {
                        MySingleton.getMySingleton().getPicasso().load(R.drawable.com_facebook_profile_picture_blank_portrait).centerCrop().fit().into(myUserChatListViewHolder.profilePicture);
                        final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
                        myUserChatListViewHolder.profilePicture.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                        myUserChatListViewHolder.imageProgressBar.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if(userChatObject.getParseUser(ParseConstants.CREATED_BY).getParseFile(ParseConstants.PROFILE_PICTURE) != null) {
                        picasso.load(userChatObject.getParseUser(ParseConstants.CREATED_BY).getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).noPlaceholder().centerCrop().fit().
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
                    }
                    else {
                        MySingleton.getMySingleton().getPicasso().load(R.drawable.com_facebook_profile_picture_blank_portrait).centerCrop().fit().into(myUserChatListViewHolder.profilePicture);
                        final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
                        myUserChatListViewHolder.profilePicture.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                        myUserChatListViewHolder.imageProgressBar.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
//        if (position == 0) {
//            return Constants.TYPE_HEADER;
//        }
        return Constants.TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mUserChatObjectList.size(); // +1 for Header +50 for more users
    }

    public static class MyUserChatListViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        /**
         * Special circular imageView using a framework
         */
        public ImageView profilePicture;

        public ImageView seenPicture;
        public ImageView sentPicture;

        /**
         * ProgressBar for the small profile_image pictures in the list loading
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
