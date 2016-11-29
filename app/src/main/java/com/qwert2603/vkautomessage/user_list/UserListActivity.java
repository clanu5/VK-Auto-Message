package com.qwert2603.vkautomessage.user_list;

import android.support.v4.app.Fragment;

import com.qwert2603.vkautomessage.navigation.NavigationActivity;

public class UserListActivity extends NavigationActivity {

    @Override
    protected boolean isNavigationButtonVisible() {
        return true;
    }

    @Override
    protected Fragment createFragment() {
        return UserListFragment.newInstance();
    }
}