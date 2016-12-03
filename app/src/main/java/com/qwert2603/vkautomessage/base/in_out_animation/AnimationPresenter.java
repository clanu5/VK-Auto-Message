package com.qwert2603.vkautomessage.base.in_out_animation;

import com.qwert2603.vkautomessage.base.BasePresenter;

/**
 * Презентер для view с поддержкой анимации появления и исчезновения элементов UI.
 * <p>
 * После окончания out-анимации AnimationView должно вызвать {@link #onAnimateOutFinished(int)} с тем же самым параметром,
 * который был передан в {@link AnimationView#animateOut(int)}.
 * <p>
 * AnimationView должно вызывать {@link #onReadyToAnimate()}, когда будет готово показать анимацию.
 * AnimationView должно вызывать {@link #onAnimateInFinished()} после окончания in-анимации.
 * AnimationView должно вызывать {@link #onAnimateEnterFinished()} после окончания анимации входа в экран.
 * AnimationView должно вызывать {@link #onAnimateExitFinished()} после окончания анимации выхода из экрана.
 *
 * @param <M> тип модели, с которой работает презентер.
 * @param <V> тип представления, которым управляет презентер.
 */
public abstract class AnimationPresenter<M, V extends AnimationView> extends BasePresenter<M, V> {

    protected static final int ON_BACK_PRESSED_ANIMATE_OUT_ID = -1;

    private enum InOutState {
        FIRST_TIME,
        ENTERING,
        OUTSIDE,
        INNING,
        INSIDE,
        OUTING,
        EXITING
    }

    private InOutState mInOutState = InOutState.FIRST_TIME;

    protected abstract boolean isFirstAnimateInWithLargeDelay();

    @Override
    public void unbindView() {
        super.unbindView();
        mInOutState = InOutState.FIRST_TIME;
    }

    @Override
    protected boolean canUpdateView() {
        return super.canUpdateView() && isInningOrInside();
    }

    public void onReadyToAnimate() {
        if (mInOutState == InOutState.FIRST_TIME) {
            mInOutState = InOutState.ENTERING;
            getView().animateEnter();
        }
        if (mInOutState == InOutState.OUTSIDE) {
            mInOutState = InOutState.INNING;
            getView().animateIn(false);
            /*
              TODO: 27.11.2016 сделать что-нибудь с SceneTransitionAnimation при возвращении к активити в другой ориентации
              in анимация тоже.
             */
        }
    }

    public void onBackPressed() {
        animateOut(ON_BACK_PRESSED_ANIMATE_OUT_ID);
    }

    public void onAnimateEnterFinished() {
        mInOutState = InOutState.INNING;
        getView().animateIn(isFirstAnimateInWithLargeDelay());
        updateView();
    }

    public void onAnimateExitFinished() {
        getView().performBackPressed();
    }

    public void onAnimateInFinished() {
        mInOutState = InOutState.INSIDE;
    }

    public void onAnimateOutFinished(int id) {
        if (id != ON_BACK_PRESSED_ANIMATE_OUT_ID) {
            mInOutState = InOutState.OUTSIDE;
        } else {
            mInOutState = InOutState.EXITING;
            getView().animateExit();
        }
    }

    /**
     * Запустить out анимацию для последующего перехода к элементу с переданному id
     *
     * @param id id элемента, к которому будем выполнен переход после завершения анимации.
     */
    protected void animateOut(int id) {
        mInOutState = InOutState.OUTING;
        getView().animateOut(id);
    }

    protected boolean isInningOrInside() {
        return mInOutState == InOutState.INNING || mInOutState == InOutState.INSIDE;
    }
}
