package com.qwert2603.vkautomessage.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;
import com.qwert2603.vkautomessage.util.AndroidUtils;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "com.qwert2603.vkautomessage.EXTRA_ITEM_ID";
    public static final String EXTRA_ITEM = "com.qwert2603.vkautomessage.EXTRA_ITEM";

    protected abstract NavigationFragment createFragment();

    private NavigationFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        mFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = createFragment();
            AndroidUtils.runOnUI(
                    () -> getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, mFragment)
                            .commit(), 2);
        }
    }

    public void performOnBackPressed() {
        AndroidUtils.runOnUI(
                () -> {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(mFragment)
                            .commit();
                    super.onBackPressed();
                }, 2);



    }

    @Override
    public void onBackPressed() {
        mFragment.onBackPressed();
    }
}
