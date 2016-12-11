package com.qwert2603.vkautomessage.base.in_out_animation;

import com.qwert2603.vkautomessage.base.navigation.NavigationView;

/**
 * Представление, которое может показывать анимацию появления и исчезновения элементов UI,
 * а также входа на экран и выхода из него.
 */
public interface AnimationView extends NavigationView {
    void animateEnter();
    void animateIn(boolean withLargeDelay);
    void animateOut();
    void animateExit();
}
