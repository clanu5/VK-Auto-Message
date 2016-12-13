package com.qwert2603.vkautomessage.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public abstract class FabHideBehavior extends FloatingActionButton.Behavior {

    public FabHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected abstract void hideFab(FloatingActionButton child);

    protected abstract void showFab(FloatingActionButton child);

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);

        if (dy > 0) {
            hideFab(child);
        } else if (dy < 0) {
            showFab(child);
        }
    }
}
