package com.qwert2603.vkautomessage.user_list;

import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;

import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;

public class UserListActivity extends BaseActivity {

    @Override
    protected NavigationFragment createFragment() {
        return UserListFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(400);
//        slide.excludeTarget(android.R.id.statusBarBackground, true);

        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);

        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(false);
    }
}