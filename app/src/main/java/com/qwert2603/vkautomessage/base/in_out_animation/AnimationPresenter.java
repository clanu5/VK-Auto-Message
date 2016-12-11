package com.qwert2603.vkautomessage.base.in_out_animation;

import com.qwert2603.vkautomessage.base.navigation.NavigationPresenter;

/**
 * Презентер для view с поддержкой анимации появления и исчезновения элементов UI.
 * <p>
 * AnimationView должно вызывать {@link #onReadyToAnimate()}, когда будет готово показать анимацию.
 * AnimationView должно вызывать {@link #onAnimateInFinished()} после окончания in-анимации.
 * AnimationView должно вызывать {@link #onAnimateOutFinished()} после окончания out-анимации.
 * AnimationView должно вызывать {@link #onAnimateEnterFinished()} после окончания анимации входа в экран.
 * AnimationView должно вызывать {@link #onAnimateExitFinished()} после окончания анимации выхода из экрана.
 *
 * @param <M> тип модели, с которой работает презентер.
 * @param <V> тип представления, которым управляет презентер.
 */
public abstract class AnimationPresenter<M, V extends AnimationView> extends NavigationPresenter<M, V> {

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

    private boolean mAnimateExitAfterOut = false;

    @Override
    public void unbindView() {
        super.unbindView();
        mInOutState = InOutState.FIRST_TIME;
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
              https://developer.android.com/training/material/animations.html?hl=ru
             */
        }
    }

    @Override
    public void onBackPressed() {
        mAnimateExitAfterOut = true;
        animateOut();
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

    public void onAnimateOutFinished() {
        if (!mAnimateExitAfterOut) {
            mInOutState = InOutState.OUTSIDE;
        } else {
            mInOutState = InOutState.EXITING;
            getView().animateExit();
            mAnimateExitAfterOut = false;
        }
    }

    protected void animateOut() {
        mInOutState = InOutState.OUTING;
        getView().animateOut();
    }

    protected final boolean isInningOrInside() {
        return mInOutState == InOutState.INNING || mInOutState == InOutState.INSIDE;
    }

    protected final boolean isOutside() {
        return mInOutState == InOutState.OUTSIDE;
    }
}
