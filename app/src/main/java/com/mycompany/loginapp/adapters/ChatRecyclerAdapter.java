package com.mycompany.loginapp.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.chat.Chat_act;
import com.mycompany.loginapp.chat.Conversation;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alexander on 11-04-2015.
 * Adapter that loads all the chat messages
 */
public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.MyChatViewHolder> {
    public static final String LOG = Chat_act.class.getSimpleName();
    private Context activityContext;
    private LayoutInflater layoutInflater;
    private Picasso picasso;
    private List<Conversation> conversationList;
    private ParseObject userChatObject;

    public ChatRecyclerAdapter(Context actContext, List<Conversation> conversationList, ParseObject userChatObject) {
        this.activityContext = actContext;
        this.conversationList = conversationList;
        this.layoutInflater = LayoutInflater.from(actContext);
        this.picasso = Picasso.with(actContext);
        this.userChatObject = userChatObject;
    }

    //    public void setChatList(List<Conversation> conversationList) {
//        //this.conversationList = new ArrayList<>(conversationList);
//        this.conversationList.addAll(conversationList);
//        this.notifyDataSetChanged();
//    }
//
//    public void addRangeList(List<Conversation> conversationList) {
//        final int positionStart = this.conversationList.size();
////        this.conversationList.clear();
////        this.conversationList.addAll(conversationList);
//        this.conversationList = new ArrayList<>(conversationList);
//        //this.notifyDataSetChanged();
//        this.notifyItemRangeInserted(positionStart, conversationList.size());
//    }
//
//    public void addNewMessages(List<Conversation> conversationList){
//        final int positionStart = this.conversationList.size();
//        this.conversationList.addAll(conversationList);
//        this.notifyItemRangeInserted(positionStart, conversationList.size());
//    }
    public void setChatList(List<Conversation> conversationList) {
        this.conversationList.addAll(conversationList);
        this.notifyDataSetChanged();
    }

    //inserts conversation objects one-by-one at position zero
    public void addRangeList(List<Conversation> conversationList) {
        //final int positionStart = this.conversationList.size();
        for (Conversation c : conversationList) {
            this.conversationList.add(0, c);
            this.notifyItemInserted(0);
        }
    }

    public void addTestMessageSingle(List<Conversation> conversationList) {
        final int positionStart = this.conversationList.size();
        this.conversationList.addAll(0, conversationList);
        //this.conversationList = new ArrayList<>(conversationList);
        //this.notifyDataSetChanged();
        this.notifyItemRangeInserted(positionStart, conversationList.size());
    }

    public void addNewMessages(List<Conversation> conversationList) {
        final int positionStart = this.conversationList.size();
        this.conversationList.addAll(conversationList);
        this.notifyItemRangeInserted(positionStart, conversationList.size());
    }

    //used to add progress-conversation-item last in the list
    public void addProgressItem(Conversation conversation, int index) {
        this.conversationList.add(index, conversation);
        this.notifyItemInserted(index);
    }

    //used to add sending messages at the start of the list
    public void addItem(Conversation conversation) {
        this.conversationList.add(conversation);
        this.notifyItemInserted(conversationList.size() - 1);
    }

    //used to remove progress-conversation-item last in the list
    public void removeItemAtPosition(int index) {
        this.conversationList.remove(index);
        this.notifyItemRemoved(index);
    }

    private String getChatReceiver() {
        String[] splitChatUserArray = userChatObject.getString("chatUserId").split(ParseUser.getCurrentUser().getUsername());
        String chatParticipantHolder = "";
        for (String match : splitChatUserArray) {
            if (!match.equals("")) {
                chatParticipantHolder = match;
            }
        }
        return chatParticipantHolder;
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Initialize the ViewHolder

        final MyChatViewHolder myChatViewHolder;

        if (viewType == Constants.TYPE_HEADER) {
            //Get the layout for this Recycler item
            View convertView = layoutInflater.inflate(R.layout.chat_load_more_progress_bar, parent, false);
            //Create a new ViewHolder with the Recycler item view
            myChatViewHolder = new MyChatViewHolder(convertView, Constants.TYPE_HEADER);
        } else if (viewType == Constants.CHAT_ITEM_SENT) {
            //Get the layout for this Recycler item
            View convertView = layoutInflater.inflate(R.layout.chat_item_sent, parent, false);
            //Create a new ViewHolder with the Recycler item view
            myChatViewHolder = new MyChatViewHolder(convertView, Constants.CHAT_ITEM_SENT);
        } else {
            //Get the layout for this Recycler item
            View convertView = layoutInflater.inflate(R.layout.chat_item_receive, parent, false);
            //Create a new ViewHolder with the Recycler item view
            myChatViewHolder = new MyChatViewHolder(convertView, Constants.CHAT_ITEM_RECEIVE);

        }
        return myChatViewHolder;

    }

    @Override
    public void onBindViewHolder(final MyChatViewHolder chatViewHolder, final int position) {
        final int itemViewType = getItemViewType(position);

        //progressBar
        if (itemViewType == Constants.TYPE_HEADER) {
            chatViewHolder.mProgressBar.setIndeterminate(true);
        }
        if (itemViewType == Constants.CHAT_ITEM_SENT) {
            //set the recycled imageView's image to null
            if (chatViewHolder.mChatReceiverSeenImageView.getDrawable() != null) {
                chatViewHolder.mChatReceiverSeenImageView.setImageBitmap(null);
                chatViewHolder.mChatReceiverSeenImageView.setImageDrawable(null);
            }
            //gets the chat object associated with current user & receiver at the last position of the chat
            if (position == conversationList.size() - 1) {
                userChatObject.getRelation(ParseConstants.CHAT_RELATION).getQuery().orderByDescending(ParseConstants.CREATED_AT).getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject chatParseObject, ParseException e) {
                        //if the receiver has seen the most recently message send by current user (me), then show the receivers photo at the bottom-right
                        if (chatParseObject.getBoolean(ParseConstants.SEEN) && ParseUser.getCurrentUser().getUsername().equals(chatParseObject.getString(ParseConstants.CHAT_SENDER))) {
                            //load the profile photo of the user in the "username" field
                            if (userChatObject.getParseUser(ParseConstants.USERNAME).getUsername().equals(getChatReceiver())) {
                                picasso.load(userChatObject.getParseUser(ParseConstants.USERNAME).getParseFile(ParseConstants.PROFILE_PICTURE).
                                        getUrl()).noPlaceholder().centerCrop().fit().
                                        into(chatViewHolder.mChatReceiverSeenImageView);
                            } else {
                                //load the profile photo of the user in the "createdBy" field
                                picasso.load(userChatObject.getParseUser(ParseConstants.CREATED_BY).getParseFile(ParseConstants.PROFILE_PICTURE).
                                        getUrl()).noPlaceholder().centerCrop().fit().
                                        into(chatViewHolder.mChatReceiverSeenImageView);
                            }
                        }
                    }
                });
            }

            final CharSequence chatMessageDate = DateUtils.getRelativeDateTimeString(activityContext, conversationList.get(position)
                    .getDate().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0);
            chatViewHolder.timeTextView.setText(chatMessageDate);

            final String chatMessage = conversationList.get(position).getMsg();
            chatViewHolder.messageTextView.setText(chatMessage);

            if (conversationList.get(position).getStatus() == Conversation.STATUS_SENT) {
                chatViewHolder.statusTextView.setText("Delivered");
            } else if (conversationList.get(position).getStatus() == Conversation.STATUS_SENDING) {
                chatViewHolder.statusTextView.setText("Sending...");
            } else {
                chatViewHolder.statusTextView.setText("Failed");
            }

        } else if (itemViewType == Constants.CHAT_ITEM_RECEIVE) {
            //loads the profile picture of the receiver into the receivers photo at the left in the chat
            if (userChatObject.getParseUser(ParseConstants.USERNAME).getParseFile(ParseConstants.PROFILE_PICTURE) != null) {

                if (userChatObject.getParseUser(ParseConstants.USERNAME).getUsername().equals(getChatReceiver())) {
                    MySingleton.getMySingleton().getPicasso().load(userChatObject.getParseUser(ParseConstants.USERNAME).
                            getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).centerCrop().fit().noPlaceholder().into(chatViewHolder.mChatImageView);
                } else {
                    MySingleton.getMySingleton().getPicasso().load(userChatObject.getParseUser("createdBy").
                            getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).centerCrop().fit().noPlaceholder().into(chatViewHolder.mChatImageView);
                }

            }

            chatViewHolder.timeTextView.setText(DateUtils.getRelativeDateTimeString(activityContext, conversationList.get(position)
                    .getDate().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));

            chatViewHolder.messageTextView.setText(conversationList.get(position).getMsg());

            chatViewHolder.statusTextView.setText("");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return conversationList.get(position).isProgress() ? Constants.TYPE_HEADER : conversationList.get(position).isSent() ? Constants.CHAT_ITEM_SENT : Constants.CHAT_ITEM_RECEIVE;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public static class MyChatViewHolder extends RecyclerView.ViewHolder {
        //for chat content
        public TextView timeTextView;
        public TextView messageTextView;
        public TextView statusTextView;
        public ImageView mChatImageView;
        public ImageView mChatReceiverSeenImageView;
        public RelativeLayout mChatLayout;
        //for progressBar at the top when loading older messages
        public ProgressBar mProgressBar;

        public MyChatViewHolder(View itemView, int ViewType) {
            super(itemView);
            if (ViewType == Constants.TYPE_HEADER) {
                mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_load_old_messages);
            } else if (ViewType == Constants.CHAT_ITEM_SENT) {
                timeTextView = (TextView) itemView.findViewById(R.id.lbl1);
                messageTextView = (TextView) itemView.findViewById(R.id.lbl2);
                statusTextView = (TextView) itemView.findViewById(R.id.lbl3);
                //mChatImageView = (ImageView) itemView.findViewById(R.id.chat_image);
                mChatReceiverSeenImageView = (ImageView) itemView.findViewById(R.id.chat_image_seen);
                mChatLayout = (RelativeLayout) itemView.findViewById(R.id.v1);
                //ColorFilter http://blog.danlew.net/2014/08/18/fast-android-asset-theming-with-colorfilter/
                final Drawable backgroundDrawable = ApplicationMain.getAppContext().getResources().getDrawable(R.drawable.chat_bubble_outgoing);
                final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
                backgroundDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                mChatLayout.setBackground(backgroundDrawable);
            } else {
                timeTextView = (TextView) itemView.findViewById(R.id.lbl1);
                messageTextView = (TextView) itemView.findViewById(R.id.lbl2);
                statusTextView = (TextView) itemView.findViewById(R.id.lbl3);
                mChatImageView = (ImageView) itemView.findViewById(R.id.chat_image);
                mChatLayout = (RelativeLayout) itemView.findViewById(R.id.v1);
                //ColorFilter http://blog.danlew.net/2014/08/18/fast-android-asset-theming-with-colorfilter/
                final Drawable backgroundDrawable = ApplicationMain.getAppContext().getResources().getDrawable(R.drawable.chat_bubble_incoming);
                final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.divider);
                backgroundDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                mChatLayout.setBackground(backgroundDrawable);
            }
        }
    }
}
