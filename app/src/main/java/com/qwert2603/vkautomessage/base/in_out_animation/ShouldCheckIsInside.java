package com.qwert2603.vkautomessage.base.in_out_animation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Аннотация для метода, который должен выполниться только тогда,
 * когда выполнилась in-анимация, а out-анимация еще не началась.
 *
 * Проверка осуществляется с помощью метода.
 * {@link InOutAnimationPresenter#isInside()}
 */
@Target(ElementType.METHOD)
public @interface ShouldCheckIsInside {
}
