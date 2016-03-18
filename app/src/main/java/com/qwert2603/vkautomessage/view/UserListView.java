package com.qwert2603.vkautomessage.view;

import com.vk.sdk.api.model.VKApiUserFull;

public interface UserListView extends ListView<VKApiUserFull> {
    void showUserSelected(VKApiUserFull user);
    void submitDode(VKApiUserFull user);
    void showCantWrite(VKApiUserFull user);
}
