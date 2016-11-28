package com.qwert2603.vkautomessage.base.delete_item;

import com.qwert2603.vkautomessage.base.BasePresenter;

public abstract class DeleteItemPresenter<M, V extends DeleteItemView> extends BasePresenter<M, V> {

    private static final int NO_ID = -1;

    protected int mId = NO_ID;

    public void onSubmitClicked() {
        getView().submitResult(mId != NO_ID, mId);
    }

    public void onCancelClicked() {
        getView().submitResult(false, mId);
    }

}
