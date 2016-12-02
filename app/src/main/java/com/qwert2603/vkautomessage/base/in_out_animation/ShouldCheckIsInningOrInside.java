package com.qwert2603.vkautomessage.base.in_out_animation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Аннотация для метода, который должен выполниться только тогда,
 * когда выполняется или выполнилась in-анимация, а out-анимация еще не началась.
 *
 * Проверка осуществляется с помощью метода.
 * {@link AnimationPresenter#isInningOrInside()}
 *
 */
// TODO: 28.11.2016 сделать через AnnotationProcessing
@Target(ElementType.METHOD)
public @interface ShouldCheckIsInningOrInside {
}
