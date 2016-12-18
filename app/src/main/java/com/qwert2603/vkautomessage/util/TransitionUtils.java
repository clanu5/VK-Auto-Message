package com.qwert2603.vkautomessage.util;

import android.app.Activity;

public final class TransitionUtils {

    /**
     * Set given duration to all "SharedElement*Transition" of given activity.
     */
    public static void setSharedElementTransitionsDuration(Activity activity, int duration) {
        activity.getWindow().getSharedElementEnterTransition().setDuration(duration);
        activity.getWindow().getSharedElementExitTransition().setDuration(duration);
        activity.getWindow().getSharedElementReenterTransition().setDuration(duration);
        activity.getWindow().getSharedElementReturnTransition().setDuration(duration);
    }

}
