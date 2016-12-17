package com.qwert2603.vkautomessage.record_list;

import android.animation.Animator;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.transition.Slide;
import android.transition.TransitionValues;
import android.view.Gravity;
import android.view.ViewGroup;

import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;
import com.qwert2603.vkautomessage.util.LogUtils;

public class RecordListActivity extends BaseActivity {

    public static final String EXTRA_DRAWING_START_Y = "com.qwert2603.vkautomessage.record_list.EXTRA_DRAWING_START_Y";

    @Override
    protected NavigationFragment createFragment() {
        int userId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        int drawingStartY = getIntent().getIntExtra(EXTRA_DRAWING_START_Y, 0);
        return RecordListFragment.newInstance(userId, drawingStartY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Slide slide = new Slide(Gravity.LEFT) {
            @Override
            public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
                LogUtils.d("createAnimator ");
                LogUtils.d("createAnimator " + startValues.view);
                LogUtils.d("createAnimator " + endValues.view);
                if (startValues.view instanceof AppBarLayout || endValues.view instanceof AppBarLayout
                        || startValues.view instanceof FloatingActionButton || endValues.view instanceof FloatingActionButton) {
                    LogUtils.d("createAnimator NULL. YEAH!!!!!");
                    return null;
                }
                return super.createAnimator(sceneRoot, startValues, endValues);
            }
        };
        slide.setDuration(400);
//        slide.excludeTarget(android.R.id.statusBarBackground, true);

        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(false);
    }

}
