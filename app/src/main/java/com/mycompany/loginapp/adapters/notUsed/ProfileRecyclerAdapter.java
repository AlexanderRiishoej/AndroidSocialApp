package com.mycompany.loginapp.adapters.notUsed;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.ProfileGalleryAdapter;
import com.mycompany.loginapp.chat.oldNotUsed.UserChatList_act;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.profile.ProfileImageHolder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Alexander on 15-04-2015.
 */
public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.MyProfileViewHolder> {

    public static final String LOG = UserChatList_act.class.getSimpleName();
    private Context activityContext;
    private LayoutInflater layoutInflater;
    private String name = ParseUser.getCurrentUser().getUsername();
    private Picasso picasso;

    public ProfileRecyclerAdapter(Context actContext) {
        this.activityContext = actContext;
        this.layoutInflater = LayoutInflater.from(actContext);
        this.picasso = Picasso.with(actContext);
    }

    public void updateRecyclerItem(int position) {
        this.notifyItemChanged(position);
    }

    @Override
    public ProfileRecyclerAdapter.MyProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyProfileViewHolder myProfileViewHolder;

        if (viewType == Constants.TYPE_ITEM) {

            //View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_list_items, parent, false); //Inflating the layout
            /** View layout to be inflated onto the Recycler */
            View convertView = layoutInflater.inflate(R.layout.not_used_profile_list_items, parent, false); //Inflating the layout
            myProfileViewHolder = new MyProfileViewHolder(convertView, Constants.TYPE_ITEM, null); //Creating ViewHolder and passing the object of type view

            return myProfileViewHolder; // Returning the created object

        } else if (viewType == Constants.TYPE_HEADER) {

            /** View layout to be inflated onto the Recycler */
            View convertView = layoutInflater.inflate(R.layout.not_used_private_profile_header, parent, false); //Inflating the layout
            myProfileViewHolder = new MyProfileViewHolder(convertView, Constants.TYPE_HEADER, null); //Creating ViewHolder and passing the object of type view

            return myProfileViewHolder; //returning the object created
        } else if (viewType == Constants.TYPE_SECOND_ITEM) {

            /** View layout to be inflated onto the Recycler */
            View convertView = layoutInflater.inflate(R.layout.not_used_profile_follower_friends, parent, false); //Inflating the layout
            myProfileViewHolder = new MyProfileViewHolder(convertView, Constants.TYPE_SECOND_ITEM, activityContext); //Creating ViewHolder and passing the object of type view

            return myProfileViewHolder; //returning the object created
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ProfileRecyclerAdapter.MyProfileViewHolder myProfileViewHolder, int position) {
        if (myProfileViewHolder.holderId == 0) {                              // as the list view is going to be called after the navigation_header view so we decrement the
            final MyProfileViewHolder viewHolderFinal = myProfileViewHolder;

            try {
                if (ProfileImageHolder.mProfilePhotoFile != null && ProfileImageHolder.mProfilePhotoFile.exists()) {
                    // the noPlaceholder() did the trick... Now i am not receiving any null pointer exception regarding the gedWidth().
                    // the order has to be centerCrop().fit(). centerCrop() crops the image to fit its width/height and fit() fits the image into the imageView
                    picasso.load(ProfileImageHolder.mProfilePhotoFile).centerCrop().fit().noPlaceholder().into(myProfileViewHolder.profilePicture);

                } else {
                    ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ParseUser.getCurrentUser().getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                picasso.load("file:" + parseUser.getString(ParseConstants.PROFILE_PICTURE_PATH)).centerCrop().fit().noPlaceholder().
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
        private Button mEditProfileButton;

        // Second list item
        public TextView friends;
        public TextView followers;
        public TextView in_common;
        public TextView videos;
        public TextView photos;
        private RecyclerView mRecyclerView;
        private GridLayoutManager mGridLayoutManager;
        private Context actContext;
        //private adapter

        public MyProfileViewHolder(View itemView, int ViewType, Context actContext) {
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
                mEditProfileButton = (Button) itemView.findViewById(R.id.edit_profile);
                setButtonClickListeners();

            } else if (ViewType == Constants.TYPE_SECOND_ITEM) {
                holderId = Constants.TYPE_SECOND_ITEM;
                friends = (TextView) itemView.findViewById(R.id.friends);
                followers = (TextView) itemView.findViewById(R.id.followers);
                in_common = (TextView) itemView.findViewById(R.id.in_common);
                videos = (TextView) itemView.findViewById(R.id.videos);
                photos = (TextView) itemView.findViewById(R.id.photos);
                this.actContext = actContext;
                //setUpRecyclerView(itemView);
            }
        }

        private void setButtonClickListeners(){
            mEditProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Snackbar.make(v.getRootView(), "Here's a Snackbar", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        private void setUpRecyclerView(View view){
            mRecyclerView = (RecyclerView) view.findViewById(R.id.gallery);
            mRecyclerView.setHasFixedSize(false);
            ProfileGalleryAdapter mProfileGalleryAdapter = new ProfileGalleryAdapter(actContext, new ArrayList<ParseObject>());

            mRecyclerView.setAdapter(mProfileGalleryAdapter);
            mGridLayoutManager = new GridLayoutManager(actContext, 3);
            mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (3 - position % 3);
                }
            });
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            /** OnItemTouchListener for the RecyclerView */
            mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(actContext, mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }
    }
}
