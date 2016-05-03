package com.qwert2603.vkautomessage.edit_time;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.base.BasePresenter;

public class EditTimePresenter extends BasePresenter<Integer, EditTimeView> {

    public EditTimePresenter() {
    }

    public void setMinuteAtDay(int minuteAtDay) {
        setModel(minuteAtDay);
    }

    @Override
    protected void onUpdateView(@NonNull EditTimeView view) {
    }

    public int getHours() {
        return getModel() / Const.MINUTES_PER_HOUR;
    }

    public int getMinutes() {
        return getModel() % Const.MINUTES_PER_HOUR;
    }

    void onSubmitClicked(int hours, int minutes) {
        setModel(hours * Const.MINUTES_PER_HOUR + minutes);
        getView().submitDone(getModel());
    }
}
