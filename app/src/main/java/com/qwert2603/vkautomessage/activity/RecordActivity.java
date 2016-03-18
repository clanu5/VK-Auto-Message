package com.qwert2603.vkautomessage.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.fragment.RecordFragment;

public class RecordActivity extends NavigationActivity {

    public static final String EXTRA_RECORD_ID = "com.qwert2603.vkautomessage.EXTRA_RECORD_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_record);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            int recordId = getIntent().getIntExtra(EXTRA_RECORD_ID, -1);
            fragment = RecordFragment.newInstance(recordId);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}