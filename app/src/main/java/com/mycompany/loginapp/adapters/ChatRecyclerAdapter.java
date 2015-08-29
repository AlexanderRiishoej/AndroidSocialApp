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
import com.squareup.picasso.Picasso;

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
    private List<Conversation> mConversationList;
    private ParseObject mUserChatObject;
    private Date lastMessageReceivedDate;
    private Date lastMessageUpdated;

    public ChatRecyclerAdapter(Context actContext, List<Conversation> conversationList, ParseObject userChatObject) {
        this.activityContext = actContext;
        this.mConversationList = conversationList;
        this.layoutInflater = LayoutInflater.from(actContext);
        this.picasso = Picasso.with(actContext);
        this.mUserChatObject = userChatObject;
    }

    //    public void setChatList(List<Conversation> mConversationList) {
//        //this.mConversationList = new ArrayList<>(mConversationList);
//        this.mConversationList.addAll(mConversationList);
//        this.notifyDataSetChanged();
//    }
//
//    public void addRangeList(List<Conversation> mConversationList) {
//        final int positionStart = this.mConversationList.size();
////        this.mConversationList.clear();
////        this.mConversationList.addAll(mConversationList);
//        this.mConversationList = new ArrayList<>(mConversationList);
//        //this.notifyDataSetChanged();
//        this.notifyItemRangeInserted(positionStart, mConversationList.size());
//    }
//
//    public void addNewMessages(List<Conversation> mConversationList){
//        final int positionStart = this.mConversationList.size();
//        this.mConversationList.addAll(mConversationList);
//        this.notifyItemRangeInserted(positionStart, mConversationList.size());
//    }
    public void setChatList(List<Conversation> conversationList) {
        this.mConversationList.addAll(conversationList);
        this.notifyDataSetChanged();
    }

    //inserts conversation objects one-by-one at position zero
    public void addRangeList(List<Conversation> conversationList) {
        //final int positionStart = this.mConversationList.size();
        for (Conversation c : conversationList) {
            this.mConversationList.add(0, c);
            this.notifyItemInserted(0);
        }
    }

    public void addTestMessageSingle(List<Conversation> conversationList) {
        final int positionStart = this.mConversationList.size();
        this.mConversationList.addAll(0, conversationList);
        //this.mConversationList = new ArrayList<>(mConversationList);
        //this.notifyDataSetChanged();
        this.notifyItemRangeInserted(positionStart, conversationList.size());
    }

    public void addNewMessages(List<Conversation> conversationList) {
        final int positionStart = this.mConversationList.size();
        this.mConversationList.addAll(conversationList);
        this.notifyItemRangeInserted(positionStart, conversationList.size());
    }

    //used to add progress-conversation-item last in the list
    public void addProgressItem(Conversation conversation, int index) {
        this.mConversationList.add(index, conversation);
        this.notifyItemInserted(index);
    }

    //used to add sending messages at the start of the list
    public void addItem(Conversation conversation) {
        this.mConversationList.add(conversation);
        //in order to make the "seen image" disappear
        this.notifyItemChanged(mConversationList.size() - 2);
        //update that an item has been inserted
        this.notifyItemInserted(mConversationList.size() - 1);
    }

    //used to remove progress-conversation-item last in the list
    public void removeItemAtPosition(int index) {
        this.mConversationList.remove(index);
        this.notifyItemRemoved(index);
    }

    /** Query parse to check if the message has been seen */
    private void getLastMessageInChat() {
        mUserChatObject.getRelation(ParseConstants.CHAT_RELATION).getQuery().whereGreaterThan(ParseConstants.UPDATED_AT, lastMessageReceivedDate).
                orderByDescending(ParseConstants.CREATED_AT).getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null && parseObject != null) {
                    mConversationList.get(mConversationList.size() - 1).isSeen = true;
                    lastMessageReceivedDate = parseObject.getUpdatedAt();
                    notifyItemChanged(mConversationList.size() - 1);
                } else {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getLastMessageInChat();
//                        }
//                    }, 1000);
                }
            }
        });
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
            final Conversation mChatConversationObject = mConversationList.get(position);

            if (chatViewHolder.mChatReceiverSeenImageView.getDrawable() != null) {
                //set the recycled imageView's image to null
                chatViewHolder.mChatReceiverSeenImageView.setImageBitmap(null);
                chatViewHolder.mChatReceiverSeenImageView.setImageDrawable(null);
            }
            //this query is to show the little image in the bottom-right corner of the chat if the message has been seen
            if (position == mConversationList.size() - 1) {
                if (mChatConversationObject.isSeen) {
                    //load the profile photo of the user in the "username" field
                    picasso.load(mChatConversationObject.getReceiver().getParseFile(ParseConstants.PROFILE_PICTURE).
                            getUrl()).noPlaceholder().centerCrop().fit().
                            into(chatViewHolder.mChatReceiverSeenImageView);
                } else {
                    this.getLastMessageInChat();
                }
            }

            final CharSequence chatMessageDate = DateUtils.getRelativeDateTimeString(activityContext, mConversationList.get(position)
                    .getDate().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0);
            chatViewHolder.timeTextView.setText(chatMessageDate);

            final String chatMessage = mConversationList.get(position).getMessage();
            chatViewHolder.messageTextView.setText(chatMessage);

            if (mConversationList.get(position).getStatus() == Conversation.STATUS_SENT) {
                chatViewHolder.statusTextView.setText("Delivered");
            } else if (mConversationList.get(position).getStatus() == Conversation.STATUS_SENDING) {
                chatViewHolder.statusTextView.setText("Sending...");
            } else {
                chatViewHolder.statusTextView.setText("Failed");
            }

        } else if (itemViewType == Constants.CHAT_ITEM_RECEIVE) {
            final Conversation mChatConversationObject = mConversationList.get(position);

            MySingleton.getMySingleton().getPicasso().load(mChatConversationObject.getSender().
                    getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).centerCrop().fit().noPlaceholder().into(chatViewHolder.mChatImageView);

            chatViewHolder.timeTextView.setText(DateUtils.getRelativeDateTimeString(activityContext, mConversationList.get(position)
                    .getDate().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0));

            chatViewHolder.messageTextView.setText(mConversationList.get(position).getMessage());

            chatViewHolder.statusTextView.setText("");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mConversationList.get(position).isProgress() ? Constants.TYPE_HEADER : mConversationList.get(position).isSender() ? Constants.CHAT_ITEM_SENT : Constants.CHAT_ITEM_RECEIVE;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mConversationList.size();
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
                timeTextView = (TextView) itemView.findViewById(R.id.timestamp_of_message_sent);
                messageTextView = (TextView) itemView.findViewById(R.id.chat_item_sent_message);
                statusTextView = (TextView) itemView.findViewById(R.id.chat_item_status_text_sent);
                //mChatImageView = (ImageView) itemView.findViewById(R.id.chat_image);
                mChatReceiverSeenImageView = (ImageView) itemView.findViewById(R.id.chat_image_seen);
                mChatLayout = (RelativeLayout) itemView.findViewById(R.id.v1);
                //ColorFilter http://blog.danlew.net/2014/08/18/fast-android-asset-theming-with-colorfilter/
                final Drawable backgroundDrawable = ApplicationMain.getAppContext().getResources().getDrawable(R.drawable.chat_bubble_outgoing);
                final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
                backgroundDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                mChatLayout.setBackground(backgroundDrawable);
            } else {
                timeTextView = (TextView) itemView.findViewById(R.id.timestamp_of_message_receive);
                messageTextView = (TextView) itemView.findViewById(R.id.chat_item_receive_message);
                statusTextView = (TextView) itemView.findViewById(R.id.chat_item_status_text_receive);
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
