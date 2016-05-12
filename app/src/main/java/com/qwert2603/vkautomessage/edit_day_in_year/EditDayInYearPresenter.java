package com.qwert2603.vkautomessage.edit_day_in_year;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;

public class EditDayInYearPresenter extends BasePresenter<EditDayInYearPresenter.DayInYear, EditDayInYearView> {

    static class DayInYear {
        public int mMonth = 3;
        public int mDayOfMonth = 26;
    }

    public EditDayInYearPresenter() {
        setModel(new DayInYear());
    }

    public void setMonth(int month) {
        getModel().mMonth = month;
    }

    public void setDayOfMonth(int dayOfMonth) {
       getModel().mDayOfMonth = dayOfMonth;
    }

    @Override
    protected void onUpdateView(@NonNull EditDayInYearView view) {
    }

    public int getMonth() {
        return getModel().mMonth;
    }

    public int getDayOfMonth() {
        return getModel().mDayOfMonth;
    }

    void onSubmitClicked(int month, int dayOfMonth) {
        getModel().mMonth = month;
        getModel().mDayOfMonth = dayOfMonth;
        getView().submitDone(getModel().mMonth, getModel().mDayOfMonth);
    }
}
