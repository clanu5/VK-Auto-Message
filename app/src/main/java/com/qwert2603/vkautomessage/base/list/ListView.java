package com.qwert2603.vkautomessage.base.list;

import com.qwert2603.vkautomessage.base.BaseView;
import com.qwert2603.vkautomessage.model.Identifiable;

import java.util.List;

/**
 * Представление списка для шаблона MVP.
 * Поддерживает in/out анимации и появление списка.
 *
 * @param <T> тип элемента списка.
 */
public interface ListView<T extends Identifiable> extends BaseView {
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

    void moveToDetailsForItem(T item);

    /**
     * Спросить у пользователя подтверждение удаления элемента с переданным id.
     *
     * @param id id элемента для удаления.
     */
    void askDeleteItem(int id);

    void startListSelectionMode();
    void stopListSelectionMode();
    void setItemSelectionState(int position, boolean select);
    void selectAllItems();
    void unSelectAllItems();

    void notifyItemRemoved(int position);
    void notifyItemInserted(int position, int id);
    void notifyItemsUpdated(List<Integer> updatedUserPositions);

    void scrollToPosition(int position);

}
