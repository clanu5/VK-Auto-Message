package com.qwert2603.vkautomessage.record_details;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.util.LogUtils;

public class RecordActivity extends BaseActivity {

    public static final int NO_DRAWING_START = -1;

    public static final String EXTRA_DRAWING_START_X = "com.qwert2603.vkautomessage.record_details.EXTRA_DRAWING_START_X";
    public static final String EXTRA_DRAWING_START_Y = "com.qwert2603.vkautomessage.record_details.EXTRA_DRAWING_START_Y";

    @Override
    protected NavigationFragment createFragment() {
        return createRecordFragmentForIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Fragment fragment = createRecordFragmentForIntent(intent);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();
    }

    private RecordFragment createRecordFragmentForIntent(Intent intent) {
        int startX = intent.getIntExtra(EXTRA_DRAWING_START_X, NO_DRAWING_START);
        int startY = intent.getIntExtra(EXTRA_DRAWING_START_Y, NO_DRAWING_START);
        LogUtils.d("createRecordFragmentForIntent " + intent.hasExtra(EXTRA_ITEM));
        if (intent.getParcelableExtra(EXTRA_ITEM) != null) {
            Record record = intent.getParcelableExtra(EXTRA_ITEM);
            return RecordFragment.newInstance(record, startX, startY);
        } else {
            int recordId = intent.getIntExtra(EXTRA_ITEM_ID, -1);
            return RecordFragment.newInstance(recordId, startX, startY);
        }
    }
}