package com.qwert2603.vkautomessage.errors_show;

import com.qwert2603.vkautomessage.base.BaseView;

public interface ErrorsShowView extends BaseView {
    void showErrors(String errors);
    void showErrorWhileLoadingErrors(String error);
}
