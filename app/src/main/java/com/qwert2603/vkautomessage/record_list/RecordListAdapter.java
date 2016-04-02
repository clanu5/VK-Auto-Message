package com.qwert2603.vkautomessage.record_list;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.record_details.RecordPresenter;
import com.qwert2603.vkautomessage.record_details.RecordView;

import java.util.List;

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

    @Override
    protected RecordPresenter createPresenter(Record model) {
        return new RecordPresenter(model);
    }

    public class RecordViewHolder
            extends BaseRecyclerViewAdapter<Record, RecordListAdapter.RecordViewHolder, RecordPresenter>.RecyclerViewHolder
            implements RecordView {

        private static final int MESSAGE_LENGTH_LIMIT = 52;

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
            mEnableCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> getPresenter().onEnableClicked(isChecked));
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
