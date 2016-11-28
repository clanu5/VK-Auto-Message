package com.qwert2603.vkautomessage.behavior;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

public class RightFabHideBehavior extends FabHideBehavior {
    public RightFabHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void hideFab(FloatingActionButton child) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        ObjectAnimator.ofFloat(child, "translationX", child.getWidth() + layoutParams.rightMargin).start();
    }

    @Override
    protected void showFab(FloatingActionButton child) {
        ObjectAnimator.ofFloat(child, "translationX", 0).start();
    }
}
