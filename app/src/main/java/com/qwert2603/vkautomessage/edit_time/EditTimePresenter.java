package com.qwert2603.vkautomessage.edit_time;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;

import java.util.Calendar;
import java.util.Date;

public class EditTimePresenter extends BasePresenter<Calendar, EditTimeView> {

    public EditTimePresenter() {
    }

    public void setTimeInMillis(Long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeInMillis));
        setModel(calendar);
    }

    @Override
    protected void onUpdateView(@NonNull EditTimeView view) {
    }

    public int getHours() {
        Calendar calendar = getModel();
        return calendar == null ? 19 : calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        Calendar calendar = getModel();
        return calendar == null ? 18 : calendar.get(Calendar.MINUTE);
    }

    void onSubmitClicked(int hours, int minutes) {
        Calendar calendar = getModel();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        getView().submitDone(calendar.getTimeInMillis());
    }
}
