package com.qwert2603.vkautomessage.base.in_out_animation;

import com.qwert2603.vkautomessage.base.BaseView;

/**
 * Представление, которое может показывать анимацию появления и исчезновения элементов UI.
 */
public interface InOutAnimationView extends BaseView {
    void animateIn(boolean withLargeDelay);
    void animateOut(int id);
    void prepareForIn();
    void performBackPressed();
}
