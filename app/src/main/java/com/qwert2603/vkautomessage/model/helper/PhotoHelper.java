package com.qwert2603.vkautomessage.model.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import rx.Observable;

public final class PhotoHelper {

    public Observable<Bitmap> downloadBitmap(String url) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(doDownloadBitmap(url));
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    /**
     * Загрузить изображение по переданному адресу из интернета.
     */
    private Bitmap doDownloadBitmap(String urlString) throws IOException {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            inputStream = url.openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return bitmap;
    }

}
