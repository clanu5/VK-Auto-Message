package com.qwert2603.vkautomessage.model.helper;

public final class PhotoHelper {
    private static PhotoHelper sPhotoHelper = new PhotoHelper();

    private PhotoHelper() {
    }

    public static PhotoHelper getInstance() {
        return sPhotoHelper;
    }
}
