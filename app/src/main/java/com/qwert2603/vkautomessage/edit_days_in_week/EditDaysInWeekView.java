package com.qwert2603.vkautomessage.edit_days_in_week;

import com.qwert2603.vkautomessage.base.BaseView;

public interface EditDaysInWeekView extends BaseView {
    void setDayInWeekEnable(int dayInWeek, boolean enable);
    void submitDone(int daysInWeek);
}
