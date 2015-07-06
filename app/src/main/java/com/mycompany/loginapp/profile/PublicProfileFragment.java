package com.mycompany.loginapp.profile;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.design.widget.CollapsingToolbarLayout;
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
import com.mycompany.loginapp.singletons.MySingleton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PublicProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublicProfileFragment extends Fragment {

    // TODO: Rename and change types and number of parameters
    public static PublicProfileFragment newInstance() {
        return new PublicProfileFragment();
    }
    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private ProfileRecyclerAdapter profileRecyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton mFabButton;
    private Toolbar mToolbar;

    public PublicProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_profile, container, false);
        // Inflate the layout for this fragment
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Amazing view");
        final ImageView parallaxImageview = (ImageView) view.findViewById(R.id.header);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_teal);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        mFabButton = (FloatingActionButton) view.findViewById(R.id.fab);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        MySingleton.getMySingleton().getPicasso()
                .load("http://thewowstyle.com/wp-content/uploads/2015/01/Blue-Green-beautiful-nature-21891805-1920-1200.jpg")
                .fit().centerCrop()
                .transform(PaletteTransformation.instance())
                .into(parallaxImageview, new Callback.EmptyCallback() {
                    @Override public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) parallaxImageview.getDrawable()).getBitmap(); // Ew!
                        Palette palette = PaletteTransformation.getPalette(bitmap);
                        List<Palette.Swatch> swatches = palette.getSwatches(); // all color swatches
                        Palette.Swatch swatch = palette.getVibrantSwatch();
                        // TODO apply palette to text views, backgrounds, etc.
                        //collapsingToolbar.setContentScrimColor(palette.getLightVibrantSwatch().getRgb());
                        //mFabButton.setBackgroundColor(palette.getDarkMutedSwatch().getRgb());
                    }
                });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.edit_profile_recyclerView);
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        //mRecyclerAdapter = new NavigationRecyclerAdapter(getActivity());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,navigation_header view name, navigation_header view email,
        // and navigation_header view profile_image picture
        profileRecyclerAdapter = new ProfileRecyclerAdapter(getActivity());

        mRecyclerView.setAdapter(profileRecyclerAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(getActivity());         // Creating a layout Manager
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

        initializeSwipeRefreshLayout(view);

        return view;
    }

    private void initializeSwipeRefreshLayout(View view) {
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_public_profile);
//        swipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
    }
}
