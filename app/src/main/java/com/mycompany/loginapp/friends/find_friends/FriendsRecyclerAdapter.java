package com.mycompany.loginapp.friends.find_friends;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.FriendsClickListener;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 11-04-2015.
 * Adapter that loads all the chat messages
 */
public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsRecyclerAdapter.MyFriendsViewHolder> {
    private static final String LOG = FriendsRecyclerAdapter.class.getSimpleName();
    private List<ParseUser> mFriendsList;
    final private LayoutInflater layoutInflater;
    final private FriendsClickListener mMainViewClickListener;
    final private ClickListener mButtonClickListener;

    public FriendsRecyclerAdapter(Context actContext, List<ParseUser> mFriendsList, final ClickListener mButtonClickListener, final FriendsClickListener mMainViewClickListener) {
        this.mFriendsList = mFriendsList;
//        this.mFriendsList = new ArrayList<>(mFriendsList);
        this.layoutInflater = LayoutInflater.from(actContext);
        this.mMainViewClickListener = mMainViewClickListener;
        this.mButtonClickListener = mButtonClickListener;
    }

    public void setChatList(List<ParseUser> userList) {
        if (this.mFriendsList == null) {
            this.mFriendsList = userList;
            this.notifyDataSetChanged();
        } else {
            this.mFriendsList.clear();
            this.mFriendsList = userList;
            this.notifyItemRangeChanged(0, userList.size());
        }
    }

    /**
     * Gets the user corresponding to the position in the adapter
     */
    public ParseUser getParseUser(int position) {
        return mFriendsList.get(position);
    }

    @Override
    public MyFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Initialize the ViewHolder

        final MyFriendsViewHolder mFindFriendsViewHolder;
        if (viewType == Constants.TYPE_HEADER) {
            //Get the layout for this Recycler item
            View convertView = layoutInflater.inflate(R.layout.subheader_item, parent, false);

            //Create a new ViewHolder with the Recycler item view
            mFindFriendsViewHolder = new MyFriendsViewHolder(convertView, Constants.TYPE_HEADER);
            //this.setUpGestureDetector();
        } else {
            //Get the layout for this Recycler item
            View convertView = layoutInflater.inflate(R.layout.find_friends_list_item, parent, false);

            //Create a new ViewHolder with the Recycler item view
            mFindFriendsViewHolder = new MyFriendsViewHolder(convertView, Constants.TYPE_ITEM);
        }
        return mFindFriendsViewHolder;

    }

    @Override
    public void onBindViewHolder(final MyFriendsViewHolder mFindFriendsViewHolder, final int position) {
        if (getItemViewType(position) == Constants.TYPE_ITEM) {
            final ParseUser parseUserObject = mFriendsList.get(position - 1);

            //mFindFriendsViewHolder.mMainItemLayout.setEnabled(true);
                mFindFriendsViewHolder.mChatButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.onTouchEvent(event);

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            //mFindFriendsViewHolder.mMainItemLayout.setEnabled(false);
                            mButtonClickListener.onClick(v, position);
                        }
                        return false;
                    }
                });


                mFindFriendsViewHolder.mMainItemLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.onTouchEvent(event);

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            mMainViewClickListener.onClick(position);
                        }
                        return false;
                    }
                });

            mFindFriendsViewHolder.username.setText(parseUserObject.getUsername());
            mFindFriendsViewHolder.name.setText(parseUserObject.getString("fullName"));

            if (parseUserObject.getParseFile(ParseConstants.PROFILE_PICTURE) != null) {
                MySingleton.getMySingleton().getPicasso().load(parseUserObject.getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).centerCrop().fit().into(mFindFriendsViewHolder.userProfilePhoto);
            } else {
                MySingleton.getMySingleton().getPicasso().load(R.drawable.com_facebook_profile_picture_blank_portrait).centerCrop().fit().into(mFindFriendsViewHolder.userProfilePhoto);
                final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
                mFindFriendsViewHolder.userProfilePhoto.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFriendsList != null ? mFriendsList.size() + 1 : 0; // +1 due to header
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Constants.TYPE_HEADER;
        }
        return Constants.TYPE_ITEM;
    }

    public void animateTo(List<ParseUser> friendsList) {
        this.applyAndAnimateRemovals(friendsList);
        this.applyAndAnimateAdditions(friendsList);
        this.applyAndAnimateMovedItems(friendsList);
    }

    private void applyAndAnimateRemovals(List<ParseUser> newModels) {
        for (int i = mFriendsList.size() - 1; i >= 0; i--) {
            final ParseUser parseUser = mFriendsList.get(i);
            if (!newModels.contains(parseUser)) {
                this.removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ParseUser> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ParseUser parseUser = newModels.get(i);
            if (!mFriendsList.contains(parseUser)) {
                this.addItem(i, parseUser);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ParseUser> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ParseUser parseUser = newModels.get(toPosition);
            final int fromPosition = mFriendsList.indexOf(parseUser);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                this.moveItem(fromPosition, toPosition);
            }
        }
    }

    public ParseUser removeItem(int position) {
        final ParseUser parseUser = mFriendsList.remove(position);
        this.notifyItemRemoved(position);
        return parseUser;
    }

    public void addItem(int position, ParseUser parseUser) {
        mFriendsList.add(position, parseUser);
        this.notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ParseUser parseUser = mFriendsList.remove(fromPosition);
        mFriendsList.add(toPosition, parseUser);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class MyFriendsViewHolder extends RecyclerView.ViewHolder {
        public int position;
        //for chat content
        @Bind(R.id.new_chat_username) TextView username;
        @Bind(R.id.new_chat_name) TextView name;
        @Bind(R.id.user_list_username_profile_parse_image) ImageView userProfilePhoto;
        @Bind(R.id.find_friends_chat_button) Button mChatButton;
        @Bind(R.id.find_friends_main_item_layout) RelativeLayout mMainItemLayout;
        //@Nullable @Bind(R.id.subheader_item) LinearLayout mSubHeaderLinearLayout;
        //for progressBar at the top when loading older messages
        public ProgressBar mProgressBar;

        public MyFriendsViewHolder(View itemView, int ViewType) {
            super(itemView);
            if(ViewType == Constants.TYPE_ITEM) {
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
