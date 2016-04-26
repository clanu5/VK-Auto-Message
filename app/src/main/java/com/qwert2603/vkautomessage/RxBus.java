package com.qwert2603.vkautomessage;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    public static final int EVENT_USERS_PHOTO_UPDATED = 1;

    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o) {
        mBus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }

}
