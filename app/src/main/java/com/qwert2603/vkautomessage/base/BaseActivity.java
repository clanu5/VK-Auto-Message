package com.qwert2603.vkautomessage.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "com.qwert2603.vkautomessage.EXTRA_ITEM_ID";

    protected abstract NavigationFragment createFragment();

    private NavigationFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        mFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = createFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mFragment)
                    .commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mFragment.onMenuPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void performOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        mFragment.onBackPressed();
    }
}
