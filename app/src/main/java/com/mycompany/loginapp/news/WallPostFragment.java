package com.mycompany.loginapp.news;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.WallPostRecyclerAdapter;
import com.mycompany.loginapp.clickListeners.ClickListener;
import com.mycompany.loginapp.clickListeners.RecyclerOnTouchListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WallPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WallPostFragment extends Fragment {
    /** Recycler */
    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private WallPostRecyclerAdapter mRecyclerWallPostAdapter;                        // Declaring Adapter For Recycler View
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public WallPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall_post, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_recycler_wall_post);
        mRecyclerWallPostAdapter = new WallPostRecyclerAdapter(getActivity());
        //loadCoverPhoto();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerWallPostAdapter);
        //recyclerOnTouchListener();
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_wall_post_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.teal_500);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                }


            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void setSwipeRefreshingEnabled(boolean enabled){
        if(mSwipeRefreshLayout == null){
            return;
        }

        if(enabled){
            mSwipeRefreshLayout.setEnabled(true);
        }
        else{
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /** Refreshes the content  */
    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerWallPostAdapter.notifyItemsHasChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}
