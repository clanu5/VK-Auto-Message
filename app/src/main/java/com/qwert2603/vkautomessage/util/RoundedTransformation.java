package com.qwert2603.vkautomessage.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

public class RoundedTransformation implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        if (source.getConfig() == null) {
            LogUtils.e("RoundedTransformation Bitmap.getConfig() == null");
            return source;
        }

        Paint paint = new Paint();
        BitmapShader bitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(bitmapShader);

        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawRoundRect(0, 0, source.getWidth(), source.getHeight(), source.getWidth()/10, source.getHeight()/10, paint);

        source.recycle();
        return result;
    }

    @Override
    public String key() {
        return "rounded";
    }
}
