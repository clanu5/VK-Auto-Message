package com.qwert2603.vkautomessage.recycler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.DecelerateInterpolator;

import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerItemAnimator extends DefaultItemAnimator {

    public enum EnterOrigin {
        BOTTOM,
        LEFT
    }

    private Map<RecyclerView.ViewHolder, Animator> mEnterAnimation = new HashMap<>();

    private int mLastAddAnimatedItem = -20;

    private EnterOrigin mEnterOrigin = EnterOrigin.BOTTOM;

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        LogUtils.d("RecyclerItemAnimator animateMove" + holder);
        return super.animateMove(holder, fromX, fromY, toX, toY);
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        LogUtils.d("RecyclerItemAnimator animateChange" + oldHolder + " " + newHolder);
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        LogUtils.d("RecyclerItemAnimator animateAdd" + holder);
        if (holder.getAdapterPosition() > mLastAddAnimatedItem) {
            ++mLastAddAnimatedItem;
            runEnterAnimation(holder);
            return false;
        }

        dispatchAddFinished(holder);
        return false;
    }

    private void runEnterAnimation(RecyclerView.ViewHolder viewHolder) {
        LogUtils.d("RecyclerItemAnimator runEnterAnimation " + viewHolder);
        if (mEnterOrigin == EnterOrigin.BOTTOM) {
            int heightPixels = viewHolder.itemView.getResources().getDisplayMetrics().heightPixels;
            viewHolder.itemView.setTranslationY(heightPixels);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(viewHolder.itemView, "translationY", 0);
            translationY.setDuration(500);
            translationY.setStartDelay(Math.min(800, viewHolder.getAdapterPosition() * 60) + 500);
            translationY.setInterpolator(new DecelerateInterpolator(3.0f));
            translationY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dispatchAddFinished(viewHolder);
                    mEnterAnimation.remove(viewHolder);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    viewHolder.itemView.setTranslationY(0);
                }
            });
            mEnterAnimation.put(viewHolder, translationY);
            translationY.start();
        } else if (mEnterOrigin == EnterOrigin.LEFT) {
            int widthPixels = viewHolder.itemView.getResources().getDisplayMetrics().widthPixels;
            viewHolder.itemView.setTranslationX(-1 * widthPixels);
            ObjectAnimator translationX = ObjectAnimator.ofFloat(viewHolder.itemView, "translationX", 0);
            translationX.setDuration(800);
            translationX.setStartDelay(Math.min(1200, viewHolder.getAdapterPosition() * 80) + 1000);
            translationX.setInterpolator(new DecelerateInterpolator(3.0f));
            translationX.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dispatchAddFinished(viewHolder);
                    mEnterAnimation.remove(viewHolder);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    viewHolder.itemView.setTranslationX(0);
                }
            });
            mEnterAnimation.put(viewHolder, translationX);
            translationX.start();
        }
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
}
