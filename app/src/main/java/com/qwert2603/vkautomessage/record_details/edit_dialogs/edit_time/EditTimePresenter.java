package com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_time;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;

public class EditTimePresenter extends BasePresenter<EditTimePresenter.Time, EditTimeView> {

    static class Time {
        public int mHour = 19;
        public int mMinute = 18;
    }

    public EditTimePresenter() {
        setModel(new Time());
    }

    public void setHour(int hour) {
        getModel().mHour = hour;
    }

    public void setMinute(int minute) {
       getModel().mMinute = minute;
    }

    @Override
    protected void onUpdateView(@NonNull EditTimeView view) {
    }

    public int getHours() {
        return getModel().mHour;
    }

    public int getMinutes() {
        return getModel().mMinute;
    }

    void onSubmitClicked(int hours, int minutes) {
        getModel().mHour = hours;
        getModel().mMinute = minutes;
        getView().submitDone(getModel().mHour, getModel().mMinute);
    }
}
