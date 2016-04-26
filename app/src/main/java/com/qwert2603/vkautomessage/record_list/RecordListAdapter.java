package com.qwert2603.vkautomessage.record_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.record_details.RecordPresenter;
import com.qwert2603.vkautomessage.record_details.RecordView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qwert2603.vkautomessage.util.StringUtils.noMore;

public class RecordListAdapter extends BaseRecyclerViewAdapter<Record, RecordListAdapter.RecordViewHolder, RecordPresenter> {

    public RecordListAdapter(List<Record> modelList) {
        super(modelList);
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_record, parent, false);
        return new RecordViewHolder(view);
    }

    public class RecordViewHolder
            extends BaseRecyclerViewAdapter<Record, ?, RecordPresenter>.RecyclerViewHolder
            implements RecordView {

        private static final int MESSAGE_LENGTH_LIMIT = 52;

        @BindView(R.id.photo_image_view)
        ImageView mPhotoImageView;

        @BindView(R.id.user_name_text_view)
        TextView mUsernameTextView;

        @BindView(R.id.enable_check_box)
        CheckBox mEnableCheckBox;

        @BindView(R.id.message_text_view)
        TextView mMessageTextView;

        @BindView(R.id.time_text_view)
        TextView mTimeTextView;

        @Inject
        RecordPresenter mRecordPresenter;

        public RecordViewHolder(View itemView) {
            super(itemView);
            VkAutoMessageApplication.getAppComponent().inject(RecordViewHolder.this);
            ButterKnife.bind(RecordViewHolder.this, itemView);
            mEnableCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> getPresenter().onEnableClicked(isChecked));
        }

        @Override
        protected RecordPresenter getPresenter() {
            return mRecordPresenter;
        }

        @Override
        protected void setModel(Record record) {
            mRecordPresenter.setRecord(record);
        }

        @Override
        public void unbindPresenter() {
            mPhotoImageView.setImageBitmap(null);
            super.unbindPresenter();
        }

        @Override
        public ImageView getPhotoImageView() {
            return mPhotoImageView;
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
