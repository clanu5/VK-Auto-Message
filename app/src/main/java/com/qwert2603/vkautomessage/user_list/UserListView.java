package com.qwert2603.vkautomessage.user_list;

import com.qwert2603.vkautomessage.base.ListView;
import com.qwert2603.vkautomessage.model.User;

public interface UserListView extends ListView<User> {
    void moveToRecordsForUser(int userId, int position);
    void showChooseUser();
    void showDeleteUser(int userId);
    void notifyItemRemoved(int position);
    void notifyItemInserted(int position);
}
