package com.qwert2603.vkautomessage.choose_user;

import com.qwert2603.vkautomessage.base.ListView;
import com.qwert2603.vkautomessage.model.VkUser;

public interface ChooseUserView extends ListView<VkUser> {

    void setRefreshingConfig(boolean enable, boolean refreshing);

    void submitDode(int userId);

    void showCantWrite();

    void showNothingFound();

    void showItemSelected(int position);
}
