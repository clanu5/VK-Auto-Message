package com.qwert2603.vkautomessage.base;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;

public abstract class BaseDialog extends DialogFragment implements BaseView {

    protected abstract BasePresenter getPresenter();

    @SuppressWarnings("unchecked")
    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
