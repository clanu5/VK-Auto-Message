package com.qwert2603.vkautomessage.util;

import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.User;

import java.util.Locale;
import java.util.Random;

public final class StringUtils {

    private static final Random sRandom = new Random();

    /**
     * Обрезать строку до нужной длины.
     */
    public static String noMore(String s, int maxLength) {
        return s.length() <= maxLength ? s : s.substring(0, maxLength - 2) + "..";
    }

    /**
     * Полное имя пользователя.
     */
    public static String getUserName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    public static String getUserInitials(User user) {
        return user.getFirstName().charAt(0) + "" + user.getLastName().charAt(0);
    }

    /**
     * Время отправки сообщения.
     */
    public static String getRecordTime(Record record) {
        int h = record.getHour();
        int m = record.getMinute();
        String pm = m <= 9 ? "0" : "";
        return h + ":" + pm + m;
    }

    /**
     * @return текст сообщения для новой записи.
     */
    public static String getNewRecordMessage() {
        return String.format(Locale.getDefault(), "Vk Auto Message %x", sRandom.nextInt());
    }

}
