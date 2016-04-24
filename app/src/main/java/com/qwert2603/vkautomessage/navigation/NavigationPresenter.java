package com.qwert2603.vkautomessage.navigation;

import android.support.annotation.NonNull;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.api.model.VKApiUser;

import javax.inject.Inject;

import rx.Subscription;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;

public class NavigationPresenter extends BasePresenter<VKApiUser, NavigationView> {

    private Subscription mSubscription;

    @Inject
    DataManager mDataManager;

    public NavigationPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(NavigationPresenter.this);
        loadMyselfUser();
    }

    @Override
    protected void onUpdateView(@NonNull NavigationView view) {
        VKApiUser user = getModel();
        if (user != null) {
            view.showUserName(getUserName(user));
            ImageLoader.getInstance().displayImage(user.photo_200, view.getUserPhotoImageView());
        } else {
            view.showLoading();
        }
    }

    @Override
    public void unbindView() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.unbindView();
    }

    public void onSettingsClicked() {
        getView().showSettings();
    }

    public void onLogOutClicked() {
        mDataManager.logOutVk();
        getView().showLogOut();
    }

    private void loadMyselfUser() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = mDataManager
                .getVkUserMyself()
                .subscribe(
                        user -> NavigationPresenter.this.setModel(user),
                        throwable -> {
                            if (mSubscription != null) {
                                mSubscription.unsubscribe();
                                mSubscription = null;
                            }
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }
}
