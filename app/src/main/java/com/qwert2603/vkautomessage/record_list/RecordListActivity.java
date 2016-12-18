package com.qwert2603.vkautomessage.record_list;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;
import com.qwert2603.vkautomessage.base.navigation.ToolbarIconState;

public class RecordListActivity extends BaseActivity {

    public static final String EXTRA_PREV_ICON_STATE = "com.qwert2603.vkautomessage.record_list.EXTRA_PREV_ICON_STATE";

    @Override
    protected NavigationFragment createFragment() {
        int userId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        @ToolbarIconState int prevIconState = getIntent().getIntExtra(EXTRA_PREV_ICON_STATE, R.attr.state_back_arrow);
        return RecordListFragment.newInstance(userId, prevIconState);
    }

}
