package com.qwert2603.vkautomessage.base.navigation;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;

import java.util.Map;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class NavigationPresenter extends BasePresenter<User, NavigationView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();
    private Subscription mRxBusSubscription = Subscriptions.unsubscribed();

    /**
     * Потому что Dagger не может инжектить в NavigationPresenter, который generic.
     */
    public static final class InjectionsHolder {
        @Inject
        DataManager mDataManager;

        @Inject
        RxBus mRxBus;

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

        mRxBusSubscription = mInjectionsHolder.mRxBus.toObservable()
                .filter(event -> event.mEvent == RxBus.Event.EVENT_USERS_VK_DATA_UPDATED)
                .map(event -> (Map<Integer, User>) event.mObject)
                .subscribe(integerUserMap -> {
                    User user = getModel();
                    if (integerUserMap.containsKey(user.getId())) {
                        user.setVkDataFrom(integerUserMap.get(user.getId()));
                        updateView();
                    }
                }, LogUtils::e);
    }

    @Override
    protected void onUpdateView(@NonNull NavigationView view) {
        User user = getModel();
        if (user != null) {
            view.showMyselfName(StringUtils.getUserName(user));
            view.showMyselfPhoto(user.getPhoto(), StringUtils.getUserInitials(user));
        } else {
            view.showLoadingMyself();
        }
    }

    @Override
    public void unbindView() {
        mRxBusSubscription.unsubscribe();
        mRxBusSubscription = Subscriptions.unsubscribed();
        mSubscription.unsubscribe();
        super.unbindView();
    }

    public void onLogOutClicked() {
        mInjectionsHolder.mDataManager.logOutVk();
        getView().performLogOut();
    }

    private void loadMyselfUser() {
        mSubscription.unsubscribe();
        mSubscription = mInjectionsHolder.mDataManager.getUserMyself()
                .subscribe(
                        model -> NavigationPresenter.this.setModel(model),
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }

}
