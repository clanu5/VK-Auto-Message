package com.qwert2603.vkautomessage.base.in_out_animation;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.util.LogUtils;

/**
 * Презентер для view с поддержкой анимации появления и исчезновения элементов UI.
 *
 * После окончания out-анимации InOutAnimationView должно вызвать {@link #onAnimateOutFinished(int)} с тем же самым параметром,
 * который был передан в {@link InOutAnimationView#animateOut(int)}.
 *
 * InOutAnimationView должно вызывать {@link #onReadyToAnimateIn()}, когда будет готово показать in-анимацию.
 * InOutAnimationView должно вызывать {@link #onAnimateInFinished()} после окончания in-анимации.
 *
 * @param <M> тип модели, с которой работает презентер.
 * @param <V> тип представления, которым управляет презентер.
 */
public abstract class InOutAnimationPresenter<M, V extends InOutAnimationView> extends BasePresenter<M, V> {
    private enum InOutState {
        FIRST_TIME,
        OUTSIDE,
        INNING,
        INSIDE,
        OUTING
    }

    private InOutState mInOutState = InOutState.FIRST_TIME;

    protected abstract boolean isFirstAnimateInWithLargeDelay();

    public void onReadyToAnimateIn() {
        LogUtils.d("onReadyToAnimateIn");
        if (mInOutState == InOutState.FIRST_TIME) {
            mInOutState = InOutState.OUTSIDE;
            getView().prepareForIn();
            mInOutState = InOutState.INNING;
            getView().animateIn(isFirstAnimateInWithLargeDelay());
        } else if (mInOutState == InOutState.OUTSIDE) {
            mInOutState = InOutState.INNING;
            getView().animateIn(false);
            /*
              TODO: 27.11.2016 при уничтожении активити SceneTransitionAnimation не работает.
              если получится сделать, чтобы работало, то можно и анимацию In делать.
             */
        }
    }

    public void onAnimateInFinished() {
        mInOutState = InOutState.INSIDE;
    }

    public void onAnimateOutFinished(int id) {
        mInOutState = InOutState.OUTSIDE;
    }

    /**
     * Запустить out анимацию для последующего перехода к элементу с переданному id
     * @param id id элемента, к которому будем выполнен переход после завершения анимации.
     */
    protected void animateOut(int id) {
        mInOutState = InOutState.OUTING;
        getView().animateOut(id);
    }
}
