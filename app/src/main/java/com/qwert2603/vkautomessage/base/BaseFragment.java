package com.qwert2603.vkautomessage.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Базовый фрагмент, построенный для работы с шаблоном MVP.
 * Организует взаимодействие с презентером:
 * - привязка/отвязка {@link BasePresenter#bindView(BaseView)}, {@link BasePresenter#unbindView()}.
 * - уведомление о готовности {@link BasePresenter#onViewReady()}, {@link BasePresenter#onViewNotReady()}.
 *
 * @param <P> Тип презентера, организующего работу фрагмента.
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView {

    /**
     * Получить презентер, организующий работу этого фрагмента.
     *
     * @return презентер для этого фрагмента.
     */
    @NonNull
    protected abstract P getPresenter();

    @SuppressWarnings("unchecked")
    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Сохраняем состояние, чтобы не создавать презентер заново.
        // Это позволяет не прерывать загрузку, если презентер что-то загружает.
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().onViewReady();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        getPresenter().onViewNotReady();
        super.onDestroyView();
    }

}