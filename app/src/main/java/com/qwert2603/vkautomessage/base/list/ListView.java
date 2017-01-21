package com.qwert2603.vkautomessage.base.list;

import android.support.annotation.NonNull;

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
    void showList(@NonNull List<T> list);

    void showNothingFound();

    void updateItem(int position, T item);

    /**
     * Move to details for given item. (Launch Activity with details, for example).
     *
     * @param newItem      true if moving to just added item and this item is in currently showing list.
     * @param itemPosition position of item. -1 if item is not in currently showing list.
     */
    void moveToDetailsForItem(int itemId, boolean newItem, int itemPosition);

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
}
