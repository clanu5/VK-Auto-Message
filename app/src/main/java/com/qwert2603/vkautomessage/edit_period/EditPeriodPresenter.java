package com.qwert2603.vkautomessage.edit_period;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;

public class EditPeriodPresenter extends BasePresenter<Integer, EditPeriodView> {

    public EditPeriodPresenter() {
    }

    public void setPeriod(int period) {
        setModel(period);
    }

    @Override
    protected void onUpdateView(@NonNull EditPeriodView view) {
        Integer model = getModel();
        if (model == null) {
            return;
        }
        view.setPeriod(model);
    }

    public void onPeriodChanged(int period) {
        setModel(period);
    }

    void onSubmitClicked() {
        Integer model = getModel();
        if (model == null) {
            return;
        }
        getView().submitDone(model);
    }

}
