package com.qwert2603.vkautomessage.user_list;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.user_details.UserView;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

public class UserListAdapter extends BaseRecyclerViewAdapter<VKApiUserFull, UserListAdapter.UserViewHolder, UserPresenter> {

    public UserListAdapter(List<VKApiUserFull> modelList) {
        super(modelList);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    protected UserPresenter createPresenter(VKApiUserFull user) {
        return new UserPresenter(user);
    }

    public class UserViewHolder extends BaseRecyclerViewAdapter.RecyclerViewHolder implements UserView {
        private ImageView mPhotoImageView;
        private TextView mUsernameTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.photo_image_view);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.user_name_text_view);
        }

        @Override
        public void showName(String name) {
            mUsernameTextView.setText(name);
        }

        @Override
        public void showPhoto(Bitmap bitmap) {
            mPhotoImageView.setImageBitmap(bitmap);
        }
    }
}
