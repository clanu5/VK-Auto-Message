package com.qwert2603.vkautomessage.record_list;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.record_details.RecordPresenter;
import com.qwert2603.vkautomessage.record_details.RecordView;

import java.util.List;

import static com.qwert2603.vkautomessage.util.StringUtils.noMore;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {

    // TODO: 29.03.2016 передевать нажатие на элемент с помошью Callback, не использовать в этом классе UserListPresenter

    /*public interface Callbacks {
        void onItemClicked(int position);
        void onItemLongClicked(int position);
    }*/

    private List<Record> mRecordList;
    private RecordListPresenter mRecordListPresenter;

    public RecordListAdapter(List<Record> recordList, RecordListPresenter recordListPresenter) {
        mRecordList = recordList;
        mRecordListPresenter = recordListPresenter;
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        holder.bindPresenter(new RecordPresenter(mRecordList.get(position)));
    }

    @Override
    public int getItemCount() {
        return mRecordList.size();
    }

    @Override
    public void onViewRecycled(RecordViewHolder holder) {
        holder.unbindPresenter();
        super.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(RecordViewHolder holder) {
        holder.unbindPresenter();
        return super.onFailedToRecycleView(holder);
    }

    public boolean isShowingList(List<Record> list) {
        return mRecordList == list;
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder implements RecordView {

        private static final int MESSAGE_LENGTH_LIMIT = 52;

        private RecordPresenter mRecordPresenter;

        private ImageView mPhotoImageView;
        private TextView mUsernameTextView;
        private CheckBox mEnableCheckBox;
        private TextView mMessageTextView;
        private TextView mTimeTextView;

        public RecordViewHolder(View itemView) {
            super(itemView);
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.photo_image_view);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.user_name_text_view);
            mEnableCheckBox = (CheckBox) itemView.findViewById(R.id.enable_check_box);
            mMessageTextView = (TextView) itemView.findViewById(R.id.message_text_view);
            mTimeTextView = (TextView) itemView.findViewById(R.id.time_text_view);
            itemView.setOnClickListener(v -> mRecordListPresenter.onRecordClicked(mRecordPresenter.getModelId()));
            itemView.setOnLongClickListener(v -> {
                mRecordListPresenter.onRecordLongClicked(mRecordPresenter.getModelId());
                return true;
            });
            mEnableCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mRecordPresenter.onEnableClicked(isChecked));
        }

        public void bindPresenter(RecordPresenter recordPresenter) {
            if (mRecordPresenter != null) {
                unbindPresenter();
            }
            mRecordPresenter = recordPresenter;
            mRecordPresenter.bindView(RecordViewHolder.this);
            mRecordPresenter.onViewReady();
        }

        public void unbindPresenter() {
            mRecordPresenter.onViewNotReady();
            mRecordPresenter.unbindView();
            mRecordPresenter = null;
        }

        @Override
        public void showPhoto(Bitmap photo) {
            mPhotoImageView.setImageBitmap(photo);
        }

        @Override
        public void showUserName(String userName) {
            mUsernameTextView.setText(userName);
        }

        @Override
        public void showMessage(String message) {
            mMessageTextView.setText(noMore(message, MESSAGE_LENGTH_LIMIT));
        }

        @Override
        public void showEnabled(boolean enabled) {
            mEnableCheckBox.setChecked(enabled);
        }

        @Override
        public void showTime(String time) {
            mTimeTextView.setText(time);
        }

        @Override
        public void showLoading() {
            mPhotoImageView.setImageBitmap(null);
            mUsernameTextView.setText(R.string.loading);
            mMessageTextView.setText(R.string.loading);
            mTimeTextView.setText(R.string.loading);
        }

        @Override
        public void showChooseUser(int currentUserId) {
        }

        @Override
        public void showEditMessage(String message) {
        }

        @Override
        public void showEditTime(long currentTimeInMillis) {
        }

        @Override
        public void showToast(int stringRes) {
            Toast.makeText(mPhotoImageView.getContext(), stringRes, Toast.LENGTH_SHORT).show();
        }
    }
}
