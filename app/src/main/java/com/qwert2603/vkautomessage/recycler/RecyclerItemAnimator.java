package com.qwert2603.vkautomessage.recycler;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecyclerItemAnimator extends DefaultItemAnimator {

    private static final int ENTER_EACH_ITEM_DELAY = 50;

    public enum EnterOrigin {
        BOTTOM,
        LEFT,
        RIGHT,
        LEFT_OR_RIGHT
    }

    // TODO: 15.01.2017 use not LAST, but mIdToAnimateEnter
    public enum AnimateEnterMode {
        NONE,
        LAST,
        ALL
    }

    private Set<RecyclerView.ViewHolder> mEnterAnimations = new HashSet<>();
    private Map<RecyclerView.ViewHolder, Animator> mRemoveAnimations = new HashMap<>();

    private EnterOrigin mEnterOrigin = EnterOrigin.BOTTOM;

    private AnimateEnterMode mAnimateEnterMode = AnimateEnterMode.NONE;

    private boolean mDelayEnter = false;
    private long mMinEnterDelay = 0;

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        LogUtils.d("animateAdd " + holder);
        BaseRecyclerViewAdapter.RecyclerViewHolder viewHolder = (BaseRecyclerViewAdapter.RecyclerViewHolder) holder;
        if (mAnimateEnterMode == AnimateEnterMode.ALL ||
                (mAnimateEnterMode == AnimateEnterMode.LAST && viewHolder.getAdapterPosition() == viewHolder.getItemsCount() - 1)) {
            runEnterAnimation(viewHolder);
            return false;
        }
        dispatchAddFinished(holder);
        return false;
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        // TODO: 29.11.2016 сделать нормальную анимацию и одновременном удалении нескольких элементов
        //http:blog.trsquarelab.com/2015/12/creating-custom-animation-in.html

        LogUtils.d("animateRemove " + holder + " isSwiped() == " + ((BaseRecyclerViewAdapter.RecyclerViewHolder) holder).isSwiped());

        if (((BaseRecyclerViewAdapter.RecyclerViewHolder) holder).isSwiped()) {
            dispatchRemoveFinished(holder);
            holder.itemView.setTranslationX(0);
            return false;
        }

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

        if (mEnterOrigin == EnterOrigin.BOTTOM) {
            viewHolder.itemView.setTranslationY(heightPixels);
            viewHolder.itemView.animate().translationY(0);
        } else if (mEnterOrigin == EnterOrigin.LEFT) {
            viewHolder.itemView.setTranslationX(-1 * widthPixels);
            viewHolder.itemView.animate().translationX(0);
        } else if (mEnterOrigin == EnterOrigin.RIGHT) {
            viewHolder.itemView.setTranslationX(widthPixels);
            viewHolder.itemView.animate().translationX(0);
        } else if (mEnterOrigin == EnterOrigin.LEFT_OR_RIGHT) {
            viewHolder.itemView.setTranslationX((viewHolder.getAdapterPosition() % 2 == 0 ? -1 : 1) * widthPixels);
            viewHolder.itemView.animate().translationX(0);
        } else {
            return;
        }

        if (mDelayEnter) {
            LogUtils.d("setStartDelay " + (mMinEnterDelay + viewHolder.getAdapterPosition() * ENTER_EACH_ITEM_DELAY));
            viewHolder.itemView.animate()
                    .setStartDelay(mMinEnterDelay + viewHolder.getAdapterPosition() * ENTER_EACH_ITEM_DELAY);
        }

        mEnterAnimations.add(viewHolder);

        viewHolder.itemView.animate()
                .setDuration(getAddDuration())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        viewHolder.itemView.setTranslationX(0);
                        viewHolder.itemView.setTranslationY(0);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewHolder.itemView.animate()
                                .setStartDelay(0)
                                .setListener(null);
                        mEnterAnimations.remove(viewHolder);
                        dispatchAddFinished(viewHolder);
                    }
                })
                .withLayer()
                .start();
    }

    private void cancelCurrentAnimationIfExist(RecyclerView.ViewHolder item) {
        if (mEnterAnimations.remove(item)) {
            LogUtils.d("cancelCurrentAnimationIfExist mEnterAnimations cancel");
            item.itemView.animate().cancel();
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
        ArrayList<RecyclerView.ViewHolder> viewHolders = new ArrayList<>(mEnterAnimations);
        mEnterAnimations.clear();
        for (RecyclerView.ViewHolder viewHolder : viewHolders) {
            viewHolder.itemView.animate().cancel();
        }

        List<Animator> animators = new ArrayList<>(mRemoveAnimations.values());
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

    @SuppressWarnings("unused")
    public long getMinEnterDelay() {
        return mMinEnterDelay;
    }

    public void setMinEnterDelay(long minEnterDelay) {
        mMinEnterDelay = minEnterDelay;
    }
}
