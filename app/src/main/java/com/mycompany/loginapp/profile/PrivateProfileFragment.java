package com.mycompany.loginapp.profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.mycompany.loginapp.eventMessages.MessageImageDialog;
import com.mycompany.loginapp.eventMessages.MessageUpdateCoverPhoto;
import com.mycompany.loginapp.eventMessages.MessageUpdateProfilePicture;
import com.mycompany.loginapp.profile.editProfile.EditProfile_act;
import com.mycompany.loginapp.profile.editProfile.MessageUpdateName;
import com.mycompany.loginapp.profile.editProfile.MessageUpdateUsername;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Fragment that handles the profile_image of the current user.
 * Implements AppBarLayout.OnOffsetChangedListener in order to avoid unintentional invocation of SwipeRefreshing while scrolling:.
 * https://gist.github.com/blackcj/001a90c7775765ad5212
 */
public class PrivateProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, View.OnTouchListener {

    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.gallery) RecyclerView mHorizontalGalleryRecyclerView;                           // Declaring RecyclerView
    private ProfileGalleryAdapter mProfileGalleryAdapter;
    @Bind(R.id.fragment_toolbar_teal) Toolbar mToolbar;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.appbar) AppBarLayout mAppBarLayout;
    @Bind(R.id.header) ImageView mParallaxImageView;
    @Bind(R.id.profile_picture) ImageView mProfilePictureImageView;
    private TextView mFriends, mFollowers, mInCommon, mVideos, mPhotos;
    //Header
    @Bind(R.id.edit_profile) Button mEditProfileButton;
    @Bind(R.id.city)TextView mCity;
    @Bind(R.id.wall_post_username)TextView mNameTextView;
    @Bind(R.id.status)TextView mOnlineStatus;
    @Bind(R.id.birth_date)TextView mBirthday;


    public PrivateProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    // http://stackoverflow.com/questions/27074717/android-fullscreen-dialog-confirmation-and-dismissive-actions
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_private_profile, container, false);
        //Bind this fragment to the view so ButterKnife can be used in this fragment
        ButterKnife.bind(this, view);
        //mCollapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        //collapsingToolbar.setTitle(ApplicationMain.mCurrentParseUser.getUsername());
        mCollapsingToolbarLayout.setTitle(ApplicationMain.mCurrentParseUser.getUsername());
        //collapsingToolbar.setStatusBarScrimColor(R.drawable.md_transparent);

//        mParallaxImageView = (ImageView) view.findViewById(R.id.header);
//        mProfilePictureImageView = (ImageView) view.findViewById(R.id.profile_picture);
        //mToolbar = (Toolbar) view.findViewById(R.id.fragment_toolbar_teal);
        //mToolbar.setTitle(ApplicationMain.mCurrentParseUser.getUsername());
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        this.setUpHeaderLayout();
        this.setUpSecondItemLayout(view);

        this.initializeSwipeRefreshLayout();
        //mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        this.initializeRecyclerView();
        //profileRecyclerAdapter = new ProfileRecyclerAdapter(getActivity());

        this.loadCoverPhoto();
        // Inflate the layout for this fragment
        return view;
    }

    private void initializeSwipeRefreshLayout() {
        //mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initializeRecyclerView(){
        /** The RecyclerView for this NavigationDrawer */
        //mHorizontalGalleryRecyclerView = (RecyclerView) view.findViewById(R.id.gallery);
        mHorizontalGalleryRecyclerView.setHasFixedSize(true);
        mProfileGalleryAdapter = new ProfileGalleryAdapter(getActivity(), new ArrayList<ParseObject>());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHorizontalGalleryRecyclerView.setAdapter(mProfileGalleryAdapter);
        mHorizontalGalleryRecyclerView.setHorizontalScrollBarEnabled(true);

        mHorizontalGalleryRecyclerView.setLayoutManager(mLayoutManager);
        /** OnItemTouchListener for the RecyclerView */
        mHorizontalGalleryRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(getActivity(), mHorizontalGalleryRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Create the fragment and show it as a dialog.
                //if there is no images i.e. if its a newly registered user
                if (mProfileGalleryAdapter.mImagePaths.size() > 0) {
                    EventBus.getDefault().postSticky(new MessageImageDialog<File>(mProfileGalleryAdapter.mImagePaths.get(position)));
                    ImageDialogFragment newFragment = ImageDialogFragment.newInstance();
                    newFragment.show(getActivity().getSupportFragmentManager(), "ImageDialog");
                }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void updateProfilePicture(){
        mProfileGalleryAdapter.addAllFiles();
        mProfileGalleryAdapter.notifyDataSetChanged();
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
        mFriends = ButterKnife.findById(fragmentView, R.id.friends);
        mFollowers = ButterKnife.findById(fragmentView, R.id.followers);
        mInCommon = ButterKnife.findById(fragmentView,R.id.in_common);
        mVideos = ButterKnife.findById(fragmentView,R.id.videos);
        mPhotos = ButterKnife.findById(fragmentView,R.id.photos);
        //this.profileHeaderParseQuery(); query for these data
    }

    private void setUpHeaderLayout() {
        mEditProfileButton.setOnTouchListener(this);
        this.profileHeaderParseQuery();
    }

    private void profileHeaderParseQuery(){
        ParseQuery<ParseUser> profileParseQuery = ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ApplicationMain.mCurrentParseUser.getUsername());
        profileParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    mCity.setText(parseUser.getString("hometown"));
                    mNameTextView.setText(parseUser.getString("fullName"));
                    mOnlineStatus.setText("online");
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
        if(ProfileImageHolder.mProfilePhotoFile != null && ProfileImageHolder.mProfilePhotoFile.exists()){
            MySingleton.getMySingleton().getPicasso().load(ProfileImageHolder.mProfilePhotoFile).centerCrop().fit().noPlaceholder().into(mProfilePictureImageView);
        }
        else {
            ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ParseUser.getCurrentUser().getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        MySingleton.getMySingleton().getPicasso().load("file:" + parseUser.getString(ParseConstants.PROFILE_PICTURE_PATH)).centerCrop().fit().noPlaceholder().
                                into(mProfilePictureImageView);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void loadCoverPhoto(){
        if(ProfileImageHolder.mProfileCoverPhotoFile != null && ProfileImageHolder.mProfileCoverPhotoFile.exists()){
            MySingleton.getMySingleton().getPicasso().load(ProfileImageHolder.mProfileCoverPhotoFile).centerCrop().fit().noPlaceholder().
                    transform(PaletteTransformation.instance()).into(mParallaxImageView, new Callback.EmptyCallback(){
//                Bitmap bitmap = ((BitmapDrawable) mParallaxImageView.getDrawable()).getBitmap(); // Ew!
//                Palette palette = PaletteTransformation.getPalette(bitmap);
//                List<Palette.Swatch> swatches = palette.getSwatches(); // all color swatches
//                Palette.Swatch swatch = palette.getVibrantSwatch();
                // TODO apply palette to text views, backgrounds, etc.
                //collapsingToolbar.setContentScrimColor(palette.getLightVibrantSwatch().getRgb());
                //mFabButton.setBackgroundColor(palette.getDarkMutedSwatch().getRgb());
            });
        }
        else {
            ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ParseUser.getCurrentUser().getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        MySingleton.getMySingleton().getPicasso().load("file:" + parseUser.getString(ParseConstants.COVER_PHOTO_PATH)).centerCrop().fit().noPlaceholder().
                                into(mParallaxImageView, new Callback.EmptyCallback(){
//                                    Bitmap bitmap = ((BitmapDrawable) mParallaxImageView.getDrawable()).getBitmap(); // Ew!
//                                    Palette palette = PaletteTransformation.getPalette(bitmap);
//                                    List<Palette.Swatch> swatches = palette.getSwatches(); // all color swatches
//                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    // TODO apply palette to text views, backgrounds, etc.
                                    //collapsingToolbar.setContentScrimColor(palette.getLightVibrantSwatch().getRgb());
                                    //mFabButton.setBackgroundColor(palette.getDarkMutedSwatch().getRgb());
                                });
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /** Gets the name of the current logged in user */
    public TextView getNameTextView(){
        return mNameTextView;
    }

    /** Sets the name of the current logged in user */
    public void setName(String name){
        this.mNameTextView.setText(name);
    }

    /** Event received when a new profile_image picture has been chosen */
    public void onEvent(MessageUpdateCoverPhoto newCoverPhotoEvent){
        this.loadCoverPhoto();
    }

    /** Event received when a new profile_image picture has been chosen */
    public void onEvent(MessageUpdateProfilePicture newProfilePhotoEvent){
        this.loadProfilePhoto();
    }

    /** Event received when a new username has been chosen */
    public void onEvent(MessageUpdateUsername messageUpdateUsername){
        mCollapsingToolbarLayout.setTitle(messageUpdateUsername.message);
    }

    /** Event received when a new name has been chosen */
    public void onEvent(MessageUpdateName messageUpdateName){
        mNameTextView.setText(messageUpdateName.message);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        view.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (view.getId()) {
                case R.id.edit_profile: // Id of the button
                    startActivity(new Intent(getActivity(), EditProfile_act.class));
                    break;
                default:
                    break;
            }
        }

        return false;
    }
}
