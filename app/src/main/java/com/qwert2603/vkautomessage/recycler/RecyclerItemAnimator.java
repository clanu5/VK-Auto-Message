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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecyclerItemAnimator extends DefaultItemAnimator {

    public static final int ENTER_DURATION = 250;
    public static final int ENTER_EACH_ITEM_DELAY = 50;

    public enum EnterOrigin {
        BOTTOM,
        LEFT,
        RIGHT,
        LEFT_OR_RIGHT
    }

    private Map<RecyclerView.ViewHolder, Animator> mEnterAnimations = new HashMap<>();
    private Map<RecyclerView.ViewHolder, Animator> mRemoveAnimations = new HashMap<>();

    private EnterOrigin mEnterOrigin = EnterOrigin.BOTTOM;

    private Interpolator mEnterInterpolator = new DecelerateInterpolator();

    private int mEnterDelayPerScreen = ENTER_EACH_ITEM_DELAY * 15;
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

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        // TODO: 29.11.2016 сделать нормальную анимацию и одновременном удалении нескольких элементов
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
            objectAnimator.setStartDelay(Math.min(mEnterDelayPerScreen, viewHolder.getAdapterPosition() * ENTER_EACH_ITEM_DELAY));
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

    public int getEnterDelayPerScreen() {
        return mEnterDelayPerScreen;
    }

    /**
     * Установить кол-во элементов, которые видны на экране в любой момент времени.
     * Чтобы анимировалось появление только первых itemsPerScreen элементов.
     *
     * @param itemsPerScreen кол-во элементов, которые видны на экране в любой момент времени.
     */
    public void setItemsPerScreen(int itemsPerScreen) {
        mEnterDelayPerScreen = itemsPerScreen * ENTER_EACH_ITEM_DELAY;
    }
}
