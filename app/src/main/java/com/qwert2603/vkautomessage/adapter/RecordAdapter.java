package com.qwert2603.vkautomessage.adapter;

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
import com.qwert2603.vkautomessage.presenter.RecordListPresenter;
import com.qwert2603.vkautomessage.presenter.RecordPresenter;
import com.qwert2603.vkautomessage.view.RecordView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<Record> mRecordList;
    private RecordListPresenter mRecordListPresenter;

    public RecordAdapter(List<Record> recordList, RecordListPresenter recordListPresenter) {
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
        holder.mRecordPresenter.setModelId(mRecordList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    protected class RecordViewHolder extends RecyclerView.ViewHolder implements RecordView {

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

        public RecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mRecordPresenter = new RecordPresenter();
            mRecordPresenter.bindView(this);
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
            mMessageEditText.setText(message);
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
