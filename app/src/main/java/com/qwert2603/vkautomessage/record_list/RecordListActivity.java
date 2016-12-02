package com.qwert2603.vkautomessage.record_list;

import android.support.v4.app.Fragment;

import com.qwert2603.vkautomessage.navigation.NavigationActivity;

public class RecordListActivity extends NavigationActivity {

    public static final String EXTRA_DRAWING_START_Y = "com.qwert2603.vkautomessage.record_list.EXTRA_DRAWING_START_Y";

    @Override
    protected boolean isNavigationButtonVisible() {
        return false;
    }

    @Override
    protected Fragment createFragment() {
        int userId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        int drawingStartY = getIntent().getIntExtra(EXTRA_DRAWING_START_Y, 0);
        return RecordListFragment.newInstance(userId, drawingStartY);
    }

}
