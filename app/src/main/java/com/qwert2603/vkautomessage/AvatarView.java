package com.qwert2603.vkautomessage;

import android.content.Context;
import android.graphics.Bitmap;
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

public class AvatarView extends FrameLayout {

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
        // TODO: 17.01.2017 make differ  mInitialsTextView background color.
        View view = LayoutInflater.from(context).inflate(R.layout.avatar_view, this, true);
        mInitialsTextView = (TextView) view.findViewById(R.id.initials_text_view);
        mPhotoImageView = (ImageView) view.findViewById(R.id.photo_image_view);
        mPhotoImageView.setVisibility(INVISIBLE);
    }

    public void showInitials(String initials) {
        mInitialsTextView.setText(initials);
        mInitialsTextView.setVisibility(VISIBLE);
        mPhotoImageView.setVisibility(INVISIBLE);
    }

    public void showPhoto(Bitmap photo) {
        mPhotoImageView.setImageBitmap(photo);
        mPhotoImageView.setVisibility(VISIBLE);
        mInitialsTextView.setVisibility(INVISIBLE);
    }

}
