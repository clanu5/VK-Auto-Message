package com.qwert2603.vkautomessage.base;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

public abstract class BaseDialog<P extends BasePresenter> extends DialogFragment implements BaseView {

    private static final String presenterKey = "presenterKey";

    private P mPresenter;

    @NonNull
    protected abstract P createPresenter();

    @NonNull
    protected P getPresenter() {
        return mPresenter;
    }

    @SuppressWarnings("unchecked")
    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPresenter = (P) savedInstanceState.getSerializable(presenterKey);
        }
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        mPresenter.bindView(this);
    }

    @CallSuper
    @Override
    public void onDestroy() {
        mPresenter.unbindView();
        super.onDestroy();
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onViewReady();
    }

    @CallSuper
    @Override
    public void onPause() {
        mPresenter.onViewNotReady();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(presenterKey, mPresenter);
        super.onSaveInstanceState(outState);
    }

}
