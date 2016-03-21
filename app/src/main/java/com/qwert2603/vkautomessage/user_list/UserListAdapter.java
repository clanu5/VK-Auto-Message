package com.qwert2603.vkautomessage.user_list;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.user_details.UserView;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    private List<VKApiUserFull> mUserList;
    private UserListPresenter mUserListPresenter;

    public UserListAdapter(List<VKApiUserFull> userList, UserListPresenter userListPresenter) {
        mUserList = userList;
        mUserListPresenter = userListPresenter;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public boolean isShowingList(List<VKApiUserFull> list) {
        return mUserList.equals(list);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements UserView {

        private UserPresenter mUserPresenter;

        public void bindPresenter(UserPresenter userPresenter) {
            mUserPresenter = userPresenter;
            mUserPresenter.bindView(UserViewHolder.this);
            mUserPresenter.onViewReady();
        }

        /*
        todo
        1. макет одного пользователя (layout)
        2. доделать этот вью холдер
        3. сделать что бы пользователь выделялся при нажании на него
         */

        public void unbindPresenter() {
            mUserPresenter.unbindView();
            mUserPresenter = null;
        }

        public UserViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void showName(String name) {

        }

        @Override
        public void showPhoto(Bitmap bitmap) {

        }
    }
}
