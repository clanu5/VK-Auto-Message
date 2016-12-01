package com.qwert2603.vkautomessage.user_list;

import com.qwert2603.vkautomessage.base.list.ListView;
import com.qwert2603.vkautomessage.model.User;

import java.util.List;

public interface UserListView extends ListView<User> {
    void showChooseUser();
    void notifyUsersUpdated(List<Integer> updatedUserPositions);
}
