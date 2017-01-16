package com.qwert2603.vkautomessage.choose_user;

import com.qwert2603.vkautomessage.base.list.ListView;
import com.qwert2603.vkautomessage.model.VkUser;

public interface ChooseUserView extends ListView<VkUser> {

    void setRefreshingConfig(boolean enable, boolean refreshing);

    void submitDode(int userId);

    void submitCancel();

    void showCantWrite();

    void showDontWriteToDeveloper();

    void showGreatChoice();
}
