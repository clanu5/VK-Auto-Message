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

    void notifyItemChanged(int position);

    /**
     * Move to details for given item. (Launch Activity with details, for example).
     *
     * @param newItem         true if moving to just added item.
     * @param newItemPosition position of just added item if newItem==true.
     *                        if newItem==false then newItemPosition is undefined.
     */
    void moveToDetailsForItem(int itemId, boolean newItem, int newItemPosition);

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
    void showSelectedItemsCount(int count);

    void showItemsDeleted(int count);

    void scrollToTop();

    /**
     * Enable clicking in list items and buttons.
     * UI should be enabled by default.
     */
    void enableUI();

    /**
     * Disable clicking in list items and buttons.
     */
    void disableUI();
}
