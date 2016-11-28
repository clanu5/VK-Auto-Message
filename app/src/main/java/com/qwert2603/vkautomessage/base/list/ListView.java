package com.qwert2603.vkautomessage.base.list;

import com.qwert2603.vkautomessage.base.in_out_animation.InOutAnimationView;
import com.qwert2603.vkautomessage.model.Identifiable;

import java.util.List;

/**
 * Представление списка для шаблона MVP.
 * Поддерживает in/out анимации и появление списка.
 *
 * @param <T> тип элемента списка.
 */
public interface ListView<T extends Identifiable> extends InOutAnimationView {
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

    void moveToDetailsForItem(int id);

    /**
     * Спросить у пользователя подтверждение удаления элемента с переданным id.
     *
     * @param id id элемента для удаления.
     */
    void askDeleteItem(int id);

    void showItemSelected(int position);

    void notifyItemRemoved(int position);

    void notifyItemInserted(int position, int id);

    void scrollListToTop();

    void scrollListToBottom();

    void scrollToPosition(int position);

    void animateAllItemsEnter(boolean animate);

    void delayEachItemEnterAnimation(boolean delay);

}
