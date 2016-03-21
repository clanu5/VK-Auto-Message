package com.qwert2603.vkautomessage.user_list;

import com.qwert2603.vkautomessage.base.ListView;
import com.vk.sdk.api.model.VKApiUserFull;

public interface UserListView extends ListView<VKApiUserFull> {
    void showUserSelected(int userId);
    void submitDode(int userId);
    void showCantWrite();
}
