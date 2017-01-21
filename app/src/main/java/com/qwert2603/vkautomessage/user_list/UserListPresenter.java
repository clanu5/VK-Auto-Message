package com.qwert2603.vkautomessage.user_list;

import android.os.SystemClock;

import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.list.ListPresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class UserListPresenter extends ListPresenter<User, List<User>, UserListView> {

    public enum SortState {
        DEFAULT,
        FIRST_NAME,
        LAST_NAME,
        RECORDS_COUNT,
        ENABLED_RECORDS_COUNT
    }

    private Subscription mSubscription = Subscriptions.unsubscribed();
    private Subscription mRxBusSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    @Inject
    RxBus mRxBus;

    private SortState mSortState = SortState.DEFAULT;

    public UserListPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(UserListPresenter.this);
    }

    @Override
    protected Transformer<List<User>, List<User>> listFromModel() {
        return users -> users;
    }

    @Override
    protected Transformer<List<User>, List<User>> showingListFromModel() {
        return users -> {
            if (users == null) {
                return null;
            }
            if (mSortState == SortState.DEFAULT) {
                return users;
            }
            long b = SystemClock.elapsedRealtime();
            List<User> showingList = new ArrayList<>(users);
            switch (mSortState) {
                case FIRST_NAME:
                    Collections.sort(showingList, (o1, o2) -> o1.getFirstName().compareTo(o2.getFirstName()));
                    break;
                case LAST_NAME:
                    Collections.sort(showingList, (o1, o2) -> o1.getLastName().compareTo(o2.getLastName()));
                    break;
                case RECORDS_COUNT:
                    Collections.sort(showingList, (o1, o2) -> Integer.compare(o1.getRecordsCount(), o2.getRecordsCount()));
                    break;
                case ENABLED_RECORDS_COUNT:
                    Collections.sort(showingList, (o1, o2) -> Integer.compare(o1.getEnabledRecordsCount(), o2.getEnabledRecordsCount()));
                    break;
            }
            long time = SystemClock.elapsedRealtime() - b;
            LogUtils.d("showingListFromModel sorting " + time + " ms");
            return showingList;
        };
    }

    @Override
    protected boolean isError() {
        return getModel() == null && mSubscription.isUnsubscribed();
    }

    @Override
    public void bindView(UserListView view) {
        super.bindView(view);
        if (getModel() == null && mSubscription.isUnsubscribed()) {
            doLoadList();
        }
        mRxBusSubscription = mRxBus.toObservable()
                .filter(event -> event.mEvent == RxBus.Event.EVENT_USERS_VK_DATA_UPDATED)
                .subscribe(event -> {
                    List<User> userList = getModel();
                    Map<Integer, User> updatedUsers = (Map<Integer, User>) event.mObject;
                    if (userList == null || updatedUsers == null) {
                        return;
                    }
                    List<Integer> updatedPositions = new ArrayList<>();
                    for (int i = 0; i < userList.size(); i++) {
                        int id = userList.get(i).getId();
                        if (updatedUsers.containsKey(id)) {
                            userList.get(i).setVkDataFrom(updatedUsers.get(id));
                            updatedPositions.add(i);
                        }
                    }
                    UserListView view1 = getView();
                    if (view1 == null || updatedPositions.isEmpty()) {
                        return;
                    }
                    updateShowingList();
                }, LogUtils::e);
    }

    @Override
    public void unbindView() {
        mRxBusSubscription.unsubscribe();
        mRxBusSubscription = Subscriptions.unsubscribed();
        mSubscription.unsubscribe();
        super.unbindView();
    }

    @Override
    protected void doLoadList() {
        mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllUsers()
                .subscribe(
                        model -> UserListPresenter.this.setModel(model),
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }

    @Override
    protected void doLoadItem(int id) {
        mSubscription.unsubscribe();
        mSubscription = mDataManager.getUserById(id)
                .subscribe(
                        user -> {
                            List<User> userList = getModel();
                            if (userList == null) {
                                return;
                            }
                            int userPosition = getItemPosition(user.getId());
                            if (userPosition != -1) {
                                userList.set(userPosition, user);
                                if (canUpdateView()) {
                                    getView().updateItem(getShowingItemPosition(user.getId()), user);
                                }
                                updateShowingList();
                            }
                        },
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }

    @Override
    protected Observable<Void> removeItem(int id) {
        return mDataManager.removeUser(id);
    }

    public void onItemDeleteSubmitted(int id) {
        super.onItemDeleteSubmitted(id);
        int position = getItemPosition(id);
        List<User> userList = getModel();
        userList.remove(position);
        updateShowingList();

        mDataManager.removeUser(id)
                .subscribe(aVoid -> {
                }, LogUtils::e);
    }

    public void onChooseUserClicked() {
        getView().showChooseUser();
    }

    /**
     * @param userId id of chosen user.
     *               If user not chosen should be negative.
     */
    public void onUserChosen(int userId) {
        if (userId < 0) {
            return;
        }
        int userPosition = getItemPosition(userId);
        if (userPosition >= 0) {
            List<User> userList = getModel();
            UserListView view = getView();
            if (userList == null || view == null) {
                return;
            }
            int showingItemPosition = getShowingItemPosition(userId);
            getView().moveToDetailsForItem(userId, showingItemPosition >= 0, showingItemPosition);
        } else {
            mDataManager.getVkUserById(userId, true)
                    .flatMap(mDataManager::addUser)
//                    .doOnNext(user -> {
//                        for (int i = 0; i < 1300; i++) {
//                            Record record = new Record(userId);
//                            record.setEnabled(true);
//                            record.setHour(new Random().nextInt(24));
//                            record.setMinute(new Random().nextInt(60));
//                            record.setRepeatType(new Random().nextInt(3));
//                            mDataManager.addRecord(record).subscribe();
//                        }
//                    })
                    .subscribe(user -> {
                        List<User> userList = getModel();
                        UserListView view = getView();
                        if (userList == null || view == null) {
                            return;
                        }

                        user.setRecordsCount(0);
                        user.setEnabledRecordsCount(0);

                        userList.add(user);
                        updateShowingList();

                        int showingItemPosition = getShowingItemPosition(userId);
                        view.moveToDetailsForItem(userId, showingItemPosition >= 0, showingItemPosition);
                    }, LogUtils::e);
        }
    }

    public void onSortStateChanged(SortState sortState) {
        if (sortState == mSortState) return;
        mSortState = sortState;
        updateShowingList();
        getView().scrollToTop();
    }

    public SortState getSortState() {
        return mSortState;
    }
}
