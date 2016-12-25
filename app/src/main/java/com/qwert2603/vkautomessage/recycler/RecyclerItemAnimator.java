package com.qwert2603.vkautomessage.recycler;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerItemAnimator extends DefaultItemAnimator {

    public static final int ENTER_DURATION = 250;
    private static final int ENTER_EACH_ITEM_DELAY = 50;

    public enum EnterOrigin {
        BOTTOM,
        LEFT,
        RIGHT,
        LEFT_OR_RIGHT
    }

    public enum AnimateEnterMode {
        NONE,
        LAST,
        ALL
    }

    private Map<RecyclerView.ViewHolder, Animator> mEnterAnimations = new HashMap<>();
    private Map<RecyclerView.ViewHolder, Animator> mRemoveAnimations = new HashMap<>();

    private EnterOrigin mEnterOrigin = EnterOrigin.BOTTOM;

    private Interpolator mEnterInterpolator = new DecelerateInterpolator();

    private AnimateEnterMode mAnimateEnterMode = AnimateEnterMode.NONE;

    private boolean mDelayEnter = false;

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        LogUtils.d("animateAdd " + holder);
        BaseRecyclerViewAdapter.RecyclerViewHolder viewHolder = (BaseRecyclerViewAdapter.RecyclerViewHolder) holder;
        if (mAnimateEnterMode == AnimateEnterMode.ALL ||
                (mAnimateEnterMode == AnimateEnterMode.LAST && viewHolder.getAdapterPosition() == viewHolder.getItemsCount() - 1)) {
            runEnterAnimation(viewHolder);
        }
        dispatchAddFinished(holder);
        return false;
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        // TODO: 29.11.2016 сделать нормальную анимацию и одновременном удалении нескольких элементов
    //http:blog.trsquarelab.com/2015/12/creating-custom-animation-in.html

        // TODO: 24.12.2016 don't animate if item was swiped
        LogUtils.d("animateRemove " + holder);
        runRemoveAnimation(holder);
        return false;
    }

    private int REM_DUR = 300;

    @Override
    public void runPendingAnimations() {
        if (mRemoveAnimations.isEmpty()) {
            super.runPendingAnimations();
        } else {
            //new Handler(Looper.getMainLooper()).postDelayed(RecyclerItemAnimator.super::runPendingAnimations, REM_DUR);
        }
    }

    private void runRemoveAnimation(RecyclerView.ViewHolder viewHolder) {
        Animator animator = AnimatorInflater.loadAnimator(viewHolder.itemView.getContext(), R.animator.item_remove_rotate);
        animator.setTarget(viewHolder.itemView);
        animator.setDuration(REM_DUR);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                viewHolder.itemView.setScaleX(1);
                viewHolder.itemView.setScaleY(1);
                viewHolder.itemView.setRotationY(0);
                dispatchRemoveFinished(viewHolder);
                mRemoveAnimations.remove(viewHolder);
                runPendingAnimations();
            }
        });
        mRemoveAnimations.put(viewHolder, animator);
        animator.start();
    }

    private void runEnterAnimation(BaseRecyclerViewAdapter.RecyclerViewHolder viewHolder) {
        LogUtils.d("runEnterAnimation " + viewHolder);
        int heightPixels = viewHolder.itemView.getResources().getDisplayMetrics().heightPixels;
        int widthPixels = viewHolder.itemView.getResources().getDisplayMetrics().widthPixels;

        ObjectAnimator objectAnimator;

        if (mEnterOrigin == EnterOrigin.BOTTOM) {
            viewHolder.itemView.setTranslationY(heightPixels);
            objectAnimator = ObjectAnimator.ofFloat(viewHolder.itemView, "translationY", 0);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    viewHolder.itemView.setTranslationY(0);
                }
            });
        } else if (mEnterOrigin == EnterOrigin.LEFT) {
            viewHolder.itemView.setTranslationX(-1 * widthPixels);
            objectAnimator = ObjectAnimator.ofFloat(viewHolder.itemView, "translationX", 0);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    viewHolder.itemView.setTranslationX(0);
                }
            });
        } else if (mEnterOrigin == EnterOrigin.RIGHT) {
            viewHolder.itemView.setTranslationX(widthPixels);
            objectAnimator = ObjectAnimator.ofFloat(viewHolder.itemView, "translationX", 0);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    viewHolder.itemView.setTranslationX(0);
                }
            });
        } else if (mEnterOrigin == EnterOrigin.LEFT_OR_RIGHT) {
            viewHolder.itemView.setTranslationX((viewHolder.getAdapterPosition() % 2 == 0 ? -1 : 1) * widthPixels);
            objectAnimator = ObjectAnimator.ofFloat(viewHolder.itemView, "translationX", 0);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    viewHolder.itemView.setTranslationX(0);
                }
            });
        } else {
            return;
        }

        objectAnimator.setDuration(ENTER_DURATION);

        if (mDelayEnter) {
            LogUtils.d("setStartDelay " + viewHolder.getAdapterPosition() * ENTER_EACH_ITEM_DELAY);
            objectAnimator.setStartDelay(viewHolder.getAdapterPosition() * ENTER_EACH_ITEM_DELAY);
        }

        objectAnimator.setInterpolator(mEnterInterpolator);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAddFinished(viewHolder);
                mEnterAnimations.remove(viewHolder);
            }
        });
        mEnterAnimations.put(viewHolder, objectAnimator);
        objectAnimator.start();
    }

    private void cancelCurrentAnimationIfExist(RecyclerView.ViewHolder item) {
        if (mEnterAnimations.containsKey(item)) {
            mEnterAnimations.remove(item).cancel();
        }
        if (mRemoveAnimations.containsKey(item)) {
            mRemoveAnimations.remove(item).cancel();
        }
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
        cancelCurrentAnimationIfExist(item);
    }

    @Override
    public void endAnimations() {
        List<Animator> animators = new ArrayList<>(mEnterAnimations.values());
        mEnterAnimations.clear();
        for (Animator animator : animators) {
            animator.cancel();
        }
        animators = new ArrayList<>(mRemoveAnimations.values());
        mRemoveAnimations.clear();
        for (Animator animator : animators) {
            animator.cancel();
        }

        super.endAnimations();
    }

    @SuppressWarnings("unused")
    public EnterOrigin getEnterOrigin() {
        return mEnterOrigin;
    }

    public void setEnterOrigin(EnterOrigin enterOrigin) {
        mEnterOrigin = enterOrigin;
    }

    @SuppressWarnings("unused")
    public AnimateEnterMode getAnimateEnterMode() {
        return mAnimateEnterMode;
    }

    public void setAnimateEnterMode(AnimateEnterMode animateEnterMode) {
        mAnimateEnterMode = animateEnterMode;
    }

    @SuppressWarnings("unused")
    public boolean isDelayEnter() {
        return mDelayEnter;
    }

    public void setDelayEnter(boolean delayEnter) {
        mDelayEnter = delayEnter;
    }
}
