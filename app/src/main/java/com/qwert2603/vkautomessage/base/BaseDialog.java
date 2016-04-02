package com.qwert2603.vkautomessage.base;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Random;

public abstract class BaseDialog<P extends BasePresenter> extends DialogFragment implements BaseView {

    private static final String presenterCodeKey = "presenterCodeKey";

    private P mPresenter;

    @NonNull
    protected abstract P createPresenter();

    @NonNull
    protected final P getPresenter() {
        return mPresenter;
    }

    @SuppressWarnings("unchecked")
    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPresenter = (P) loadPresenter(savedInstanceState.getInt(presenterCodeKey));
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
        outState.putInt(presenterCodeKey, savePresenter(mPresenter));
        super.onSaveInstanceState(outState);
    }

    /**
     * Сохраненные презентеры.
     * @see #savePresenter(BasePresenter)
     * @see #loadPresenter(int)
     */
    private static final HashMap<Integer, BasePresenter> sPresenters = new HashMap<>();

    private static final Random sRandom = new Random();

    /**
     * Сохранить presenter.
     * @return код сохраненного presenter'a.
     */
    private static int savePresenter(BasePresenter presenter) {
        int code;
        do {
            code = sRandom.nextInt();
        } while (sPresenters.containsKey(code));
        sPresenters.put(code, presenter);
        return code;
    }

    /**
     * Загрузить сохраненный presenter.
     * После загрузки presenter удаляется ихз созраненных.
     * @param code код сохраненного presenter'a.
     */
    private static BasePresenter loadPresenter(int code) {
        BasePresenter presenter = sPresenters.get(code);
        sPresenters.remove(code);
        return presenter;
    }

}
