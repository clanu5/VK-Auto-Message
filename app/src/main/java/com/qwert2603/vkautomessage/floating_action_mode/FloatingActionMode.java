package com.qwert2603.vkautomessage.floating_action_mode;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.util.AndroidUtils;
import com.qwert2603.vkautomessage.util.LogUtils;

/**
 * Floating action mode that shows layout given to it.
 * Can be dragged over screen and swiped-to-dismiss.
 */
@CoordinatorLayout.DefaultBehavior(FloatingActionMode.FloatingActionModeBehavior.class)
public class FloatingActionMode extends LinearLayout {

    private static final String SUPER_STATE_KEY = "com.qwert2603.vkautomessage.floating_action_mode.SUPER_STATE_KEY";
    private static final String DRAG_ICON_KEY = "com.qwert2603.vkautomessage.floating_action_mode.DRAG_ICON_KEY";
    private static final String CAN_DISMISS_KEY = "com.qwert2603.vkautomessage.floating_action_mode.CAN_DISMISS_KEY";
    private static final String DISMISS_THRESHOLD_KEY = "com.qwert2603.vkautomessage.floating_action_mode.DISMISS_THRESHOLD_KEY";
    private static final String CAN_DRAG_KEY = "com.qwert2603.vkautomessage.floating_action_mode.CAN_DRAG_KEY";
    private static final String ANIMATION_CONTENT_RES_KEY = "com.qwert2603.vkautomessage.floating_action_mode.ANIMATION_CONTENT_RES_KEY";
    private static final String ANIMATION_DURATION_KEY = "com.qwert2603.vkautomessage.floating_action_mode.ANIMATION_DURATION_KEY";
    private static final String HIDE_DIRECTION_KEY = "com.qwert2603.vkautomessage.floating_action_mode.HIDE_DIRECTION_KEY";

    public enum HideDirection {
        NONE,
        TOP,
        BOTTOM,
        NEAREST
    }

    private ImageButton mDragImageButton;

    @DrawableRes
    private int mDragIcon = R.drawable.ic_drag_white_24dp;

    @Nullable
    private OnActionModeDismissListener mOnActionModeDismissListener;

    private boolean mCanDismiss = true;
    private boolean mCanDrag = true;

    @FloatRange(from = 0.0f, to = 1.0f)
    private float mDismissThreshold = 0.4f;

    @LayoutRes
    private int mActionModeContentRes = 0;

    private int mAnimationDuration = 400;

    private HideDirection mHideDirection = HideDirection.NEAREST;

    /**
     * Top offset of FloatingActionMode calculated by {@link FloatingActionModeBehavior}.
     */
    private int mTopOffset;

    private float mDragTranslationY = 0;

    public FloatingActionMode(Context context) {
        super(context);
        LogUtils.d("public FloatingActionMode(Context context) {");
        init(context);
    }

    public FloatingActionMode(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            LogUtils.d("public FloatingActionMode(Context context, @Nullable AttributeSet attrs) {" + attrs);
        }

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionMode);
            mCanDismiss = typedArray.getBoolean(R.styleable.FloatingActionMode_can_dismiss, true);
            mDismissThreshold = typedArray.getFloat(R.styleable.FloatingActionMode_dismiss_threshold, 0.4f);
            mCanDrag = typedArray.getBoolean(R.styleable.FloatingActionMode_can_drag, true);
            mDragIcon = typedArray.getResourceId(R.styleable.FloatingActionMode_drag_icon, R.drawable.ic_drag_white_24dp);
            mActionModeContentRes = typedArray.getResourceId(R.styleable.FloatingActionMode_content_res, 0);
            mAnimationDuration = typedArray.getInteger(R.styleable.FloatingActionMode_animation_duration, 400);
            int hd = typedArray.getInteger(R.styleable.FloatingActionMode_hide_direction, -1);
            mHideDirection = hd > 0 ? HideDirection.values()[hd] : HideDirection.NEAREST;
            typedArray.recycle();
        }

        init(context);
    }

    private void init(Context context) {
        if (!isInEditMode()) {
            setVisibility(INVISIBLE);
        }

        View view = LayoutInflater.from(context).inflate(R.layout.floating_action_mode, this, true);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        mDragImageButton = (ImageButton) view.findViewById(R.id.drag_button);

        mDragImageButton.setOnTouchListener(new View.OnTouchListener() {
            float prevTransitionY;
            float startRawX;
            float startRawY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mCanDrag) {
                    return false;
                }

                float q = Math.abs(event.getRawX() - startRawX) / FloatingActionMode.this.getWidth();
                LogUtils.d("q == " + q);

                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mDragImageButton.setPressed(true);
                    startRawX = event.getRawX();
                    startRawY = event.getRawY();
                    prevTransitionY = FloatingActionMode.this.getTranslationY();
                } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    mDragTranslationY = prevTransitionY + event.getRawY() - startRawY;
                    setTranslationY(mDragTranslationY);
                    setTranslationX(event.getRawX() - startRawX);
                    if (mCanDismiss) {
                        float alpha = q < mDismissThreshold ? 1 : 1 - (q - mDismissThreshold) / (1 - mDismissThreshold);
                        setAlpha(alpha);
                    }
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    mDragImageButton.setPressed(false);
                    FloatingActionMode.this.animate().translationX(0);
                    if (mCanDismiss && q > mDismissThreshold) {
                        if (mOnActionModeDismissListener != null) {
                            mOnActionModeDismissListener.onActionModeDismiss();
                        }
                    }
                }
                return true;
            }
        });

        mDragImageButton.setVisibility(mCanDrag ? VISIBLE : INVISIBLE);
        mDragImageButton.setImageResource(mDragIcon);

        if (mActionModeContentRes != 0) {
            start(mActionModeContentRes);
        }
    }

    public interface OnActionModeDismissListener {
        void onActionModeDismiss();
    }

    @SuppressWarnings("unused")
    @Nullable
    public OnActionModeDismissListener getOnActionModeDismissListener() {
        return mOnActionModeDismissListener;
    }

    public void setOnActionModeDismissListener(@Nullable OnActionModeDismissListener onActionModeDismissListener) {
        mOnActionModeDismissListener = onActionModeDismissListener;
    }

    @SuppressWarnings("unused")
    @FloatRange(from = 0.0f, to = 1.0f)
    public float getDismissThreshold() {
        return mDismissThreshold;
    }

    /**
     * Set threshold from than actionMode will be dismissed.
     * If threshold > (transitionX/width) actionMode will be dismissed after user stops touching {@link #mDragImageButton}.
     */
    @SuppressWarnings("unused")
    public void setDismissThreshold(@FloatRange(from = 0.0f, to = 1.0f) float dismissThreshold) {
        mDismissThreshold = dismissThreshold;
    }

    @SuppressWarnings("unused")
    public boolean isCanDismiss() {
        return mCanDismiss;
    }

    @SuppressWarnings("unused")
    public void setCanDismiss(boolean canDismiss) {
        mCanDismiss = canDismiss;
    }

    @SuppressWarnings("unused")
    public HideDirection getHideDirection() {
        return mHideDirection;
    }

    @SuppressWarnings("unused")
    public void setHideDirection(HideDirection hideDirection) {
        mHideDirection = hideDirection;
    }

    @SuppressWarnings("unused")
    public int getAnimationDuration() {
        return mAnimationDuration;
    }

    @SuppressWarnings("unused")
    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    @SuppressWarnings("unused")
    public int getDragIcon() {
        return mDragIcon;
    }

    @SuppressWarnings("unused")
    public void setDragIcon(int dragIcon) {
        mDragIcon = dragIcon;
        mDragImageButton.setImageResource(mDragIcon);
    }

    @SuppressWarnings("unused")
    public boolean isCanDrag() {
        return mCanDrag;
    }

    @SuppressWarnings("unused")
    public void setCanDrag(boolean canDrag) {
        mCanDrag = canDrag;
    }

    /**
     * Start actionMode with given content. With animation.
     */
    public void start(@LayoutRes int actionModeContentRes) {
        if (isStarted()) {
            throw new RuntimeException("FloatingActionMode already started!");
        }

        mActionModeContentRes = actionModeContentRes;
        View view = LayoutInflater.from(getContext()).inflate(mActionModeContentRes, this, true);

        setVisibility(VISIBLE);

        if (!isInEditMode()) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    hideWithoutAnimation();
                    animateShow();
                    return true;
                }
            });
        }
    }

    /**
     * Stop action mode. With animation.
     */
    public void stop() {
        mActionModeContentRes = 0;
        animateHide();
        AndroidUtils.runOnUI(() -> {
            setVisibility(INVISIBLE);
            mDragTranslationY = 0;
            setTranslationY(0);
            removeViewAt(1);
        }, mAnimationDuration);
    }

    public boolean isStarted() {
        // first is mDragImageButton.
        return getChildCount() > 1;
    }

    private void animateShow() {
        AndroidUtils.setViewEnabled(FloatingActionMode.this, true);
        animate().scaleY(1).scaleX(1).translationY(mDragTranslationY).alpha(1).setDuration(mAnimationDuration);
    }

    private void animateHide() {
        AndroidUtils.setViewEnabled(FloatingActionMode.this, false);
        setPivotY(0);
        setPivotX(getWidth() / 2);
        animate().scaleY(0.4f).scaleX(0.4f).translationY(getHideTranslationY()).alpha(0.5f).setDuration(mAnimationDuration);
    }

    private void hideWithoutAnimation() {
        AndroidUtils.setViewEnabled(FloatingActionMode.this, false);
        setPivotY(0);
        setPivotX(getWidth() / 2);
        setScaleY(0.4f);
        setScaleX(0.4f);
        setTranslationY(getHideTranslationY());
        setAlpha(0.5f);
    }

    private float getHideTranslationY() {
        float transitionY;
        ViewGroup parent = (ViewGroup) getParent();

        LogUtils.d("getHideTranslationY " + mTopOffset);

        switch (mHideDirection) {
            case TOP:
                transitionY = mTopOffset - getTop();
                break;
            case BOTTOM:
                transitionY = parent.getHeight() - getBottom() + getHeight() * 0.6f;
                break;
            case NEAREST:
                if (getTop() + getHeight() / 2 + mDragTranslationY < parent.getHeight() / 2) {
                    transitionY = mTopOffset - getTop();
                } else {
                    transitionY = parent.getHeight() - getBottom() + getHeight() * 0.6f;
                }
                break;
            case NONE:
            default:
                transitionY = 0;
                break;
        }
        return transitionY;
    }

    @SuppressWarnings("unused")
    public static class FloatingActionModeBehavior extends CoordinatorLayout.Behavior<FloatingActionMode> {

        public FloatingActionModeBehavior() {
        }

        public FloatingActionModeBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMode child, View dependency) {
            return dependency instanceof AppBarLayout;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMode child, View dependency) {
            if (dependency instanceof AppBarLayout) {
                int d = dependency.getHeight() - child.mTopOffset;
                child.mTopOffset = dependency.getHeight();
                child.setTop(child.getTop() + d);
                child.setBottom(child.getBottom() + d);
            }
            return true;
        }

        @Override
        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMode child, View directTargetChild, View target, int nestedScrollAxes) {
            return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
        }

        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMode child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

            if (dyConsumed > 0) {
                child.animateHide();
            } else if (dyConsumed < 0) {
                child.animateShow();
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        LogUtils.d("protected Parcelable onSaveInstanceState() {");
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState());

        bundle.putInt(DRAG_ICON_KEY, mDragIcon);
        bundle.putBoolean(CAN_DISMISS_KEY, mCanDismiss);
        bundle.putFloat(DISMISS_THRESHOLD_KEY, mDismissThreshold);
        bundle.putBoolean(CAN_DRAG_KEY, mCanDrag);
        bundle.putInt(ANIMATION_CONTENT_RES_KEY, mActionModeContentRes);
        bundle.putInt(ANIMATION_DURATION_KEY, mAnimationDuration);
        bundle.putInt(HIDE_DIRECTION_KEY, mHideDirection.ordinal());

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        LogUtils.d("protected void onRestoreInstanceState(Parcelable state) {");
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            mDragIcon = bundle.getInt(DRAG_ICON_KEY);
            mCanDismiss = bundle.getBoolean(CAN_DISMISS_KEY);
            mDismissThreshold = bundle.getFloat(DISMISS_THRESHOLD_KEY);
            mCanDrag = bundle.getBoolean(CAN_DRAG_KEY);
            mActionModeContentRes = bundle.getInt(ANIMATION_CONTENT_RES_KEY);
            mAnimationDuration = bundle.getInt(ANIMATION_DURATION_KEY);
            mHideDirection = HideDirection.values()[bundle.getInt(HIDE_DIRECTION_KEY)];

            state = bundle.getParcelable(SUPER_STATE_KEY);
        }
        super.onRestoreInstanceState(state);

        init(getContext());
    }
}
