package com.qwert2603.vkautomessage.presenter;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.view.BaseView;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<M, V extends BaseView> {
    private M mModel;
    private WeakReference<V> mView;

    protected void setModel(M model) {
        mModel = model;
        updateView();
    }

    protected M getModel() {
        return mModel;
    }

    protected V getView() {
        return mView == null ? null : mView.get();
    }

    public void bindView(V view) {
        mView = new WeakReference<>(view);
        updateView();
    }

    public void unbindView() {
        mView = null;
    }

    protected void updateView() {
        if (mView != null && mView.get() != null) {
            onUpdateView(mView.get());
        }
    }

    protected abstract void onUpdateView(@NonNull V view);
}
