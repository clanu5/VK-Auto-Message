package com.qwert2603.vkautomessage.navigation;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;

public class NavigationPresenter extends BasePresenter<User, NavigationView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    public NavigationPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(NavigationPresenter.this);
    }

    @Override
    public void bindView(NavigationView view) {
        super.bindView(view);
        if (getModel() == null && mSubscription.isUnsubscribed()) {
            loadMyselfUser();
        }
    }

    @Override
    protected void onUpdateView(@NonNull NavigationView view) {
        User user = getModel();
        if (user != null) {
            view.showUserName(getUserName(user));
            ImageView userPhotoImageView = view.getUserPhotoImageView();
            if (userPhotoImageView != null) {
                ImageLoader.getInstance().displayImage(user.getPhoto(), userPhotoImageView);
            }
        } else {
            view.showLoading();
        }
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    public void onLogOutClicked() {
        mDataManager.logOutVk();
        getView().showLogOut();
    }

    private void loadMyselfUser() {
        mSubscription.unsubscribe();
        mSubscription = mDataManager
                .getUserMyself()
                .subscribe(
                        user -> NavigationPresenter.this.setModel(user),
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }
}
