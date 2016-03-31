package com.qwert2603.vkautomessage.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView {

    private P mPresenter;

    protected abstract P createPresenter();

    protected P getPresenter() {
        return mPresenter;
    }

    @SuppressWarnings("unchecked")
    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPresenter = createPresenter();
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

}
