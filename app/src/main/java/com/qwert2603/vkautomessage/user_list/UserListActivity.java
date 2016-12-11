package com.qwert2603.vkautomessage.user_list;

import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;

public class UserListActivity extends BaseActivity {

    @Override
    protected NavigationFragment createFragment() {
        return UserListFragment.newInstance();
    }
}