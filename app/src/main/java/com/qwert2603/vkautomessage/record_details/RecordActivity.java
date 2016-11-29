package com.qwert2603.vkautomessage.record_details;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.navigation.NavigationActivity;

public class RecordActivity extends NavigationActivity {

    public static final String EXTRA_RECORD_ID = "com.qwert2603.vkautomessage.EXTRA_RECORD_ID";

    @Override
    protected boolean isNavigationButtonVisible() {
        return false;
    }

    @Override
    protected Fragment createFragment() {
        int recordId = getIntent().getIntExtra(EXTRA_RECORD_ID, -1);
        return RecordFragment.newInstance(recordId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int recordId = intent.getIntExtra(EXTRA_RECORD_ID, -1);
        Fragment fragment = RecordFragment.newInstance(recordId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();
    }
}