package com.qwert2603.vkautomessage.util;

import android.app.Activity;
import android.transition.Transition;

public final class TransitionUtils {

    /**
     * Set given duration to all "SharedElement***Transition" of given activity.
     */
    public static void setSharedElementTransitionsDuration(Activity activity, int duration) {
        activity.getWindow().getSharedElementEnterTransition().setDuration(duration);
        activity.getWindow().getSharedElementExitTransition().setDuration(duration);
        activity.getWindow().getSharedElementReenterTransition().setDuration(duration);
        activity.getWindow().getSharedElementReturnTransition().setDuration(duration);
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
