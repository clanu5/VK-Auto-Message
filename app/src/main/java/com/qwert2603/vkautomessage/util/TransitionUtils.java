package com.qwert2603.vkautomessage.util;

import android.app.Activity;
import android.support.annotation.TransitionRes;
import android.transition.Transition;
import android.transition.TransitionInflater;

public final class TransitionUtils {

    /**
     * Set given transition to all "SharedElement***Transition" methods of given activity.
     */
    public static void setSharedElementTransitions(Activity activity, @TransitionRes int transitionRes) {
        Transition transition = TransitionInflater.from(activity).inflateTransition(transitionRes);
        activity.getWindow().setSharedElementReenterTransition(transition);
        activity.getWindow().setSharedElementEnterTransition(transition);
        activity.getWindow().setSharedElementExitTransition(transition);
        activity.getWindow().setSharedElementReturnTransition(transition);
    }

    @SuppressWarnings("unused")
    public static class TransitionListenerCallback implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }

}
