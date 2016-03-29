package com.qwert2603.vkautomessage.user_list;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.user_details.UserView;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    // TODO: 29.03.2016 передевать нажатие на элемент с помошью Callback, не использовать в этом классе UserListPresenter

    /*public interface Callbacks {
        void onItemClicked(int position);
    }*/

    private List<VKApiUserFull> mUserList;
    private UserListPresenter mUserListPresenter;

    public UserListAdapter(List<VKApiUserFull> userList, UserListPresenter userListPresenter) {
        mUserList = userList;
        mUserListPresenter = userListPresenter;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bindPresenter(new UserPresenter(mUserList.get(position), mUserListPresenter));
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    @Override
    public void onViewRecycled(UserViewHolder holder) {
        super.onViewRecycled(holder);
        holder.unbindPresenter();
    }

    @Override
    public boolean onFailedToRecycleView(UserViewHolder holder) {
        holder.unbindPresenter();
        return super.onFailedToRecycleView(holder);
    }

    public boolean isShowingList(List<VKApiUserFull> list) {
        return mUserList.equals(list);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements UserView {

        private UserPresenter mUserPresenter;

        private View mItemView;
        private ImageView mPhotoImageView;
        private TextView mUsernameTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemView.setOnClickListener(v -> mUserListPresenter.onUserClicked(mUserPresenter.getUser()));
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.photo_image_view);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.user_name_text_view);
        }

        public void bindPresenter(UserPresenter userPresenter) {
            mUserPresenter = userPresenter;
            mUserPresenter.bindView(UserViewHolder.this);
            mUserPresenter.onViewReady();
        }

        public void unbindPresenter() {
            mUserPresenter.onViewNotReady();
            mUserPresenter.unbindView();
            mUserPresenter = null;
        }

        @Override
        public void showSelected(boolean selected) {
            mItemView.setBackgroundColor(mItemView.getContext().getResources()
                    .getColor(selected ? R.color.colorAccent : android.R.color.transparent));
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
