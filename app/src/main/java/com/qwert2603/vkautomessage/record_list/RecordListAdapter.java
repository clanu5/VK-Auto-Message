package com.qwert2603.vkautomessage.record_list;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.record_details.RecordPresenter;
import com.qwert2603.vkautomessage.record_details.RecordView;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {

    private List<Record> mRecordList;
    private RecordListPresenter mRecordListPresenter;

    public RecordListAdapter(List<Record> recordList, RecordListPresenter recordListPresenter) {
        mRecordList = recordList;
        mRecordListPresenter = recordListPresenter;
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.d("onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        LogUtils.d("onBindViewHolder " + position);
        holder.bindPresenter(new RecordPresenter(mRecordList.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onViewRecycled(RecordViewHolder holder) {
        super.onViewRecycled(holder);
        holder.unbindPresenter();
    }

    @Override
    public boolean onFailedToRecycleView(RecordViewHolder holder) {
        holder.mRecordPresenter.onViewNotReady();
        holder.mRecordPresenter.unbindView();
        return super.onFailedToRecycleView(holder);
    }

    protected class RecordViewHolder extends RecyclerView.ViewHolder implements RecordView {

        private static final int MESSAGE_LENGTH_LIMIT = 26;

        private RecordPresenter mRecordPresenter;

        @Bind(R.id.photo_image_view)
        ImageView mPhotoImageView;

        @Bind(R.id.user_name_text_view)
        Button mUsernameButton;

        @Bind(R.id.enable_check_box)
        Switch mEnableSwitch;

        @Bind(R.id.message_text_view)
        EditText mMessageEditText;

        @Bind(R.id.time_text_view)
        Button mTimeButton;

        public void bindPresenter(RecordPresenter recordPresenter) {
            LogUtils.d("bindPresenter");
            mRecordPresenter = recordPresenter;
            mRecordPresenter.bindView(RecordViewHolder.this);
            mRecordPresenter.onViewReady();
        }

        public void unbindPresenter() {
            LogUtils.d("unbindPresenter");
            mRecordPresenter = null;
        }

        public RecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> mRecordListPresenter.onRecordClicked(mRecordPresenter.getModelId()));
            mEnableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> mRecordPresenter.onEnableClicked(isChecked));
        }

        @Override
        public void showPhoto(Bitmap photo) {
            mPhotoImageView.setImageBitmap(photo);
        }

        @Override
        public void showUserName(String userName) {
            mUsernameButton.setText(userName);
        }

        @Override
        public void showMessage(String message) {
            mMessageEditText.setText(StringUtils.noMore(message, MESSAGE_LENGTH_LIMIT));
        }

        @Override
        public void showEnabled(boolean enabled) {
            mEnableSwitch.setChecked(enabled);
        }

        @Override
        public void showTime(String time) {
            mTimeButton.setText(time);
        }

        @Override
        public void showChooseUser() {
        }

        @Override
        public void showChooseTime() {
        }
    }
}
