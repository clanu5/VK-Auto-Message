package com.qwert2603.vkautomessage.edit_message;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;

public class EditMessagePresenter extends BasePresenter<String, EditMessageView> {

    public EditMessagePresenter() {
    }

    public void setMessage(String message) {
        setModel(message);
    }

    @Override
    protected void onUpdateView(@NonNull EditMessageView view) {
        String model = getModel();
        if (model == null) {
            return;
        }
        view.setMessage(model);
    }

    public void onMessageEdited(String message) {
        if (! message.equals(getModel())) {
            setModel(message);
        }
    }

    void onSubmitClicked() {
        String model = getModel();
        if (model == null) {
            return;
        }
        getView().submitDone(model);
    }
}
