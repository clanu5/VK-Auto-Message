package com.qwert2603.vkautomessage.navigation;

import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

public interface ToolbarHolder {
    Toolbar getToolbar();
    TextView getToolbarTitle();
    ImageView getToolbarIcon();
    void setToolbarTitle(String title);
}
