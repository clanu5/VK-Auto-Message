package com.qwert2603.vkautomessage.base.navigation;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public interface ActivityInterface {
    Toolbar getToolbar();
    void startToolbarActionMode(View view);
    void restoreToolbarActionMode(View view);
    void stopToolbarActionMode(View view);
    TextView getToolbarTitle();
    ImageView getToolbarIcon();
    void setToolbarTitle(String title);
    void setActivityActionsListener(ActivityActionsListener activityActionsListener);
    void performOnBackPressed();
}
