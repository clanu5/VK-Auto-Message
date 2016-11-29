package com.qwert2603.vkautomessage.record_list;

import android.support.v4.app.Fragment;

import com.qwert2603.vkautomessage.navigation.NavigationActivity;

public class RecordListActivity extends NavigationActivity {

    public static final String EXTRA_USER_ID = "com.qwert2603.vkautomessage.EXTRA_USER_ID";

    @Override
    protected boolean isNavigationButtonVisible() {
        return false;
    }

    @Override
    protected Fragment createFragment() {
        int userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        return RecordListFragment.newInstance(userId);
    }

}
