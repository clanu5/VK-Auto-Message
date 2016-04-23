package com.qwert2603.vkautomessage.di;

import com.qwert2603.vkautomessage.delete_record.DeleteRecordDialog;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordPresenter;
import com.qwert2603.vkautomessage.edit_message.EditMessageDialog;
import com.qwert2603.vkautomessage.edit_message.EditMessagePresenter;
import com.qwert2603.vkautomessage.edit_time.EditTimeDialog;
import com.qwert2603.vkautomessage.helper.PreferenceHelper;
import com.qwert2603.vkautomessage.helper.SendMessageHelper;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.navigation.NavigationActivity;
import com.qwert2603.vkautomessage.record_details.RecordFragment;
import com.qwert2603.vkautomessage.record_details.RecordPresenter;
import com.qwert2603.vkautomessage.record_list.RecordListAdapter;
import com.qwert2603.vkautomessage.record_list.RecordListFragment;
import com.qwert2603.vkautomessage.record_list.RecordListPresenter;
import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.user_list.UserListAdapter;
import com.qwert2603.vkautomessage.user_list.UserListDialog;
import com.qwert2603.vkautomessage.user_list.UserListPresenter;

import dagger.Component;

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
    void inject(UserListPresenter userListPresenter);

    void inject(RecordListFragment recordListFragment);
    void inject(RecordFragment recordFragment);

    void inject(DeleteRecordPresenter deleteRecordPresenter);
    void inject(DeleteRecordDialog deleteRecordDialog);

    void inject(EditMessagePresenter editMessagePresenter);
    void inject(EditMessageDialog editMessageDialog);

    void inject(EditTimeDialog editTimeDialog);

    void inject(NavigationActivity navigationActivity);

    void inject(RecordListAdapter.RecordViewHolder recordViewHolder);

    void inject(UserListDialog userListDialog);

    void inject(UserListAdapter.UserViewHolder userViewHolder);
}
