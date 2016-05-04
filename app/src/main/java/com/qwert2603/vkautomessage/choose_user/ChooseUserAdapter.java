package com.qwert2603.vkautomessage.choose_user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.user_details.UserView;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseUserAdapter extends BaseRecyclerViewAdapter<VKApiUserFull, ChooseUserAdapter.UserViewHolder, UserPresenter> {

    public ChooseUserAdapter(List<VKApiUserFull> modelList) {
        super(modelList);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    public class UserViewHolder
            extends BaseRecyclerViewAdapter<VKApiUserFull, ?, UserPresenter>.RecyclerViewHolder
            implements UserView {

        @BindView(R.id.photo_image_view)
        ImageView mPhotoImageView;

        @BindView(R.id.user_name_text_view)
        TextView mUsernameTextView;
        
        @BindView(R.id.records_count_text_view)
        TextView mRecordsCountTextView;

        @Inject
        UserPresenter mUserPresenter;

        public UserViewHolder(View itemView) {
            super(itemView);
            VkAutoMessageApplication.getAppComponent().inject(UserViewHolder.this);
            ButterKnife.bind(UserViewHolder.this, itemView);
            mRecordsCountTextView.setText("");
        }

        @Override
        protected UserPresenter getPresenter() {
            return mUserPresenter;
        }

        @Override
        protected void setModel(VKApiUserFull vkApiUserFull) {
            mUserPresenter.setUser(new User(vkApiUserFull));
        }

        @Override
        public void unbindPresenter() {
            mPhotoImageView.setImageBitmap(null);
            super.unbindPresenter();
        }

        @Override
        public void showName(String name) {
            mUsernameTextView.setText(name);
        }

        @Override
        public ImageView getPhotoImageView() {
            return mPhotoImageView;
        }

        @Override
        public void showRecordsCount(String recordsCount) {
        }
    }
}
