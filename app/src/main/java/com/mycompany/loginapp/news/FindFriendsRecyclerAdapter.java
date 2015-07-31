package com.mycompany.loginapp.news;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 11-04-2015.
 * Adapter that loads all the chat messages
 */
public class FindFriendsRecyclerAdapter extends RecyclerView.Adapter<FindFriendsRecyclerAdapter.MyFriendsViewHolder> {
    public static final String LOG = FindFriendsRecyclerAdapter.class.getSimpleName();
    private int mFriendsViewHolderPosition;
    private Context activityContext;
    private LayoutInflater layoutInflater;
    private Picasso picasso;
    private List<ParseUser> userList;

    final private FriendsClickListener mMainViewClickListener;
    final private ClickListener mButtonClickListener;
    final RecyclerView mRecyclerView;
    private GestureDetector mGestureDetector;

    public FindFriendsRecyclerAdapter(Context actContext, RecyclerView mRecyclerView, final ClickListener mButtonClickListener, final FriendsClickListener mMainViewClickListener) {
        this.activityContext = actContext;
        this.userList = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(actContext);
        this.picasso = Picasso.with(actContext);
        this.mMainViewClickListener = mMainViewClickListener;
        this.mButtonClickListener = mButtonClickListener;
        this.mRecyclerView = mRecyclerView;
    }

    public void setChatList(List<ParseUser> userList) {
        this.userList = new ArrayList<>(userList);
        this.notifyDataSetChanged();
    }

    /**
     * Gets the user corresponding to the position in the adapter
     */
    public ParseUser getParseUser(int position) {
        return userList.get(position);
    }

    //sets up the gestureDetector for the main layout
    private void setUpGestureDetector(){

        this.mGestureDetector = new GestureDetector(activityContext, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                final View v = mRecyclerView.getChildAt(mFriendsViewHolderPosition);

                if (mMainViewClickListener != null) {
                    v.onTouchEvent(e); // called to alert the RecyclerView that a touch has happened and the ripple should be drawn
                    mMainViewClickListener.onClick(mFriendsViewHolderPosition);
                }

                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }
        });

    }

    @Override
    public MyFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Initialize the ViewHolder

        final MyFriendsViewHolder mFindFriendsViewHolder;

        //Get the layout for this Recycler item
        View convertView = layoutInflater.inflate(R.layout.find_friends_item, parent, false);

        //Create a new ViewHolder with the Recycler item view
        mFindFriendsViewHolder = new MyFriendsViewHolder(convertView, Constants.TYPE_HEADER);
        this.setUpGestureDetector();

        return mFindFriendsViewHolder;

    }

    @Override
    public void onBindViewHolder(final MyFriendsViewHolder mFindFriendsViewHolder, final int position) {
        if (userList != null && userList.size() > 0) {
            final ParseUser parseUserObject = userList.get(position);
            //mFindFriendsViewHolder.mMainItemLayout.setEnabled(true);
            mFindFriendsViewHolder.mChatButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
                    mFriendsViewHolderPosition = position;

                    if(mGestureDetector.onTouchEvent(event)){
                        mMainViewClickListener.onClick(position);
                        return true;
                    }

                    return false;
                }
            });

            mFindFriendsViewHolder.arrowImage.setImageDrawable(ApplicationMain.getAppContext().getResources().getDrawable(R.drawable.ic_chevron_right_grey600_24dp));
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
        return userList.size();
    }

    public static class MyFriendsViewHolder extends RecyclerView.ViewHolder {
        public int position;
        //for chat content
        public TextView username;
        public TextView name;
        public ImageView userProfilePhoto;
        public ImageView arrowImage;
        public Button mChatButton;
        public RelativeLayout mMainItemLayout;
        //for progressBar at the top when loading older messages
        public ProgressBar mProgressBar;

        public MyFriendsViewHolder(View itemView, int ViewType) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_list_username);
            userProfilePhoto = (ImageView) itemView.findViewById(R.id.user_list_username_profile_parse_image);
            name = (TextView) itemView.findViewById(R.id.user_list_name);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrow_imageView);
            mChatButton = (Button) itemView.findViewById(R.id.find_friends_chat_button);
            mMainItemLayout = (RelativeLayout) itemView.findViewById(R.id.find_friends_main_item_layout);
        }
    }
}
