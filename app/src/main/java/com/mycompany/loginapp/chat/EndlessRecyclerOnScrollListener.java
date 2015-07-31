package com.mycompany.loginapp.chat;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/*
* This class is a ScrollListener for RecyclerView that allows to show/hide
* views when list is scrolled. It assumes that you have added a header
* to your list.
* http://stackoverflow.com/questions/27044449/put-an-indeterminate-progressbar-as-footer-in-a-recyclerview-grid
* */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = false; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();

        //loading is false and the visible items has to be less than the total amount of items
        if(!loading && totalItemCount == firstVisibleItem + totalItemCount && visibleItemCount < totalItemCount){
            loading = true;
            this.onLoadMore();
        }
    }

    public abstract void onLoadMore();

    public void setLoading(boolean loading){
        this.loading = loading;
    }
}
