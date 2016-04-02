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

    public interface Callbacks {
        void onItemClicked(int position);
    }

    private List<VKApiUserFull> mUserList;
    private Callbacks mCallbacks;
    private RecyclerViewSelector mRecyclerViewSelector = new RecyclerViewSelector();

    public UserListAdapter(List<VKApiUserFull> userList) {
        mUserList = userList;
    }

    public void setCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void setSelectedItemPosition(int position) {
        mRecyclerViewSelector.setSelectedPosition(position);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bindPresenter(new UserPresenter(mUserList.get(position)));
        mRecyclerViewSelector.setItemViewBackground(holder.mItemView, position);
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

    public class RecyclerViewSelector {
        private int mSelectedPosition = -1;

        public void setSelectedPosition(int selectedPosition) {
            int oldSelectedPosition = mSelectedPosition;
            mSelectedPosition = selectedPosition;
            notifyItemChanged(oldSelectedPosition);
            notifyItemChanged(mSelectedPosition);
        }

        @SuppressWarnings("deprecation")
        public void setItemViewBackground(View itemView, int position) {
            itemView.setBackgroundColor(itemView.getContext().getResources()
                    .getColor(position == mSelectedPosition ? R.color.selected_user : android.R.color.transparent));
        }
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements UserView {

        private UserPresenter mUserPresenter;

        private View mItemView;
        private ImageView mPhotoImageView;
        private TextView mUsernameTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemView.setOnClickListener(v -> {
                if (mCallbacks != null) {
                    mCallbacks.onItemClicked(getLayoutPosition());
                }
            });
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.photo_image_view);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.user_name_text_view);
        }

        public void bindPresenter(UserPresenter userPresenter) {
            if (mUserPresenter != null) {
                unbindPresenter();
            }
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
        public void showName(String name) {
            mUsernameTextView.setText(name);
        }

        @Override
        public void showPhoto(Bitmap bitmap) {
            mPhotoImageView.setImageBitmap(bitmap);
        }
    }
}
