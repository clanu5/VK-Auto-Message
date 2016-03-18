package com.qwert2603.vkautomessage.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.fragment.RecordListFragment;

public class RecordsListActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = RecordListFragment.newInstance();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();
        }
    }

}
