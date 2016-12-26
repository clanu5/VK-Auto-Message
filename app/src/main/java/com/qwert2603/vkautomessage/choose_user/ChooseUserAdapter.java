package com.qwert2603.vkautomessage.choose_user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.integer_view.anim_integer_view.CounterIntegerView;
import com.qwert2603.vkautomessage.model.VkUser;
import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.user_details.UserView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseUserAdapter extends BaseRecyclerViewAdapter<VkUser, ChooseUserAdapter.UserViewHolder, UserPresenter> {

    public ChooseUserAdapter() {
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    public class UserViewHolder
            extends BaseRecyclerViewAdapter<VkUser, ?, UserPresenter>.RecyclerViewHolder
            implements UserView {

        @BindView(R.id.photo_image_view)
        ImageView mPhotoImageView;

        @BindView(R.id.user_name_text_view)
        TextView mUsernameTextView;

        @BindView(R.id.records_count_layout)
        LinearLayout mRecordsCountLinearLayout;

        @BindView(R.id.records_count_text_view)
        CounterIntegerView mRecordsCountTextView;

        @BindView(R.id.enabled_records_count_text_view)
        CounterIntegerView mEnabledRecordsCountTextView;

        @Inject
        UserPresenter mUserPresenter;

        public UserViewHolder(View itemView) {
            super(itemView);
            VkAutoMessageApplication.getAppComponent().inject(UserViewHolder.this);
            ButterKnife.bind(UserViewHolder.this, itemView);
            mPhotoImageView.getLayoutParams().height = (int) itemView.getResources().getDimension(R.dimen.item_choose_user_photo_size);
            mPhotoImageView.getLayoutParams().width = (int) itemView.getResources().getDimension(R.dimen.item_choose_user_photo_size);
            itemView.getLayoutParams().height = (int) itemView.getResources().getDimension(R.dimen.item_choose_user_height);
        }

        @Override
        protected UserPresenter getPresenter() {
            return mUserPresenter;
        }

        @Override
        protected void setModel(VkUser vkUser) {
            mUserPresenter.setUser(vkUser);
        }

        @Override
        public void bindPresenter() {
            super.bindPresenter();
            mRecordsCountEverShown = false;
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
        public void hideRecordsCount() {
            mRecordsCountLinearLayout.setVisibility(View.GONE);
        }

        private boolean mRecordsCountEverShown = false;

        @Override
        public void showRecordsCount(int recordsCount, int enabledRecordsCount) {
            mRecordsCountLinearLayout.setVisibility(View.VISIBLE);
            mRecordsCountTextView.setInteger(recordsCount, mRecordsCountEverShown);
            mEnabledRecordsCountTextView.setInteger(enabledRecordsCount, mRecordsCountEverShown);
            if (!mRecordsCountEverShown) {
                mRecordsCountEverShown = true;
            }
        }
    }
}
