package com.qwert2603.vkautomessage.edit_message;

import com.qwert2603.vkautomessage.base.BaseView;

public interface EditMessageView extends BaseView {
    void setMessage(String message);
    void submitDone(String message);
}
