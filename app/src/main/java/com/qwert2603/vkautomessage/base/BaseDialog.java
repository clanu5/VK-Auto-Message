package com.qwert2603.vkautomessage.base;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

public abstract class BaseDialog<P extends BasePresenter> extends DialogFragment implements BaseView {

    //private static final String presenterCodeKey = "presenterCodeKey";

    /**
     * Получить презентер, организующий работу этого диалога.
     *
     * @return презентер для этого диалога.
     */
    @NonNull
    protected abstract P getPresenter();

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

    // FIXME: 24.04.2016 сделать сохранение
    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(presenterCodeKey, savePresenter(getPresenter()));
        super.onSaveInstanceState(outState);
    }*/

    /**
     * Сохраненные презентеры.
     * @see #savePresenter(BasePresenter)
     * @see #loadPresenter(int)
     */
    //private static final HashMap<Integer, BasePresenter> sPresenters = new HashMap<>();

    //private static final Random sRandom = new Random();

    /**
     * Сохранить presenter.
     * @return код сохраненного presenter'a.
     */
    /*private static int savePresenter(BasePresenter presenter) {
        int code;
        do {
            code = sRandom.nextInt();
        } while (sPresenters.containsKey(code));
        sPresenters.put(code, presenter);
        return code;
    }*/

    /**
     * Загрузить сохраненный presenter.
     * После загрузки presenter удаляется ихз созраненных.
     * @param code код сохраненного presenter'a.
     */
    /*private static BasePresenter loadPresenter(int code) {
        BasePresenter presenter = sPresenters.get(code);
        sPresenters.remove(code);
        return presenter;
    }*/

}
