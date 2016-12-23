package com.qwert2603.vkautomessage.user_details;

import android.widget.ImageView;

import com.qwert2603.vkautomessage.BaseTest;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.StringUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import javax.inject.Inject;

public class UserPresenterTest extends BaseTest {

    @Inject
    UserView mUserViewMock;

    @Inject
    DataManager mDataManagerMock;

    @Inject
    List<User> mUserList;

    @Before
    public void setUp() {
        getTestComponent().inject(UserPresenterTest.this);
        Mockito.when(mUserViewMock.getPhotoImageView()).thenReturn(Mockito.mock(ImageView.class));
    }

    @Test
    public void testSetUser() {
        User user = mUserList.get(0);
        UserPresenter userPresenter = new UserPresenter();
        userPresenter.setUser(user);
        userPresenter.bindView(mUserViewMock);
        Mockito.verifyZeroInteractions(mUserViewMock);
        userPresenter.onViewReady();

        Mockito.verify(mUserViewMock, Mockito.times(1)).getPhotoImageView();
        Mockito.verify(mUserViewMock, Mockito.times(1)).showName(StringUtils.getUserName(user));
        Mockito.verify(mUserViewMock, Mockito.times(1)).hideRecordsCount();
    }

    @Test
    public void testSetUser2() {
        User user = mUserList.get(0);
        user.setRecordsCount(7);
        user.setEnabledRecordsCount(4);
        UserPresenter userPresenter = new UserPresenter();
        userPresenter.setUser(user);
        userPresenter.bindView(mUserViewMock);
        Mockito.verifyZeroInteractions(mUserViewMock);
        userPresenter.onViewReady();

        Mockito.verify(mUserViewMock, Mockito.times(1)).getPhotoImageView();
        Mockito.verify(mUserViewMock, Mockito.times(1)).showName(StringUtils.getUserName(user));
        Mockito.verify(mUserViewMock, Mockito.times(1)).showRecordsCount(7, 4, false);
    }

    @Test
    public void testOnViewNotReady() {
        User user = mUserList.get(0);
        UserPresenter userPresenter = new UserPresenter();
        userPresenter.setUser(user);
        userPresenter.bindView(mUserViewMock);
        Mockito.verifyZeroInteractions(mUserViewMock);
        userPresenter.onViewReady();

        userPresenter.onViewNotReady();
        Mockito.verify(mUserViewMock, Mockito.times(2)).getPhotoImageView();
    }

}