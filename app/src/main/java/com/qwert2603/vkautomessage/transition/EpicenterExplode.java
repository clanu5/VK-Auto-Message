package com.qwert2603.vkautomessage.transition;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.transition.Explode;
import android.transition.Transition;
import android.util.AttributeSet;

/**
 * Because epicenter for activity transition calculated via framework's internal tools,
 * so we just hardcode epicenter Rect here.
 */
public class EpicenterExplode extends Explode implements EpicenterTransition {

    @Nullable
    private Rect mEpicenterRect;

    public EpicenterExplode() {
    }

    public EpicenterExplode(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setEpicenterCallback(EpicenterCallback epicenterCallback) {
        // nth
    }

    @Override
    public void setEpicenterRect(@Nullable Rect epicenterRect) {
        mEpicenterRect = epicenterRect;
    }

    @Override
    public EpicenterCallback getEpicenterCallback() {
        return new EpicenterCallback() {
            @Override
            public Rect onGetEpicenter(Transition transition) {
                return mEpicenterRect;
            }
        };
    }

    @Override
    public Rect getEpicenter() {
        return mEpicenterRect;
    }
}
