package com.qwert2603.vkautomessage.di;

import com.qwert2603.vkautomessage.delete_record.DeleteRecordPresenter;
import com.qwert2603.vkautomessage.edit_message.EditMessagePresenter;
import com.qwert2603.vkautomessage.edit_time.EditTimePresenter;
import com.qwert2603.vkautomessage.navigation.NavigationPresenter;
import com.qwert2603.vkautomessage.record_details.RecordPresenter;
import com.qwert2603.vkautomessage.record_list.RecordListPresenter;
import com.qwert2603.vkautomessage.choose_user.ChooseUserPresenter;
import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.user_list.UserListPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModule {

    @Provides
    RecordListPresenter provideRecordListPresenter() {
        return new RecordListPresenter();
    }

    @Provides
    RecordPresenter provideRecordPresenter() {
        return new RecordPresenter();
    }

    @Provides
    ChooseUserPresenter provideChooseUserPresenter() {
        return new ChooseUserPresenter();
    }

    @Provides
    UserListPresenter provideUserListPresenter() {
        return new UserListPresenter();
    }

    @Provides
    UserPresenter provideUserPresenter() {
        return new UserPresenter();
    }

    @Provides
    NavigationPresenter provideNavigationPresenter() {
        return new NavigationPresenter();
    }

    @Provides
    DeleteRecordPresenter provideDeleteRecordPresenter() {
        return new DeleteRecordPresenter();
    }

    @Provides
    EditMessagePresenter provideEditMessagePresenter() {
        return new EditMessagePresenter();
    }

    @Provides
    EditTimePresenter provideEditTimePresenter() {
        return new EditTimePresenter();
    }

}
