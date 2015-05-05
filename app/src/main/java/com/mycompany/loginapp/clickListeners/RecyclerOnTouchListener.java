package com.mycompany.loginapp.clickListeners;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.mycompany.loginapp.clickListeners.ClickListener;

/**
 * Created by Alexander on 12-04-2015.
 */
public class RecyclerOnTouchListener implements RecyclerView.OnItemTouchListener{

    /** GestureDetector that detects motionEvents whether you doubletapped, tapped, swiped, long press or whatever touch happened */
    private GestureDetector gestureDetector;
    private ClickListener clickListener;

    public RecyclerOnTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
        this.clickListener = clickListener;

        /** The GestureDetector handles different kind of touches that the user may perform */
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if(child != null && clickListener != null){
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }

                Log.d("RecyclerOnTouchListener", "onLongPress: " + e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(child != null && clickListener != null){
                    child.onTouchEvent(e); // called to alert the RecyclerView that a touch has happened and the ripple should be drawn
                    clickListener.onClick(child, recyclerView.getChildPosition(child));
                }
                Log.d("RecyclerOnTouchListener", "onSingleTapUp: " + e);
                return super.onSingleTapUp(e);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        /** if the child view clicked is not null and the clickListener passed is not null and if our gestureDetector
         * handled the event correctly by returning true then we have to handle the click indicating that the user pressed
         * on the RecyclerView at a given position, then process the click event */
        if(child != null && this.clickListener != null && gestureDetector.onTouchEvent(e)){
            clickListener.onClick(child, recyclerView.getChildPosition(child));
        }

        // returns false if the touch event is of the type onSingleTapUp or onLongPress in this cases, and the event is forwarded to the gestureDetector
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }
}
