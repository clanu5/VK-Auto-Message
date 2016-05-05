package com.qwert2603.vkautomessage.model;

import com.vk.sdk.api.model.VKApiUserFull;

public class VkUser extends User {

    private boolean canWrite;

    public VkUser(VKApiUserFull vkApiUser) {
        super(vkApiUser);
        canWrite = vkApiUser.can_write_private_message;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }
}
