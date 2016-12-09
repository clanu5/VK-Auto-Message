package com.qwert2603.vkautomessage.recycler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.qwert2603.vkautomessage.util.LogUtils;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.END;
import static android.support.v7.widget.helper.ItemTouchHelper.START;

public class SimpleOnItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mItemTouchHelperAdapter;

    private Paint mPaint;

    private Drawable mDeleteDrawable;

    public SimpleOnItemTouchHelperCallback(ItemTouchHelperAdapter itemTouchHelperAdapter, @ColorInt int backColor, Drawable deleteDrawable) {
        mItemTouchHelperAdapter = itemTouchHelperAdapter;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(backColor);
        mDeleteDrawable = deleteDrawable;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, START | END);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        LogUtils.d("SimpleOnItemTouchHelperCallback onSwiped " + viewHolder);
        mItemTouchHelperAdapter.onItemDismiss(viewHolder);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) {
            viewHolder.itemView.setTranslationX(dX);

            int border = (viewHolder.itemView.getHeight() - mDeleteDrawable.getIntrinsicHeight()) / 2;
            if (dX < 0) {
                c.drawRect(viewHolder.itemView.getRight() + dX, viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom(), mPaint);
                mDeleteDrawable.setBounds(
                        viewHolder.itemView.getLeft() + viewHolder.itemView.getWidth() - border - mDeleteDrawable.getIntrinsicWidth(),
                        viewHolder.itemView.getTop() + border,
                        viewHolder.itemView.getLeft() + viewHolder.itemView.getWidth() - border,
                        viewHolder.itemView.getTop() + border + mDeleteDrawable.getIntrinsicHeight()
                );
            } else {
                c.drawRect(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(), viewHolder.itemView.getLeft() + dX, viewHolder.itemView.getBottom(), mPaint);
                mDeleteDrawable.setBounds(
                        viewHolder.itemView.getLeft() + border,
                        viewHolder.itemView.getTop() + border,
                        viewHolder.itemView.getLeft() + border + mDeleteDrawable.getIntrinsicWidth(),
                        viewHolder.itemView.getTop() + border + mDeleteDrawable.getIntrinsicHeight()
                );
            }
            mDeleteDrawable.draw(c);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @SuppressWarnings("unused")
    public Drawable getDeleteDrawable() {
        return mDeleteDrawable;
    }

    @SuppressWarnings("unused")
    public void setDeleteDrawable(Drawable deleteDrawable) {
        mDeleteDrawable = deleteDrawable;
    }

    public void setBackColor(@ColorInt int backColor) {
        mPaint.setColor(backColor);
    }

    @ColorInt
    public int getColor() {
        return mPaint.getColor();
    }
}
