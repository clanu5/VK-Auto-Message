package com.qwert2603.vkautomessage.presenter;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.view.DrawerView;

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
