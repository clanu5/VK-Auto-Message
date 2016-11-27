package com.qwert2603.vkautomessage.user_list;

import com.qwert2603.vkautomessage.base.list.ListView;
import com.qwert2603.vkautomessage.model.User;

public interface UserListView extends ListView<User> {
    void showChooseUser();
}
