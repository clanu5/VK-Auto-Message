package com.qwert2603.vkautomessage.user_list;

import com.qwert2603.vkautomessage.base.ListView;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

public interface UserListView extends ListView<VKApiUserFull> {

    void setRefreshingConfig(boolean enable, boolean refreshing);

    void submitDode(int userId);

    void showCantWrite();

    void setSelectedItemPosition(int position);

    void showNothingFound();

    void showListWithSelectedItem(List<VKApiUserFull> list, int selectedPosition);
}
