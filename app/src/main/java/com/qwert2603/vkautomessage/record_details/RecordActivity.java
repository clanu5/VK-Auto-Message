package com.qwert2603.vkautomessage.record_details;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.util.LogUtils;

public class RecordActivity extends BaseActivity {

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
        LogUtils.d("createRecordFragmentForIntent " + intent.hasExtra(EXTRA_ITEM));
        if (intent.getParcelableExtra(EXTRA_ITEM) != null) {
            Record record = intent.getParcelableExtra(EXTRA_ITEM);
            return RecordFragment.newInstance(record);
        } else {
            int recordId = intent.getIntExtra(EXTRA_ITEM_ID, -1);
            return RecordFragment.newInstance(recordId);
        }
    }
}