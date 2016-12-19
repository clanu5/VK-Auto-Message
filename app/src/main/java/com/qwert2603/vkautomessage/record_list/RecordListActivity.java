package com.qwert2603.vkautomessage.record_list;

import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;

public class RecordListActivity extends BaseActivity {

    @Override
    protected NavigationFragment createFragment() {
        int userId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        return RecordListFragment.newInstance(userId);
    }

}
