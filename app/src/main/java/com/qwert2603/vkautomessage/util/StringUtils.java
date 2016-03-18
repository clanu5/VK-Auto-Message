package com.qwert2603.vkautomessage.util;

import com.vk.sdk.api.model.VKApiUserFull;

public final class StringUtils {

    /**
     * Обрезать строку до нужной длины.
     */
    public static String noMore(String s, int maxLength) {
        return s.length() <= maxLength ? s : s.substring(0, maxLength - 2) + "..";
    }

    /**
     * Полное имя пользователя.
     */
    public static String getUserName(VKApiUserFull user) {
        return user.first_name + " " + user.last_name;
    }

}
