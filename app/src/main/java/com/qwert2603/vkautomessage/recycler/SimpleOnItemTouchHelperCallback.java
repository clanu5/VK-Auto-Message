package com.qwert2603.vkautomessage.recycler;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

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
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
            LogUtils.d("SimpleOnItemTouchHelperCallback onChildDraw " + alpha + " " + dX + " " + viewHolder);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
