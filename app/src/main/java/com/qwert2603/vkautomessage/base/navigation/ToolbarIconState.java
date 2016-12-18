package com.qwert2603.vkautomessage.base.navigation;

import android.support.annotation.IntDef;

import com.qwert2603.vkautomessage.R;

@IntDef({R.attr.state_close, R.attr.state_back_arrow, R.attr.state_burger})
@interface ToolbarIconState {
    int[] STATES = {
            R.attr.state_close,
            R.attr.state_back_arrow,
            R.attr.state_burger
    };
}
