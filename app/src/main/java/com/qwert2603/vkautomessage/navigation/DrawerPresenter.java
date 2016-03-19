package com.qwert2603.vkautomessage.navigation;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;

public class DrawerPresenter extends BasePresenter<Object, DrawerView> {
    @Override
    protected void onUpdateView(@NonNull DrawerView view) {
    }
    
    public void onNavigationClicked() {
        getView().openDrawer();
    }

    public void onSettingsClicked() {
        getView().showSettings();
    }

    public void onLogOutClicked() {
        getView().logOut();
    }
}
