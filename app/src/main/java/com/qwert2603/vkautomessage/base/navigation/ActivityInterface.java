package com.qwert2603.vkautomessage.base.navigation;

import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

public interface ActivityInterface {
    Toolbar getToolbar();
    TextView getToolbarTitle();
    ImageView getToolbarIcon();
    void setToolbarTitle(String title);
    void setOnBackPressedListener(OnBackPressedListener onBackPressedListener);
    void performOnBackPressed();
}
