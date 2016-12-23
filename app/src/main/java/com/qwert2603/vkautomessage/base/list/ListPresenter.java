package com.qwert2603.vkautomessage.base.list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.Identifiable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Презентер для view списка.
 *
 * @param <T> тип элемента списка
 * @param <M> тип модели
 * @param <V> тип представления
 */
public abstract class ListPresenter<T extends Identifiable, M, V extends ListView<T>> extends BasePresenter<M, V> {

    private Set<Integer> mSelectedIds = new HashSet<>();

    protected abstract List<T> getList();

    protected abstract boolean isError();

    protected abstract void doLoadList();

    protected abstract void doLoadItem(int id);

    @Override
    public void onViewReady() {
        super.onViewReady();
        getView().enableUI();
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

    public void onActionModeCancelled() {
        mSelectedIds.clear();
        getView().showSelectedItemsCount(0);
        getView().unSelectAllItems();
    }

    public void onItemDismissed(int position) {
        askDeleteItem(position);
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

    private void askDeleteItem(int position) {
        List<T> list = getList();
        if (list == null) {
            return;
        }
        getView().askDeleteItem(list.get(position).getId());
        getView().setItemSelectionState(position, true);
    }

}
