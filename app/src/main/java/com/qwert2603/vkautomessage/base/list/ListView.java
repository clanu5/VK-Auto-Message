package com.qwert2603.vkautomessage.base.list;

import com.qwert2603.vkautomessage.base.in_out_animation.AnimationView;
import com.qwert2603.vkautomessage.model.Identifiable;

import java.util.List;

/**
 * Представление списка для шаблона MVP.
 * Поддерживает in/out анимации и появление списка.
 *
 * @param <T> тип элемента списка.
 */
public interface ListView<T extends Identifiable> extends AnimationView {
    /**
     * Отобразить сообщение о загрузке.
     */
    void showLoading();

    /**
     * Отобразить сообщение об ошибке загрузки.
     */
    void showError();

    /**
     * Отобразить сообщение о том, что список пуст.
     */
    void showEmpty();

    /**
     * Отобразить список.
     *
     * @param list список для отображения.
     */
    void showList(List<T> list);

    /**
     * Отобразить появление списка.
     *
     * @param list список для появления.
     */
    void showListEnter(List<T> list);

    void moveToDetailsForItem(int id, boolean withSetPressed);

    void moveToDetailsForItem(T item, boolean withSetPressed);

    /**
     * Спросить у пользователя подтверждение удаления элемента с переданным id.
     *
     * @param id id элемента для удаления.
     */
    void askDeleteItem(int id);

    void setItemSelectionState(int position, boolean select);

    void selectAllItems();

    void unSelectAllItems();

    void startListSelectionMode();

    void stopListSelectionMode();

    void notifyItemRemoved(int position);

    void notifyItemInserted(int position, int id);

    void notifyItemsUpdated(List<Integer> updatedUserPositions);

    void scrollListToTop();

    void smoothScrollListToBottom();

    void smoothScrollToPosition(int position);

    void scrollToPosition(int position);

    void animateAllItemsEnter(boolean animate);

    void delayEachItemEnterAnimation(boolean delay);

    void animateInNewItemButton(int delay);

    int getItemEnterDelayPerScreen();

    int getLastCompletelyVisibleItemPosition();

}
