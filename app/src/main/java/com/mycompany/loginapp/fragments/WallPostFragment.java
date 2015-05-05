package com.mycompany.loginapp.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.WallPostRecyclerAdapter;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WallPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WallPostFragment extends Fragment {
    /** Recycler */
    private ImageView imageView;
    private Toolbar cardToolbar;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /** Recycler */
    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private WallPostRecyclerAdapter mRecyclerWallPostAdapter;                        // Declaring Adapter For Recycler View
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSeach.
     */
    // TODO: Rename and change types and number of parameters
    public static WallPostFragment newInstance(String param1, String param2) {
        WallPostFragment fragment = new WallPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WallPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall_post, container, false);
//         imageView = (ImageView)view.findViewById(R.id.news_profile_picture);
//        cardToolbar = (Toolbar)view.findViewById(R.id.card_toolbar);
//        cardToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                return false;
//            }
//        });
//        cardToolbar.inflateMenu(R.menu.menu_main);
        //loadProfilePicture();

        mRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_recycler_wall_post);
        mRecyclerWallPostAdapter = new WallPostRecyclerAdapter(getActivity());
        //loadProfilePicture();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerWallPostAdapter);
        //recyclerOnTouchListener();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_wall_post_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void recyclerOnTouchListener(){
        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                    new MaterialDialog.Builder(getActivity())
                            .positiveText("OK")
                            .negativeText("Cancel")
                            .title("Input")
                            .content("Content")
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                            .widgetColorRes(R.color.amber_500)
                            .iconRes(R.drawable.icon)
                            .positiveColorRes(R.color.teal_500)
                            .btnSelector(R.drawable.ripple_button_flat)
                            .input("Phone number", "Test", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    Log.d("", input.toString());
                                    String phoneNumber = input.toString();
                                    final ParseUser currentUser = ParseUser.getCurrentUser();
                                    currentUser.put("phoneNumber", phoneNumber);
                                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.alert_wait));
                                    currentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            progressDialog.dismiss();
                                            if (e == null) {
                                                //profileRecyclerAdapter.notifyItemChanged(1);
                                            } else {
                                                Utilities.showDialog(getActivity(), "Error saving information... " + e.getMessage());
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }).show();
                }


            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    /** Refreshes the content  */
    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerWallPostAdapter.notifyItemsHasChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    /**
     * Gets the ParseUser equal to the Username of the logged in User.
     * Checks whether the stored path is null or if the path to the file exists.
     * If CurrentMediaFilePath is null it means that the profile picture path field in Parse is null, load the image stored in Parse instead
     * If the file exists, load it from the file, else load the image stored in Parse instead.
     */
    private void loadProfilePicture() {
        /** https://parse.com/docs/android_guide#queries-basic */
        ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ParseUser.getCurrentUser().getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    String CurrentMediaFilePath = parseUser.getString(ParseConstants.PROFILE_PICTURE_PATH);
                    /** Check if the stored path in Parse is empty */
                    if (CurrentMediaFilePath != null) {
                        File tempImageFile = new File(CurrentMediaFilePath);

                        /** Check if path exists since the user might have deleted this picture */
                        if (tempImageFile.exists()) {
                            //profileRecyclerAdapter.updateProfileImage(CurrentMediaFilePath);
                            Picasso.with(getActivity()).load("file:" + CurrentMediaFilePath).fit().noPlaceholder().into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("FragmentSearch", "Success");
                                }

                                @Override
                                public void onError() {
                                    Log.d("FragmentSearch", "Error");
                                }
                            });
                        } else {
                            //getProfilePictureFromParse();
                        }

                    } else {
                        //getProfilePictureFromParse();
                    }

                } else {
                    Utilities.showDialog(getActivity(), "Error getting parseUser object" + " " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
