package com.mycompany.loginapp.profile;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.ProfileGalleryAdapter;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageFindFriends;
import com.mycompany.loginapp.eventMessages.MessageImageDialog;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Fragment that handles the profile_image of the current user.
 * Implements AppBarLayout.OnOffsetChangedListener in order to avoid unintentional invokation of SwipeRefreshing while scrolling:.
 * https://gist.github.com/blackcj/001a90c7775765ad5212
 */
public class PublicProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private LinearLayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private ProfileGalleryAdapter mProfileGalleryAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFabButton;
    private AppBarLayout mAppBarLayout;
    private ImageView mParallaxImageView, mProfilePictureImageView;
    private TextView mCity, mName, mOnlineStatus, mBirthday;
    private TextView mFriends, mFollowers, mInCommon, mVideos, mPhotos;
    private Button mAddFriendsButton;
    private ParseUser mPublicParseUser;

    public PublicProfileFragment() {
        // Required empty public constructor
    }

    public static PublicProfileFragment newInstance() {
        return new PublicProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPublicParseUser = EventBus.getDefault().getStickyEvent(MessageFindFriends.class).mParseUserObject;
        //EventBus.getDefault().register(this);
    }

    // http://stackoverflow.com/questions/27074717/android-fullscreen-dialog-confirmation-and-dismissive-actions
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_profile, container, false);
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        //collapsingToolbar.setTitle("Profile view");
        collapsingToolbar.setTitle(mPublicParseUser.getUsername());
        //collapsingToolbar.setStatusBarScrimColor(R.drawable.md_transparent);
        mParallaxImageView = (ImageView) view.findViewById(R.id.header);
        mProfilePictureImageView = (ImageView) view.findViewById(R.id.profile_picture);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_teal);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        this.setUpHeaderLayout(view);
        this.setUpSecondItemLayout(view);

        this.initializeSwipeRefreshLayout(view);
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        this.initializeRecyclerView(view);
        //profileRecyclerAdapter = new ProfileRecyclerAdapter(getActivity());

        this.loadCoverPhoto();
        this.loadProfilePhoto();

        mProfileGalleryAdapter.addAllUrls(mPublicParseUser);
        mProfileGalleryAdapter.notifyDataSetChanged();
        // Inflate the layout for this fragment
        return view;
    }

    private void initializeSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initializeRecyclerView(View view){
        /** The RecyclerView for this NavigationDrawer */
        mRecyclerView = (RecyclerView) view.findViewById(R.id.gallery);
        mRecyclerView.setHasFixedSize(true);
        mProfileGalleryAdapter = new ProfileGalleryAdapter(getActivity(), new ArrayList<ParseObject>());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setAdapter(mProfileGalleryAdapter);
        mRecyclerView.setHorizontalScrollBarEnabled(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
        /** OnItemTouchListener for the RecyclerView */
        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Create the fragment and show it as a dialog.
                //EventBus.getDefault().removeStickyEvent(MessageImageDialogFile.class);
                EventBus.getDefault().postSticky(new MessageImageDialog<String>(mProfileGalleryAdapter.mImageUrls.get(position)));
                ImageDialogFragment newFragment = ImageDialogFragment.newInstance();
                newFragment.show(getActivity().getSupportFragmentManager(), "ImageDialog");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    //https://gist.github.com/blackcj/001a90c7775765ad5212
    //https://developer.android.com/reference/android/support/design/widget/AppBarLayout.OnOffsetChangedListener.html
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    private void setUpSecondItemLayout(View fragmentView) {
        mFriends = (TextView) fragmentView.findViewById(R.id.friends);
        mFollowers = (TextView) fragmentView.findViewById(R.id.followers);
        mInCommon = (TextView) fragmentView.findViewById(R.id.in_common);
        mVideos = (TextView) fragmentView.findViewById(R.id.videos);
        mPhotos = (TextView) fragmentView.findViewById(R.id.photos);
        //this.profileHeaderParseQuery(); query for these data
    }

    private void setUpHeaderLayout(View fragmentView) {
        mCity = (TextView) fragmentView.findViewById(R.id.city);
        mName = (TextView) fragmentView.findViewById(R.id.wall_post_username);
        mOnlineStatus = (TextView) fragmentView.findViewById(R.id.status);
        mBirthday = (TextView) fragmentView.findViewById(R.id.birth_date);
        mAddFriendsButton = (Button) fragmentView.findViewById(R.id.add_to_friends);
        mAddFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title("Add to friends")
                        .content("Add to friends button clicked")
                        .show();
            }
        });
        this.profileHeaderParseQuery();
    }

    private void profileHeaderParseQuery(){
        ParseQuery<ParseUser> profileParseQuery = ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, mPublicParseUser.getUsername());
        profileParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    mCity.setText(parseUser.getString("hometown"));
                    mName.setText(parseUser.getString("fullName"));

                    if (parseUser.getBoolean("online")) {
                        mOnlineStatus.setText("online");
                    } else {
                        mOnlineStatus.setText("offline");
                    }

                    mBirthday.setText(parseUser.getString("birthday"));
                } else {
                    new MaterialDialog.Builder(getActivity())
                            .title("Error")
                            .content("Error loading profile data." + " " + e.getMessage())
                            .show();
                }
            }
        });
    }

    private void loadProfilePhoto() {
        final ParseFile profilePhoto = mPublicParseUser.getParseFile(ParseConstants.PROFILE_PICTURE);
        if(profilePhoto != null){
            MySingleton.getMySingleton().getPicasso().load(profilePhoto.getUrl()).centerCrop().fit().noPlaceholder().into(mProfilePictureImageView);
        }
        else {
            MySingleton.getMySingleton().getPicasso().load(R.drawable.com_facebook_profile_picture_blank_portrait).centerCrop().fit().into(mProfilePictureImageView);
            final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
            mProfilePictureImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    private void loadCoverPhoto(){
        final ParseFile coverPhoto = mPublicParseUser.getParseFile(ParseConstants.COVER_PHOTO);
        if(coverPhoto != null){
            MySingleton.getMySingleton().getPicasso().load(coverPhoto.getUrl()).centerCrop().fit().noPlaceholder().into(mParallaxImageView);
        }
        else {
            MySingleton.getMySingleton().getPicasso().load(R.drawable.com_facebook_profile_picture_blank_portrait).centerCrop().fit().into(mParallaxImageView);
            final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
            mParallaxImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    /** Gets the name of the current logged in user */
    public String getName(){
        return mName.getText().toString();
    }

    /** Sets the name of the current logged in user */
    public void setName(String name){
        this.mName.setText(name);
    }
}
