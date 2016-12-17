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

public class NavigationPresenter extends BasePresenter<User, NavigationView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    /**
     * Потому что Dagger не может инжектить в NavigationPresenter, который generic.
     */
    public static final class InjectionsHolder {
        @Inject
        DataManager mDataManager;

        InjectionsHolder() {
            VkAutoMessageApplication.getAppComponent().inject(InjectionsHolder.this);
        }
    }

    private final InjectionsHolder mInjectionsHolder;

    public NavigationPresenter() {
        mInjectionsHolder = new InjectionsHolder();
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
            view.showMyselfName(getUserName(user));
            ImageView myselfPhotoImageView = view.getMyselfPhotoImageView();
            if (myselfPhotoImageView != null) {
                ImageLoader.getInstance().displayImage(user.getPhoto(), myselfPhotoImageView);
            }
        } else {
            view.showLoadingMyself();
        }
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    public void onLogOutClicked() {
        mInjectionsHolder.mDataManager.logOutVk();
        getView().performLogOut();
    }

    private void loadMyselfUser() {
        mSubscription.unsubscribe();
        mSubscription = mInjectionsHolder.mDataManager
                .getUserMyself()
                .subscribe(
                        NavigationPresenter.this::setModel,
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }

}
