package com.mycompany.loginapp.itemDecorators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mycompany.loginapp.R;

/**
 * Created by Alexander on 01-07-2015.
 */
public class FillWidthDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public FillWidthDividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.edit_profile_list_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if(i != 0) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
