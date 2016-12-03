package com.qwert2603.vkautomessage.base.in_out_animation;

import com.qwert2603.vkautomessage.base.BaseView;

/**
 * Представление, которое может показывать анимацию появления и исчезновения элементов UI,
 * а также входа на экран и выхода из него.
 */
public interface AnimationView extends BaseView {
    void animateEnter();
    void animateIn(boolean withLargeDelay);
    void animateOut(int id);
    void animateExit();
    void performBackPressed();
}
