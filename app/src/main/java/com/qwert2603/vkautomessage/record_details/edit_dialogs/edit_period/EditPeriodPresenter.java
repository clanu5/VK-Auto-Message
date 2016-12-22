package com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_period;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.Record;

public class EditPeriodPresenter extends BasePresenter<Integer, EditPeriodView> {

    public EditPeriodPresenter() {
    }

    public void setPeriod(int period) {
        setModel(period);
    }

    @Override
    protected void onUpdateView(@NonNull EditPeriodView view) {
    }

    public int getSelectedPeriodPosition() {
        Integer period = getModel();
        if (period == null) {
            return -1;
        }
        int selectedPos = -1;
        for (int i = 0; i < Record.PERIODS.length; i++) {
            if (Record.PERIODS[i] == period) {
                selectedPos = i;
            }
        }
        return selectedPos;
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
