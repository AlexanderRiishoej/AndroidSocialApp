package com.mycompany.loginapp.chat.oldNotUsed;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/*
* This class is a ScrollListener for RecyclerView that allows to show/hide
* views when list is scrolled. It assumes that you have added a header
* to your list. @see pl.michalz.hideonscrollexample.adapter.partone.RecyclerAdapter
* */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 20; //20

    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;

    //Its parameters - dx, dy are the amounts of horizontal and vertical scrolls
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

//        if (firstVisibleItem == 0) {
//            if(!mControlsVisible) {
//                onShow();
//                mControlsVisible = true;
//            }
//
//
//            // Now if this total scroll amount exceeds some threshold (that you can adjust -
//            // the bigger it is, the more you have to scroll to show/hide views) we are
//            // showing/hiding views depending on the direction (dy>0 means that we are scrolling down, dy<0 means that we are scrolling up)
//        } else {
            if (mScrolledDistance > HIDE_THRESHOLD && mControlsVisible) {
                onHide();
                mControlsVisible = false;
                mScrolledDistance = 0;
            } else if (mScrolledDistance < -HIDE_THRESHOLD && !mControlsVisible) {
                onShow();
                mControlsVisible = true;
                mScrolledDistance = 0;
            }
        //}
        // We are calculating total scroll amount (sum of deltas) but only if views are hidden and
        // we are scrolling up or if views are visible and we are scrolling down because these are the cases that we care about
        if((mControlsVisible && dy>0) || (!mControlsVisible && dy<0)) {
            mScrolledDistance += dy;
        }
    }

    // We are not actually showing/hiding views in our scroll listener class, instead we make it
    // abstract and call show()/hide() methods, so the caller can implement them
    public abstract void onHide();
    public abstract void onShow();
}
