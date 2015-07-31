package com.mycompany.loginapp.clickListeners;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.mycompany.loginapp.R;

/**
 * Created by Alexander on 12-04-2015.
 */
public class RecyclerFindFriendsOnTouchListener implements RecyclerView.OnItemTouchListener {

    /**
     * GestureDetector that detects motionEvents whether you doubletapped, tapped, swiped, long press or whatever touch happened
     */
    private GestureDetector gestureDetector;
    private ClickListener clickListener;

    public RecyclerFindFriendsOnTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
        this.clickListener = clickListener;

        /** The GestureDetector handles different kind of touches that the user may perform */
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                }

                Log.d("RecyclerOnTouchListener", "onLongPress: " + e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    child.onTouchEvent(e); // called to alert the RecyclerView that a touch has happened and the ripple should be drawn
                    clickListener.onClick(child, recyclerView.getChildLayoutPosition(child));
                }
                Log.d("RecyclerOnTouchListener", "onSingleTapUp: " + e);
                return super.onSingleTapUp(e);
            }
        });
    }

    //this method is invoked whenever an item is clicked in the recyclerView as the first event raised
    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        //When clicking a child inside the recyclerView, it receives an event from the recyclerView click itself AND the childView clicked. Hence, when clicking a button in the recyclerView, 2 events
        //are fired. If I am only interested in the click event from the child view, i have to check if this is indeed the event that is invoked. To do this, im disabling the child view in the adapter order
        //to check if its true or not. Hence, if "isEnable" is false, the event is fired from the child view, where i set the "isEnable" to false.
        //By returning false in the closed brackets, i tell the recyclerView, that it should not forward the event further, hence stopping the recyclerView in consuming the event.
//        if (child != null && (child.getId() == R.id.find_friends_main_item_layout) && !child.isEnabled()) {
//            return false;
//        }
//        if(e.getAction() == MotionEvent.ACTION_DOWN){
//            return false;
//        }
        final int action = MotionEventCompat.getActionMasked(e);
        /** if the child view clicked is not null and the clickListener passed is not null and if our gestureDetector
         * handled the event correctly by returning true then we have to handle the click indicating that the user pressed
         * on the RecyclerView at a given position, then process the click event */
        if (child != null && this.clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, recyclerView.getChildLayoutPosition(child));
        }

        // returns false if the touch event is of the type onSingleTapUp or onLongPress in this cases, and the event is forwarded to the gestureDetector
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
