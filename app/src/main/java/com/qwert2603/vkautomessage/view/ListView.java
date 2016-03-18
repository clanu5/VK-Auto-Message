package com.qwert2603.vkautomessage.view;

import java.util.List;

public interface ListView<T> extends BaseView {
    void showLoading();
    void showError();
    void showEmpty();
    void showList(List<T> list);
}
