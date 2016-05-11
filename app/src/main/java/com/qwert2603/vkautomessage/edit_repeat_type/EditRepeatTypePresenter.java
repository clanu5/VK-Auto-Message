package com.qwert2603.vkautomessage.edit_repeat_type;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;

public class EditRepeatTypePresenter extends BasePresenter<Integer, EditRepeatTypeView> {

    public EditRepeatTypePresenter() {
    }

    public void setRepeatType(int repeatType) {
        setModel(repeatType);
    }

    @Override
    protected void onUpdateView(@NonNull EditRepeatTypeView view) {
        Integer model = getModel();
        if (model == null) {
            return;
        }
        view.setRepeatType(model);
    }

    public void onRepeatTypeChanged(int repeatType) {
        setModel(repeatType);
    }

    void onSubmitClicked() {
        Integer model = getModel();
        if (model == null) {
            return;
        }
        getView().submitDone(model);
    }

}
