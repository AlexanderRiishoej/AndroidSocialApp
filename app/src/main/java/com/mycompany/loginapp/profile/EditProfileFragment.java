package com.mycompany.loginapp.profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.EditProfileRecyclerAdapter;
import com.mycompany.loginapp.itemDecorators.FillWidthDividerItemDecoration;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private LinearLayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private EditProfileRecyclerAdapter mEditProfileRecyclerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();

        return fragment;
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

//        this.initializeSwipeRefreshLayout(view);
//        this.initializeRecyclerView(view);

        // Inflate the layout for this fragment
        return view;
    }

    private void initializeSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
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
        //mRecyclerView = (RecyclerView) view.findViewById(R.id.edit_profile_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new FillWidthDividerItemDecoration(getActivity()));
        mEditProfileRecyclerAdapter = new EditProfileRecyclerAdapter(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setAdapter(mEditProfileRecyclerAdapter);

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
    }
}
