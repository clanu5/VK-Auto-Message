package com.qwert2603.vkautomessage.base.list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.in_out_animation.InOutAnimationPresenter;
import com.qwert2603.vkautomessage.base.in_out_animation.ShouldCheckIsInningOrInside;
import com.qwert2603.vkautomessage.model.Identifiable;

import java.util.List;

/**
 * Презентер для view списка с поддержкой in/out анимации и появление списка.
 * <p>
 * По умолчанию:
 * - при нажатии на элемент происходит out-анимация и последующий переход к подробностям об этом элементе.
 * <p>
 * - при долгом нажатии и свайпе запрашивается подтверждение на удаление.
 * после запроса ListView должно вызвать {@link #onItemDeleteSubmitted(int)} или {@link #onItemDeleteCanceled(int)}
 * элемент для удаления выделяется пока не будет получен результат запроса на удаление.
 *
 * @param <T> тип элемента списка
 * @param <M> тип модели
 * @param <V> тип представления
 */
public abstract class ListPresenter<T extends Identifiable, M, V extends ListView<T>> extends InOutAnimationPresenter<M, V> {

    private enum AnimationState {
        WAITING_FOR_TRIGGER,
        SHOULD_START,
        STARTED
    }

    private AnimationState mListEnterAnimationState = AnimationState.WAITING_FOR_TRIGGER;

    protected abstract List<T> getList();

    protected abstract boolean isError();

    protected abstract void doLoadList();

    @Override
    protected void onUpdateView(@NonNull V view) {
        if (getModel() == null) {
            if (isError()) {
                view.showError();
            } else {
                view.showLoading();
            }
        } else {
            if (mListEnterAnimationState != AnimationState.WAITING_FOR_TRIGGER) {
                List<T> list = getList();
                if (list == null || list.isEmpty()) {
                    view.showEmpty();
                } else {
                    if (mListEnterAnimationState == AnimationState.SHOULD_START) {
                        mListEnterAnimationState = AnimationState.STARTED;
                        view.animateAllItemsEnter(true);
                        view.delayEachItemEnterAnimation(true);
                        view.showListEnter(list);
                    } else {
                        view.showList(list);
                    }
                }
            }
        }
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
    public void onAnimateOutFinished(int id) {
        super.onAnimateOutFinished(id);
        getView().moveToDetailsForItem(id);
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
        getView().scrollToPosition(position);
        animateOut(list.get(position).getId());
    }

    @ShouldCheckIsInningOrInside
    public void onItemAtPositionLongClicked(int position) {
        if (!isInningOrInside()) {
            return;
        }
        askDeleteItem(position);
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
        getView().showItemSelected(-1);
    }

    public void onItemDeleteCanceled(int id) {
        getView().showItemSelected(-1);
    }

    public void onReloadList() {
        doLoadList();
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
        getView().showItemSelected(position);
    }

}
