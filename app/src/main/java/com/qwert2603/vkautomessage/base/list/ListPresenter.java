package com.qwert2603.vkautomessage.base.list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.in_out_animation.AnimationPresenter;
import com.qwert2603.vkautomessage.base.in_out_animation.ShouldCheckIsInningOrInside;
import com.qwert2603.vkautomessage.model.Identifiable;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Презентер для view списка с поддержкой in/out анимации и появления списка.
 *
 * @param <T> тип элемента списка
 * @param <M> тип модели
 * @param <V> тип представления
 */
public abstract class ListPresenter<T extends Identifiable, M, V extends ListView<T>> extends AnimationPresenter<M, V> {

    private static final int NO_ITEM_ID_TO_MOVE = -1;

    private enum AnimationState {
        WAITING_FOR_TRIGGER,
        SHOULD_START,
        STARTED
    }

    private AnimationState mListEnterAnimationState = AnimationState.WAITING_FOR_TRIGGER;

    private int mItemIdToMove = NO_ITEM_ID_TO_MOVE;
    private boolean mMoveWithSetPressed = false;

    private Set<Integer> mSelectedIds = new HashSet<>();

    protected abstract List<T> getList();

    protected abstract boolean isError();

    protected abstract void doLoadList();

    protected abstract void doLoadItem(int id);

    @Override
    protected void onUpdateView(@NonNull V view) {
        super.onUpdateView(view);
        if (getModel() == null) {
            if (isError()) {
                view.showError();
            } else {
                view.showLoading();
            }
        } else {
            if (mListEnterAnimationState != AnimationState.WAITING_FOR_TRIGGER) {
                List<T> list = getList();
                if (mListEnterAnimationState == AnimationState.SHOULD_START) {
                    mListEnterAnimationState = AnimationState.STARTED;
                    if (list == null || list.isEmpty()) {
                        view.showEmpty();
                        view.animateInNewItemButton(0);
                    } else {
                        view.animateAllItemsEnter(true);
                        view.delayEachItemEnterAnimation(true);
                        view.showListEnter(list);

                        int delay = Math.min(view.getItemEnterDelayPerScreen(), list.size() * RecyclerItemAnimator.ENTER_EACH_ITEM_DELAY);
                        view.animateInNewItemButton(delay);
                    }
                } else {
                    if (list == null || list.isEmpty()) {
                        view.showEmpty();
                    } else {
                        view.showList(list);
                    }
                }
            }
        }
    }

    @Override
    public void onReadyToAnimate() {
        if (isOutside()) {
            getView().animateInNewItemButton(50);
        }
        super.onReadyToAnimate();
    }

    @Override
    public void onAnimateInFinished() {
        super.onAnimateInFinished();
        if (mListEnterAnimationState == AnimationState.WAITING_FOR_TRIGGER) {
            mListEnterAnimationState = AnimationState.SHOULD_START;
            updateView();
        }
    }

    @Override
    public void onAnimateOutFinished() {
        super.onAnimateOutFinished();
        if (mItemIdToMove != NO_ITEM_ID_TO_MOVE) {
            performMoveToItem(mItemIdToMove, mMoveWithSetPressed);
            mItemIdToMove = NO_ITEM_ID_TO_MOVE;
        }
    }

    protected void performMoveToItem(int itemIdToMove, boolean moveWithSetPressed) {
        getView().moveToDetailsForItem(itemIdToMove, moveWithSetPressed);
    }

    @ShouldCheckIsInningOrInside
    public void onItemAtPositionClicked(int position) {
        if (!isInningOrInside()) {
            return;
        }
        List<T> list = getList();
        if (list == null) {
            return;
        }
        getView().smoothScrollToPosition(position);
        if (mSelectedIds.isEmpty()) {
            setItemIdToMove(list.get(position).getId(), false);
            animateOut();
        } else {
            toggleItemSelectionState(position);
        }
    }

    @ShouldCheckIsInningOrInside
    public void onItemAtPositionLongClicked(int position) {
        // TODO: 29.11.2016 начинать множественное выделение на longClick (чтобы удалять сразу несколько потом)
        if (!isInningOrInside()) {
            return;
        }
        getView().smoothScrollToPosition(position);
        //askDeleteItem(position);
        toggleItemSelectionState(position);
    }

    private void toggleItemSelectionState(int position) {
        int id = getList().get(position).getId();
        if (!mSelectedIds.contains(id)) {
            if (mSelectedIds.isEmpty()) {
                getView().startListSelectionMode();
            }
            mSelectedIds.add(id);
            getView().setItemSelectionState(position, true);
        } else {
            mSelectedIds.remove(id);
            getView().setItemSelectionState(position, false);
            if (mSelectedIds.isEmpty()) {
                getView().stopListSelectionMode();
            }
        }
    }

    public void onSelectAllClicked() {
        for (T t : getList()) {
            mSelectedIds.add(t.getId());
        }
        getView().selectAllItems();
    }

    @Override
    public void onActionModeCancelled() {
        super.onActionModeCancelled();
        mSelectedIds.clear();
        getView().unSelectAllItems();
    }

    @ShouldCheckIsInningOrInside
    public void onItemDismissed(int position) {
        if (!isInningOrInside()) {
            return;
        }
        askDeleteItem(position);
    }

    @ShouldCheckIsInningOrInside
    public void onItemDeleteSubmitted(int id) {
        getView().unSelectAllItems();
    }

    public void onItemDeleteCanceled(int id) {
        getView().unSelectAllItems();
    }

    public void onReloadList() {
        doLoadList();
        updateView();
    }

    public final void onReloadItem(int id) {
        doLoadItem(id);
        updateView();
    }

    @ShouldCheckIsInningOrInside
    public void onToolbarClicked() {
        if (!isInningOrInside()) {
            return;
        }
        getView().scrollListToTop();
    }

    private void askDeleteItem(int position) {
        List<T> list = getList();
        if (list == null) {
            return;
        }
        getView().askDeleteItem(list.get(position).getId());
        getView().setItemSelectionState(position, true);
    }

    protected final void setItemIdToMove(int id, boolean withSetPressed) {
        mItemIdToMove = id;
        mMoveWithSetPressed = withSetPressed;
    }

}
