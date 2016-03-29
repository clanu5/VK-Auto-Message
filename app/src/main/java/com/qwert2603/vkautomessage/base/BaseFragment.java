package com.qwert2603.vkautomessage.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;

public abstract class BaseFragment extends Fragment implements BaseView {

    protected abstract BasePresenter getPresenter();

    @SuppressWarnings("unchecked")
    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getPresenter().bindView(this);
    }

    @CallSuper
    @Override
    public void onDestroy() {
        getPresenter().unbindView();
        super.onDestroy();
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        getPresenter().onViewReady();
    }

    @CallSuper
    @Override
    public void onPause() {
        getPresenter().onViewNotReady();
        super.onPause();
    }
}
