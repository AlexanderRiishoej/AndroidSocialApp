package com.mycompany.loginapp.news;

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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 11-04-2015.
 * Adapter that loads all the chat messages
 */
public class NewChatRecyclerAdapter extends RecyclerView.Adapter<NewChatRecyclerAdapter.MyNewChatViewHolder> {
    private static final String LOG = NewChatRecyclerAdapter.class.getSimpleName();
    final private LayoutInflater layoutInflater;
    private List<ParseUser> userList;
    final private FriendsClickListener mMainViewClickListener;
    final private ClickListener mButtonClickListener;

    public NewChatRecyclerAdapter(Context actContext, List<ParseUser> mFriendsList, final ClickListener mButtonClickListener, final FriendsClickListener mMainViewClickListener) {
        this.userList = mFriendsList;
        this.layoutInflater = LayoutInflater.from(actContext);
        this.mMainViewClickListener = mMainViewClickListener;
        this.mButtonClickListener = mButtonClickListener;
    }

    public void setChatList(List<ParseUser> userList) {
        if(this.userList == null) {
            this.userList = userList;
            this.notifyDataSetChanged();
        }
        else {
            this.userList.clear();
            this.userList = userList;
            this.notifyItemRangeChanged(0, userList.size());
        }
    }

    /**
     * Gets the user corresponding to the position in the adapter
     */
    public ParseUser getParseUser(int position) {
        return userList.get(position);
    }

    @Override
    public MyNewChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Initialize the ViewHolder

        final MyNewChatViewHolder mFindFriendsViewHolder;

        //Get the layout for this Recycler item
        View convertView = layoutInflater.inflate(R.layout.new_chat_item, parent, false);

        //Create a new ViewHolder with the Recycler item view
        mFindFriendsViewHolder = new MyNewChatViewHolder(convertView, Constants.TYPE_HEADER);
        //this.setUpGestureDetector();

        return mFindFriendsViewHolder;

    }

    @Override
    public void onBindViewHolder(final MyNewChatViewHolder mNewChatViewHolder, final int position) {
            final ParseUser parseUserObject = userList.get(position);

            //mFindFriendsViewHolder.mMainItemLayout.setEnabled(true);
            mNewChatViewHolder.mChatButton.setOnTouchListener(new View.OnTouchListener() {
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

            mNewChatViewHolder.mMainItemLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.onTouchEvent(event);

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mMainViewClickListener.onClick(position);
                    }
                    return false;
                }
            });

            mNewChatViewHolder.arrowImage.setImageDrawable(ApplicationMain.getAppContext().getResources().getDrawable(R.drawable.ic_chevron_right_grey600_24dp));
            mNewChatViewHolder.username.setText(parseUserObject.getUsername());
            mNewChatViewHolder.name.setText(parseUserObject.getString("fullName"));

            if (parseUserObject.getParseFile(ParseConstants.PROFILE_PICTURE) != null) {
                MySingleton.getMySingleton().getPicasso().load(parseUserObject.getParseFile(ParseConstants.PROFILE_PICTURE).getUrl()).centerCrop().fit().into(mNewChatViewHolder.userProfilePhoto);
            } else {
                MySingleton.getMySingleton().getPicasso().load(R.drawable.com_facebook_profile_picture_blank_portrait).centerCrop().fit().into(mNewChatViewHolder.userProfilePhoto);
                final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
                mNewChatViewHolder.userProfilePhoto.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class MyNewChatViewHolder extends RecyclerView.ViewHolder {
        public int position;
        //for chat content
        @Bind(R.id.user_list_username) TextView username;
        @Bind(R.id.user_list_name) TextView name;
        @Bind(R.id.user_list_username_profile_parse_image) ImageView userProfilePhoto;
        @Bind(R.id.arrow_imageView) ImageView arrowImage;
        @Bind(R.id.find_friends_chat_button) Button mChatButton;
        @Bind(R.id.find_friends_main_item_layout) RelativeLayout mMainItemLayout;
        //for progressBar at the top when loading older messages
        public ProgressBar mProgressBar;

        public MyNewChatViewHolder(View itemView, int ViewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
