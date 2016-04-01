package com.qwert2603.vkautomessage.base;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<M, V extends BaseView> {
    private M mModel;
    private WeakReference<V> mView;
    private boolean mIsViewReady = false;

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
    }

    public void unbindView() {
        mIsViewReady = false;
        mView = null;
    }

    public void onViewReady() {
        mIsViewReady = true;
        updateView();
    }

    public void onViewNotReady() {
        mIsViewReady = false;
    }

    protected void updateView() {
        if (mView != null && mView.get() != null && mIsViewReady) {
            onUpdateView(mView.get());
        }
    }

    protected abstract void onUpdateView(@NonNull V view);
}
