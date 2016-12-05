package com.qwert2603.vkautomessage.base.in_out_animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.base.navigation.ActivityInterface;

/**
 * Фрагмент для отображения списка и показа in/out-анимаций.
 */
public abstract class AnimationFragment<P extends AnimationPresenter> extends BaseFragment<P> implements AnimationView {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getPresenter().onReadyToAnimate();
    }

    protected abstract Animator createEnterAnimator();

    protected abstract Animator createExitAnimator();

    protected abstract Animator createInAnimator(boolean withLargeDelay);

    protected abstract Animator createOutAnimator();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ActivityInterface) context).setOnBackPressedListener(() -> getPresenter().onBackPressed());
    }

    @Override
    public void onDetach() {
        ((ActivityInterface) getActivity()).setOnBackPressedListener(null);
        super.onDetach();
    }

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

    @Override
    public void performBackPressed() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((ActivityInterface) activity).performOnBackPressed();
        }
    }
}
