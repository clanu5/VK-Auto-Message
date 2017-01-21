package com.qwert2603.vkautomessage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwert2603.vkautomessage.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class AvatarView extends FrameLayout {

    private static final int[] COLORS = {
            0xffe57373,
            0xfff06292,
            0xffba68c8,
            0xff9575cd,
            0xff7986cb,
            0xff64b5f6,
            0xff4fc3f7,
            0xff4dd0e1,
            0xff4db6ac,
            0xff81c784,
            0xffaed581,
            0xffff8a65,
            0xffd4e157,
            0xffffd54f,
            0xffffb74d,
            0xffa1887f,
            0xff90a4ae
    };

    private TextView mInitialsTextView;
    private ImageView mPhotoImageView;

    public AvatarView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.avatar_view, this, true);
        mInitialsTextView = (TextView) view.findViewById(R.id.initials_text_view);
        mPhotoImageView = (ImageView) view.findViewById(R.id.photo_image_view);
        mPhotoImageView.setVisibility(INVISIBLE);
    }

    public void showInitials(String initials) {
        mInitialsTextView.setText(initials);
        setBackgroundResource(R.drawable.avatar_background);
        ((GradientDrawable) getBackground()).setColor(COLORS[initials.hashCode() % COLORS.length]);
        mInitialsTextView.setVisibility(VISIBLE);
        mPhotoImageView.setVisibility(INVISIBLE);
    }

    public void showPhoto(Bitmap photo) {
        mPhotoImageView.setImageBitmap(photo);
        setBackground(null);
        mPhotoImageView.setVisibility(VISIBLE);
        mInitialsTextView.setVisibility(INVISIBLE);
    }

    public static class PicassoTarget implements Target {
        private final AvatarView mAvatarView;

        public PicassoTarget(AvatarView avatarView) {
            mAvatarView = avatarView;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mAvatarView.showPhoto(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            LogUtils.e("mPicassoTarget onBitmapFailed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }

}
