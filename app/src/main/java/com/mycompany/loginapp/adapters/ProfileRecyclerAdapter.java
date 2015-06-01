package com.mycompany.loginapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.chat.UserChatList_act;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.profile.ProfileImageHolder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Alexander on 15-04-2015.
 */
public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.MyProfileViewHolder> {

    public static final String LOG = UserChatList_act.class.getSimpleName();
    private Context activityContext;
    private List<ParseObject> userChatList;
    private LayoutInflater layoutInflater;
    private String name = ParseUser.getCurrentUser().getUsername();
    private String profilePicturePath;
    private AQuery aQuery;
    private Picasso picasso;

    private String mNavTitles[] = {"Mobile", "Profile", "Chat", "New Chat", "Mail", "Settings"};
    private int mIcons[] = {R.drawable.ic_local_phone_grey600_24dp, R.drawable.ic_person_grey600_24dp,
            R.drawable.ic_messenger_grey600_24dp, R.drawable.ic_chat_grey600_24dp, R.drawable.ic_action_email, R.drawable.ic_settings_applications_grey600_24dp};

    public ProfileRecyclerAdapter(Context actContext) {
        this.activityContext = actContext;
        this.layoutInflater = LayoutInflater.from(actContext);
        this.aQuery = new AQuery(actContext);
        this.picasso = Picasso.with(actContext);
    }

    public void updateProfileImage(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
        this.notifyItemChanged(0);
    }

    public void updateRecyclerItem(int position) {
        this.notifyItemRangeChanged(position, 1);
    }

    @Override
    public ProfileRecyclerAdapter.MyProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyProfileViewHolder myProfileViewHolder;

        if (viewType == Constants.TYPE_ITEM) {

            //View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_list_items, parent, false); //Inflating the layout
            /** View layout to be inflated onto the Recycler */
            View convertView = layoutInflater.inflate(R.layout.profile_list_items, parent, false); //Inflating the layout
            myProfileViewHolder = new MyProfileViewHolder(convertView, Constants.TYPE_ITEM); //Creating ViewHolder and passing the object of type view

            return myProfileViewHolder; // Returning the created object

        } else if (viewType == Constants.TYPE_HEADER) {

            /** View layout to be inflated onto the Recycler */
            View convertView = layoutInflater.inflate(R.layout.private_profile_header, parent, false); //Inflating the layout
            myProfileViewHolder = new MyProfileViewHolder(convertView, Constants.TYPE_HEADER); //Creating ViewHolder and passing the object of type view

            return myProfileViewHolder; //returning the object created
        } else if (viewType == Constants.TYPE_SECOND_ITEM) {

            /** View layout to be inflated onto the Recycler */
            View convertView = layoutInflater.inflate(R.layout.profile_follower_friends, parent, false); //Inflating the layout
            myProfileViewHolder = new MyProfileViewHolder(convertView, Constants.TYPE_SECOND_ITEM); //Creating ViewHolder and passing the object of type view

            return myProfileViewHolder; //returning the object created
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ProfileRecyclerAdapter.MyProfileViewHolder myProfileViewHolder, int position) {
        if (myProfileViewHolder.holderId == 0) {                              // as the list view is going to be called after the navigation_header view so we decrement the
            final MyProfileViewHolder viewHolderFinal = myProfileViewHolder;

            try {
                if (ProfileImageHolder.imageFile != null && ProfileImageHolder.imageFile.exists()) {
                    // the noPlaceholder() did the trick... Now i am not receiving any null pointer exception regarding the gedWidth().
                    picasso.load(ProfileImageHolder.imageFile).fit().noPlaceholder().into(myProfileViewHolder.profilePicture);
                } else {
                    ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ParseUser.getCurrentUser().getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                picasso.load("file:" + parseUser.getString(ParseConstants.PROFILE_PICTURE_PATH)).fit().noPlaceholder().
                                        into(viewHolderFinal.profilePicture);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                ex.getMessage();
            }

            myProfileViewHolder.name.setText(name);
            myProfileViewHolder.city.setText("Espergærde, 3060");
            myProfileViewHolder.birthdate.setText("Maj 23, 1991");
            myProfileViewHolder.status.setText("online");

        } else if (myProfileViewHolder.holderId == 1) {
            myProfileViewHolder.friends.setText("577");
            myProfileViewHolder.followers.setText("9");
            myProfileViewHolder.in_common.setText("488");
            myProfileViewHolder.videos.setText("216");
            myProfileViewHolder.photos.setText("288");
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return 2; // mNavTitles.length + 1
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Constants.TYPE_HEADER;
        } else if (position == 1) {
            return Constants.TYPE_SECOND_ITEM;
        } else {
            return Constants.TYPE_ITEM;
        }
    }

    public static class MyProfileViewHolder extends RecyclerView.ViewHolder {
        public int holderId;

        // List items
        public TextView description;
        public ImageView icon;
        public TextView information;
        public ImageView iconEditField;

        // Header
        public ImageView profilePicture;
        public TextView city;
        public TextView name;
        public TextView status;
        public TextView birthdate;

        // Second list item
        public TextView friends;
        public TextView followers;
        public TextView in_common;
        public TextView videos;
        public TextView photos;

        public MyProfileViewHolder(View itemView, int ViewType) {
            super(itemView);
            if (ViewType == Constants.TYPE_ITEM) {
                holderId = Constants.TYPE_ITEM;
                description = (TextView) itemView.findViewById(R.id.descriptionText);
                icon = (ImageView) itemView.findViewById(R.id.rowIcon);
                information = (TextView) itemView.findViewById(R.id.rowText);
                iconEditField = (ImageView) itemView.findViewById(R.id.rowIconEdit);
            } else if (ViewType == Constants.TYPE_HEADER) {
                holderId = Constants.TYPE_HEADER;
                profilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
                city = (TextView) itemView.findViewById(R.id.city);
                name = (TextView) itemView.findViewById(R.id.wall_post_username);
                status = (TextView) itemView.findViewById(R.id.status);
                birthdate = (TextView) itemView.findViewById(R.id.birth_date);
            } else if (ViewType == Constants.TYPE_SECOND_ITEM) {
                holderId = Constants.TYPE_SECOND_ITEM;
                friends = (TextView) itemView.findViewById(R.id.friends);
                followers = (TextView) itemView.findViewById(R.id.followers);
                in_common = (TextView) itemView.findViewById(R.id.in_common);
                videos = (TextView) itemView.findViewById(R.id.videos);
                photos = (TextView) itemView.findViewById(R.id.photos);
            }
        }
    }
}
