package com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_repeat_type;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.Record;

public class EditRepeatTypePresenter extends BasePresenter<Integer, EditRepeatTypeView> {

    public EditRepeatTypePresenter() {
    }

    @Override
    protected void onUpdateView(@NonNull EditRepeatTypeView view) {
    }

    public void onRepeatTypeChanged(@Record.RepeatType int repeatType) {
        setModel(repeatType);
    }

    void onSubmitClicked() {
        @Record.RepeatType Integer model = getModel();
        if (model == null) {
            return;
        }
        getView().submitDone(model);
    }

}
