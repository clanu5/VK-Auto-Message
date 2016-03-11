package com.qwert2603.vkautomessage.entities;

import java.util.Calendar;

public class Record {

    private int userId;
    private String message;
    private boolean enabled;

    /**
     * Начало отправки (чч:мм:сс).
     */
    private Calendar start;

    /**
     * Период отправки в мс.
     */
    private int period;


    public Record() {
    }

    public Record(int userId, String message, boolean enabled, Calendar start, int period) {
        this.userId = userId;
        this.message = message;
        this.enabled = enabled;
        this.start = start;
        this.period = period;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
