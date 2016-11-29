package com.qwert2603.vkautomessage.recycler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.util.LogUtils;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.END;
import static android.support.v7.widget.helper.ItemTouchHelper.START;

public class SimpleOnItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mItemTouchHelperAdapter;

    public SimpleOnItemTouchHelperCallback(ItemTouchHelperAdapter itemTouchHelperAdapter) {
        mItemTouchHelperAdapter = itemTouchHelperAdapter;
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
            float width = viewHolder.itemView.getWidth();
            float alpha = 1.0f - (Math.abs(dX) / width);
            //viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(0xffff8000);
            if (dX < 0) {
                c.drawRect(viewHolder.itemView.getRight() + dX, viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom(), paint);
            } else {
                c.drawRect(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(), viewHolder.itemView.getLeft() + dX, viewHolder.itemView.getBottom(), paint);
            }
            Drawable drawable = ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.ic_delete_black_24dp);
            drawable.setBounds(
                    viewHolder.itemView.getLeft(),
                    viewHolder.itemView.getTop(),
                    viewHolder.itemView.getLeft() + drawable.getIntrinsicWidth(),
                    viewHolder.itemView.getTop() + drawable.getIntrinsicHeight()
            );
            drawable.draw(c);
            // TODO: 29.11.2016 рисовать посередине по вертикали и с обеих сторон.
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
