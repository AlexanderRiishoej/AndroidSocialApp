package com.mycompany.loginapp.profile;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.ProfileRecyclerAdapter;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageUpdateCoverPhoto;
import com.mycompany.loginapp.eventMessages.MessageUpdateProfilePicture;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import de.greenrobot.event.EventBus;

import java.util.List;

/**
 * Fragment that handles the profile of the current user.
 * Implements AppBarLayout.OnOffsetChangedListener in order to avoid unintentional invokation of SwipeRefreshing while scrolling:.
 * https://gist.github.com/blackcj/001a90c7775765ad5212
 */
public class PrivateProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private ProfileRecyclerAdapter profileRecyclerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFabButton;
    private AppBarLayout mAppBarLayout;
    private ImageView mParallaxImageView;

    public PrivateProfileFragment() {
        // Required empty public constructor
    }

    public static PrivateProfileFragment newInstance() {
        return new PrivateProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        //setHasOptionsMenu(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("FragmentView");

    }

    // http://stackoverflow.com/questions/27074717/android-fullscreen-dialog-confirmation-and-dismissive-actions
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_private_profile, container, false);
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Profile view");
        //collapsingToolbar.setStatusBarScrimColor(R.drawable.md_transparent);
        mParallaxImageView = (ImageView) view.findViewById(R.id.header);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_teal);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        mFabButton = (FloatingActionButton) view.findViewById(R.id.fab);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        this.initializeSwipeRefreshLayout(view);
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        /** The RecyclerView for this NavigationDrawer */
        mRecyclerView = (RecyclerView) view.findViewById(R.id.edit_profile_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        profileRecyclerAdapter = new ProfileRecyclerAdapter(getActivity());

        mRecyclerView.setAdapter(profileRecyclerAdapter);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        /** OnItemTouchListener for the RecyclerView */
        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        this.loadCoverPhoto();
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

    public void updateProfilePicture(){
        profileRecyclerAdapter.updateRecyclerItem(0);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /** Event received when a new profile picture has been chosen */
    public void onEvent(MessageUpdateCoverPhoto newCoverPhotoEvent){
        this.loadCoverPhoto();
    }

    private void loadCoverPhoto(){
        if(ProfileImageHolder.profileCoverPhotoFile != null && ProfileImageHolder.profileCoverPhotoFile.exists()){
            MySingleton.getMySingleton().getPicasso().load(ProfileImageHolder.profileCoverPhotoFile).centerCrop().fit().noPlaceholder().
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
}
