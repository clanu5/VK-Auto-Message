package com.qwert2603.vkautomessage.di;

import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.choose_user.ChooseUserAdapter;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordDialog;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordPresenter;
import com.qwert2603.vkautomessage.delete_user.DeleteUserDialog;
import com.qwert2603.vkautomessage.delete_user.DeleteUserPresenter;
import com.qwert2603.vkautomessage.edit_message.EditMessageDialog;
import com.qwert2603.vkautomessage.edit_message.EditMessagePresenter;
import com.qwert2603.vkautomessage.edit_time.EditTimeDialog;
import com.qwert2603.vkautomessage.helper.PreferenceHelper;
import com.qwert2603.vkautomessage.helper.SendMessageHelper;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.navigation.NavigationActivity;
import com.qwert2603.vkautomessage.navigation.NavigationPresenter;
import com.qwert2603.vkautomessage.receiver.BootCompletedReceiver;
import com.qwert2603.vkautomessage.record_details.RecordFragment;
import com.qwert2603.vkautomessage.record_details.RecordPresenter;
import com.qwert2603.vkautomessage.record_list.RecordListAdapter;
import com.qwert2603.vkautomessage.record_list.RecordListFragment;
import com.qwert2603.vkautomessage.record_list.RecordListPresenter;
import com.qwert2603.vkautomessage.user_list.UserListAdapter;
import com.qwert2603.vkautomessage.choose_user.ChooseUserDialog;
import com.qwert2603.vkautomessage.service.SendMessageService;
import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.choose_user.ChooseUserPresenter;
import com.qwert2603.vkautomessage.user_list.UserListFragment;
import com.qwert2603.vkautomessage.user_list.UserListPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class,
        ModelModule.class,
        PresenterModule.class,
        ViewModule.class
})
public interface AppComponent {
    void inject(DataManager dataManager);
    void inject(SendMessageHelper sendMessageHelper);
    void inject(PreferenceHelper preferenceHelper);

    void inject(RecordPresenter recordPresenter);
    void inject(RecordListPresenter recordListPresenter);
    void inject(UserPresenter userPresenter);
    void inject(ChooseUserPresenter chooseUserPresenter);

    void inject(RecordListFragment recordListFragment);
    void inject(RecordFragment recordFragment);

    void inject(DeleteRecordPresenter deleteRecordPresenter);
    void inject(DeleteRecordDialog deleteRecordDialog);

    void inject(EditMessagePresenter editMessagePresenter);
    void inject(EditMessageDialog editMessageDialog);

    void inject(EditTimeDialog editTimeDialog);

    void inject(NavigationPresenter navigationPresenter);
    void inject(NavigationActivity navigationActivity);

    void inject(RecordListAdapter.RecordViewHolder recordViewHolder);

    void inject(ChooseUserDialog userListDialog);

    void inject(UserListAdapter.UserViewHolder userViewHolder);

    void inject(SendMessageService sendMessageService);

    void inject(BootCompletedReceiver bootCompletedReceiver);

    void inject(UserListFragment userListFragment);

    void inject(UserListPresenter userListPresenter);

    void inject(ChooseUserAdapter.UserViewHolder userViewHolder);

    void inject(DeleteUserPresenter deleteUserPresenter);

    void inject(DeleteUserDialog deleteUserDialog);

    void inject(RxBus rxBus);
}
