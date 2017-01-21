package com.qwert2603.vkautomessage.user_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qwert2603.vkautomessage.avatar_view.AvatarView;
import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.integer_view.anim_integer_view.CounterIntegerView;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.user_details.UserView;
import com.qwert2603.vkautomessage.avatar_view.RoundedTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListAdapter extends BaseRecyclerViewAdapter<User, UserListAdapter.UserViewHolder, UserPresenter> {

    public UserListAdapter() {
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    public class UserViewHolder
            extends BaseRecyclerViewAdapter<User, ?, UserPresenter>.RecyclerViewHolder
            implements UserView {

        @BindView(R.id.avatar_view)
        AvatarView mAvatarView;

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

        private final Target mPicassoTarget;

        public UserViewHolder(View itemView) {
            super(itemView);
            VkAutoMessageApplication.getAppComponent().inject(UserViewHolder.this);
            ButterKnife.bind(UserViewHolder.this, itemView);
            mPicassoTarget = new AvatarView.PicassoTarget(mAvatarView);
        }

        @Override
        protected UserPresenter getPresenter() {
            return mUserPresenter;
        }

        @Override
        protected void setModel(User user) {
            mUserPresenter.setUser(user);
        }

        @Override
        public void bindPresenter() {
            super.bindPresenter();
            mRecordsCountEverShown = false;
        }

        @Override
        public void unbindPresenter() {
            super.unbindPresenter();
            Picasso.with(itemView.getContext()).cancelRequest(mPicassoTarget);
        }

        @Override
        public void showName(String name) {
            mUsernameTextView.setText(name);
        }

        @Override
        public void showPhoto(String url, String initials) {
            mAvatarView.showInitials(initials);
            Picasso.with(itemView.getContext())
                    .load(url)
                    .transform(new RoundedTransformation())
                    .into(mPicassoTarget);
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
