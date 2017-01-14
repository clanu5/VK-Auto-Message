package com.qwert2603.vkautomessage.base.list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.Identifiable;
import com.qwert2603.vkautomessage.util.CollectionUtils;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;

/**
 * Презентер для view списка.
 *
 * @param <T> тип элемента списка
 * @param <M> тип модели
 * @param <V> тип представления
 */
public abstract class ListPresenter<T extends Identifiable, M, V extends ListView<T>> extends BasePresenter<M, V> {

    protected interface Transformer<T, U> {
        U transform(T t);
    }

    protected final Set<Integer> mSelectedIds = new HashSet<>();

    private List<T> mPrevList = null;

    private List<T> mShowingList = null;

    private List<T> mPrevShowingList = null;

    protected abstract boolean isError();

    protected abstract void doLoadList();

    protected abstract void doLoadItem(int id);

    protected abstract Observable<Void> removeItem(int id);

    protected abstract Transformer<M, List<T>> listFromModel();

    protected abstract Transformer<M, List<T>> showingListFromModel();

    protected final void updateShowingList() {
        mShowingList = showingListFromModel().transform(getModel());
        updateView();
    }

    protected final List<T> getShowingList() {
        return mShowingList;
    }

    @Override
    protected void setModel(M model) {
        mShowingList = showingListFromModel().transform(model);
        super.setModel(model);
    }

    @Override
    protected void onUpdateView(@NonNull V view) {
        if (getModel() == null) {
            if (isError()) {
                view.showError();
            } else {
                view.showLoading();
            }
        } else {
            if (mShowingList == null || mShowingList.isEmpty()) {
                view.showEmpty();
            } else {
                view.showList(mShowingList);
            }
        }
    }

    public void onItemAtPositionClicked(int position) {
        if (mSelectedIds.isEmpty()) {
            getView().moveToDetailsForItem(mShowingList.get(position).getId(), false, position);
        } else {
            toggleItemSelectionState(position);
        }
    }

    public void onItemAtPositionLongClicked(int position) {
        toggleItemSelectionState(position);
    }

    private void toggleItemSelectionState(int position) {
        int id = mShowingList.get(position).getId();
        if (!mSelectedIds.contains(id)) {
            if (mSelectedIds.isEmpty()) {
                getView().startListSelectionMode();
                getView().showSelectedItemsCount(0);
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
        getView().showSelectedItemsCount(mSelectedIds.size());
    }

    public void onSelectAllClicked() {
        for (T t : mShowingList) {
            mSelectedIds.add(t.getId());
        }
        getView().showSelectedItemsCount(mSelectedIds.size());
        getView().selectAllItems();
    }

    public void onSelectionModeCancelled() {
        mSelectedIds.clear();
        getView().showSelectedItemsCount(0);
        getView().unSelectAllItems();
    }

    public void onDeleteSelectedClicked() {
        List<T> list = listFromModel().transform(getModel());
        mPrevList = new ArrayList<>(list);
        CollectionUtils.removeIf(list, t -> mSelectedIds.contains(t.getId()));

        mPrevShowingList = new ArrayList<>(mShowingList);
        CollectionUtils.removeIf(mShowingList, t -> mSelectedIds.contains(t.getId()));

        for (Integer id : mSelectedIds) {
            removeItem(id)
                    .subscribe(aLong -> {
                    }, LogUtils::e);
        }

        getView().showItemsDeleted(mSelectedIds.size());
        mSelectedIds.clear();
        updateView();
        getView().stopListSelectionMode();
    }

    public void onUndoDeletionClicked() {
        if (mPrevList != null) {
            List<T> list = listFromModel().transform(getModel());
            list.clear();
            list.addAll(mPrevList);
            mPrevList = null;

            mShowingList.clear();
            mShowingList.addAll(mPrevShowingList);
            mPrevShowingList = null;

            updateView();
        }
    }

    public void onItemDismissed(int position) {
//        askDeleteItem(position);

        removeItem(mShowingList.get(position).getId())
                .subscribe(aLong -> {
                }, LogUtils::e);

        if (mSelectedIds.contains(mShowingList.get(position).getId())) {
            toggleItemSelectionState(position);
        }

        mShowingList.remove(position);
        updateView();
    }

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

    public final void onReturnFromItemDetails(int id) {
        doLoadItem(id);
    }

    public void onScrollToTopClicked() {
        getView().scrollToTop();
    }

    private void askDeleteItem(int position) {
        getView().askDeleteItem(mShowingList.get(position).getId());
        getView().setItemSelectionState(position, true);
    }

}
