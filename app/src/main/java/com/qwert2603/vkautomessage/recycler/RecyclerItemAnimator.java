package com.qwert2603.vkautomessage.recycler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecyclerItemAnimator extends DefaultItemAnimator {

    public static final int ENTER_DURATION = 400;
    public static final int ENTER_EACH_ITEM_DELAY = 60;
    public static final int MAX_ENTER_DURATION = 1200;

    public enum EnterOrigin {
        BOTTOM,
        LEFT,
        RIGHT,
        LEFT_OR_RIGHT
    }

    private Map<RecyclerView.ViewHolder, Animator> mEnterAnimation = new HashMap<>();

    private EnterOrigin mEnterOrigin = EnterOrigin.BOTTOM;

    private Interpolator mEnterInterpolator = new DecelerateInterpolator();

    private Set<Integer> mItemsToAnimateEnter = new HashSet<>();
    private boolean mAlwaysAnimateEnter = true;
    private boolean mDelayEnter = false;

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        boolean animateEnterThisItem = mItemsToAnimateEnter.remove((int) holder.getItemId());
        if (animateEnterThisItem || mAlwaysAnimateEnter) {
            runEnterAnimation(holder);
            return false;
        }

        dispatchAddFinished(holder);
        return false;
    }

    private void runEnterAnimation(RecyclerView.ViewHolder viewHolder) {
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
            objectAnimator.setStartDelay(Math.min(MAX_ENTER_DURATION, viewHolder.getAdapterPosition() * ENTER_EACH_ITEM_DELAY));
        }
        objectAnimator.setInterpolator(mEnterInterpolator);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAddFinished(viewHolder);
                mEnterAnimation.remove(viewHolder);
            }
        });
        mEnterAnimation.put(viewHolder, objectAnimator);
        objectAnimator.start();
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        if (mEnterAnimation.containsKey(item)) {
            mEnterAnimation.remove(item).cancel();
        }
        super.endAnimation(item);
    }

    @Override
    public void endAnimations() {
        List<Animator> animators = new ArrayList<>(mEnterAnimation.values());
        mEnterAnimation.clear();
        for (Animator animator : animators) {
            animator.cancel();
        }

        super.endAnimations();
    }

    public EnterOrigin getEnterOrigin() {
        return mEnterOrigin;
    }

    public void setEnterOrigin(EnterOrigin enterOrigin) {
        mEnterOrigin = enterOrigin;
    }

    public boolean isAlwaysAnimateEnter() {
        return mAlwaysAnimateEnter;
    }

    public void setAlwaysAnimateEnter(boolean alwaysAnimateEnter) {
        mAlwaysAnimateEnter = alwaysAnimateEnter;
    }

    public boolean isDelayEnter() {
        return mDelayEnter;
    }

    public void setDelayEnter(boolean delayEnter) {
        mDelayEnter = delayEnter;
    }

    public void addItemToAnimateEnter(int id) {
        mItemsToAnimateEnter.add(id);
    }

    public void removeItemToAnimateEnter(int id) {
        mItemsToAnimateEnter.remove(id);
    }

    public void clearItemsToAnimateEnter() {
        mItemsToAnimateEnter.clear();
    }
}
