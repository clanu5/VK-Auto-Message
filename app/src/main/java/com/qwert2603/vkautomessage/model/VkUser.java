package com.qwert2603.vkautomessage.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.vk.sdk.api.model.VKApiUserFull;

public class VkUser extends User implements Parcelable {

    private boolean canWrite;

    public VkUser(VKApiUserFull vkApiUser) {
        super(vkApiUser);
        canWrite = vkApiUser.can_write_private_message;
    }

    protected VkUser(Parcel in) {
        super(in);
        canWrite = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (canWrite ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VkUser> CREATOR = new Creator<VkUser>() {
        @Override
        public VkUser createFromParcel(Parcel in) {
            return new VkUser(in);
        }

        @Override
        public VkUser[] newArray(int size) {
            return new VkUser[size];
        }
    };

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VkUser)) return false;
        if (!super.equals(o)) return false;

        VkUser vkUser = (VkUser) o;

        return canWrite == vkUser.canWrite;
    }

}
