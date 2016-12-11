package com.qwert2603.vkautomessage.record_details;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;

public class RecordActivity extends BaseActivity {

    public static final int NO_DRAWING_START = -1;

    public static final String EXTRA_DRAWING_START_X = "com.qwert2603.vkautomessage.record_details.EXTRA_DRAWING_START_X";
    public static final String EXTRA_DRAWING_START_Y = "com.qwert2603.vkautomessage.record_details.EXTRA_DRAWING_START_Y";

    @Override
    protected NavigationFragment createFragment() {
        int recordId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        int startX = getIntent().getIntExtra(EXTRA_DRAWING_START_X, NO_DRAWING_START);
        int startY = getIntent().getIntExtra(EXTRA_DRAWING_START_Y, NO_DRAWING_START);
        return RecordFragment.newInstance(recordId, startX, startY);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int recordId = intent.getIntExtra(EXTRA_ITEM_ID, -1);
        Fragment fragment = RecordFragment.newInstance(recordId, NO_DRAWING_START, NO_DRAWING_START);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();
    }
}