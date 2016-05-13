package com.qwert2603.vkautomessage.edit_days_in_week;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.base.BasePresenter;

public class EditDaysInWeekPresenter extends BasePresenter<Integer, EditDaysInWeekView> {

    public EditDaysInWeekPresenter() {
    }

    public void setDaysInWeek(int daysInWeek) {
        setModel(daysInWeek);
    }

    @Override
    protected void onUpdateView(@NonNull EditDaysInWeekView view) {
        Integer model = getModel();
        if (model == null) {
            return;
        }
        for (int i = 1; i < Const.DAYS_PER_WEEK + 1; i++) {
            boolean enable = (model & (1 << i)) != 0;
            view.setDayInWeekEnable(i, enable);
        }
    }

    public void onDayInWeekEnableChanged(int dayInWeek, boolean enable) {
        Integer model = getModel();
        int daysOfWeek = model != null ? model : 0;
        if (((daysOfWeek & (1 << dayInWeek)) != 0) == enable) {
            return;
        }
        if (enable) {
            daysOfWeek |= 1 << dayInWeek;
        } else {
            daysOfWeek &= ~(1 << dayInWeek);
        }
        setModel(daysOfWeek);
    }

    void onSubmitClicked() {
        Integer model = getModel();
        if (model == null) {
            return;
        }
        getView().submitDone(model);
    }

}
