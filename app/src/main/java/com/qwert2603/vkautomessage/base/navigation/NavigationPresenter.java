package com.qwert2603.vkautomessage.base.navigation;

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

public class NavigationPresenter<M, V extends NavigationView> extends BasePresenter<M, V> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    /**
     * Потому что Dagger не может инжектить в NavigationPresenter, который generic.
     */
    public static final class DataManagerHolder {
        @Inject
        DataManager mDataManager;

        DataManagerHolder() {
            VkAutoMessageApplication.getAppComponent().inject(DataManagerHolder.this);
        }
    }

    private DataManagerHolder mDataManagerHolder;

    private User mUser;

    public NavigationPresenter() {
        mDataManagerHolder = new DataManagerHolder();
    }

    @Override
    public void bindView(V view) {
        super.bindView(view);
        if (getModel() == null && mSubscription.isUnsubscribed()) {
            loadMyselfUser();
        }
    }

    @Override
    protected void onUpdateView(@NonNull V view) {
        if (mUser != null) {
            view.showUserName(getUserName(mUser));
            ImageView userPhotoImageView = view.getUserPhotoImageView();
            if (userPhotoImageView != null) {
                ImageLoader.getInstance().displayImage(mUser.getPhoto(), userPhotoImageView);
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
        mDataManagerHolder.mDataManager.logOutVk();
        getView().showLogOut();
    }

    public void onDrawerSlide(int drawerWidth, float slideOffset) {
        getView().setContentTranslationX((drawerWidth * slideOffset) / 2.6f);
    }

    private void loadMyselfUser() {
        mSubscription.unsubscribe();
        mSubscription = mDataManagerHolder.mDataManager
                .getUserMyself()
                .subscribe(
                        user -> {
                            mUser = user;
                            updateView();
                        },
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }

    public void onActionModeCancelled() {
    }

    public void onBackPressed() {
        getView().performBackPressed();
    }
}
