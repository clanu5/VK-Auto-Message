package com.qwert2603.vkautomessage.record_list;

import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;

import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;

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

        Slide slide = new Slide(Gravity.LEFT);
        slide.setDuration(400);
//        slide.excludeTarget(android.R.id.statusBarBackground, true);

        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(false);
    }

}
