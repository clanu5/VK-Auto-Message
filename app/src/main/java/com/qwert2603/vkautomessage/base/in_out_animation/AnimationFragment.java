package com.qwert2603.vkautomessage.base.in_out_animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;

import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;

/**
 * Фрагмент для отображения списка и показа in/out-анимаций.
 */
public abstract class AnimationFragment<P extends AnimationPresenter> extends NavigationFragment<P> implements AnimationView {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                getPresenter().onReadyToAnimate();
                return true;
            }
        });
    }

    protected abstract Animator createEnterAnimator();

    protected abstract Animator createExitAnimator();

    protected abstract Animator createInAnimator(boolean withLargeDelay);

    protected abstract Animator createOutAnimator();

    @Override
    public void animateEnter() {
        Animator enterAnimator = createEnterAnimator();
        enterAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getPresenter().onAnimateEnterFinished();
            }
        });
        enterAnimator.start();
    }

    @Override
    public void animateExit() {
        Animator exitAnimator = createExitAnimator();
        exitAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getPresenter().onAnimateExitFinished();
            }
        });
        exitAnimator.start();
    }

    @Override
    public void animateIn(boolean withLargeDelay) {
        Animator inAnimator = createInAnimator(withLargeDelay);
        inAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getPresenter().onAnimateInFinished();
            }
        });
        inAnimator.start();
    }

    @Override
    public void animateOut() {
        Animator outAnimator = createOutAnimator();
        outAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getPresenter().onAnimateOutFinished();
            }
        });
        outAnimator.start();
    }
}
