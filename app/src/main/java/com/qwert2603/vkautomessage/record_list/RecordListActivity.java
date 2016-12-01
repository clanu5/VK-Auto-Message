package com.qwert2603.vkautomessage.record_list;

import android.support.v4.app.Fragment;

import com.qwert2603.vkautomessage.navigation.NavigationActivity;

public class RecordListActivity extends NavigationActivity {

    @Override
    protected boolean isNavigationButtonVisible() {
        return false;
    }

    @Override
    protected Fragment createFragment() {
        int userId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        return RecordListFragment.newInstance(userId);
    }

}
