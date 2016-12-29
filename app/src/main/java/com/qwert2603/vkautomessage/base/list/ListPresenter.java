package com.qwert2603.vkautomessage.base.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.Identifiable;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

    protected final Set<Integer> mSelectedIds = new HashSet<>();

    @Nullable
    private List<T> mPrevList = null;

    private final Set<Integer> mIdsToUpdate = new HashSet<>();

    protected abstract List<T> getList();

    protected abstract boolean isError();

    protected abstract void doLoadList();

    protected abstract void doLoadItem(int id);

    protected abstract Observable<Void> removeItem(int id);

    @Override
    public void onViewReady() {
        super.onViewReady();
        getView().enableUI();
        LogUtils.d("ListPresenter onViewReady mIdsToUpdate == " + mIdsToUpdate);
        if (!mIdsToUpdate.isEmpty()) {
            for (int i = 0; i < getList().size(); i++) {
                if (mIdsToUpdate.contains(getList().get(i).getId())) {
                    getView().updateItem(i);
                    mIdsToUpdate.remove(getList().get(i).getId());
                    if (mIdsToUpdate.isEmpty()) {
                        break;
                    }
                }
            }
        }
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
            List<T> list = getList();
            if (list == null || list.isEmpty()) {
                view.showEmpty();
            } else {
                view.showList(list);
            }
        }
    }

    public void onItemAtPositionClicked(int position) {
        List<T> list = getList();
        if (list == null) {
            return;
        }
        if (mSelectedIds.isEmpty()) {
            getView().disableUI();
            getView().moveToDetailsForItem(list.get(position).getId(), false, -1);
        } else {
            toggleItemSelectionState(position);
        }
    }

    public void onItemAtPositionLongClicked(int position) {
        toggleItemSelectionState(position);
    }

    private void toggleItemSelectionState(int position) {
        int id = getList().get(position).getId();
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
        for (T t : getList()) {
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
        mPrevList = new ArrayList<>(getList());
        Iterator<T> each = getList().iterator();
        while (each.hasNext()) {
            int id = each.next().getId();
            if (mSelectedIds.contains(id)) {
                each.remove();
                removeItem(id)
                        .subscribe(aLong -> {
                        }, LogUtils::e);
            }
        }
        getView().showItemsDeleted(mSelectedIds.size());
        mSelectedIds.clear();
        updateView();
        getView().stopListSelectionMode();
    }

    public void onUndoDeletionClicked() {
        if (mPrevList != null) {
            getList().clear();
            getList().addAll(mPrevList);
            mPrevList = null;
            updateView();
        }
    }

    public void onItemDismissed(int position) {
//        askDeleteItem(position);

        removeItem(getList().get(position).getId())
                .subscribe(aLong -> {
                }, LogUtils::e);

        if (mSelectedIds.contains(getList().get(position).getId())) {
            toggleItemSelectionState(position);
        }

        getList().remove(position);
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
        getView().enableUI();
    }

    public void onScrollToTopClicked() {
        getView().scrollToTop();
    }

    protected final void updateItem(int position) {
        if (canUpdateView()) {
            getView().updateItem(position);
        } else {
            mIdsToUpdate.add(getList().get(position).getId());
        }
    }

    private void askDeleteItem(int position) {
        List<T> list = getList();
        if (list == null) {
            return;
        }
        getView().askDeleteItem(list.get(position).getId());
        getView().setItemSelectionState(position, true);
    }

}
