package com.qwert2603.vkautomessage.base.in_out_animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.qwert2603.vkautomessage.base.BaseFragment;

/**
 * Фрагмент для отображения списка и показа in/out-анимаций.
 */
public abstract class InOutAnimationFragment<P extends InOutAnimationPresenter> extends BaseFragment<P> implements InOutAnimationView {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getPresenter().onReadyToAnimateIn();
    }

    protected abstract Animator createInAnimator(boolean withLargeDelay);

    protected abstract Animator createOutAnimator();

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
    public void animateOut(int id) {
        Animator outAnimator = createOutAnimator();
        outAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getPresenter().onAnimateOutFinished(id);
            }
        });
        outAnimator.start();
    }

}
