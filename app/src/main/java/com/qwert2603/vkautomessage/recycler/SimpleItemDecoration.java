package com.qwert2603.vkautomessage.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qwert2603.vkautomessage.R;

public class SimpleItemDecoration extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;

    public SimpleItemDecoration(Context context) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            mDivider.setBounds(
                    left,
                    child.getBottom() + params.bottomMargin,
                    right,
                    child.getBottom() + params.bottomMargin + mDivider.getIntrinsicHeight()
            );
            mDivider.draw(c);
        }
    }
}
