package com.qwert2603.vkautomessage.navigation;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;

public class DrawerPresenter extends BasePresenter<Object, DrawerView> {
    @Override
    protected void onUpdateView(@NonNull DrawerView view) {
    }

    public void onSettingsClicked() {
        getView().showSettings();
    }

    public void onLogOutClicked() {
        DataManager.getInstance().logOutVk();
        getView().showLogOut();
    }
}
