package com.qwert2603.vkautomessage.record_list;

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

import butterknife.Bind;
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

    @Override
    protected RecordPresenter createPresenter(Record model) {
        return new RecordPresenter(model);
    }

    public class RecordViewHolder
            extends BaseRecyclerViewAdapter<?, ?, RecordPresenter>.RecyclerViewHolder
            implements RecordView {

        private static final int MESSAGE_LENGTH_LIMIT = 52;

        @Bind(R.id.photo_image_view)
        ImageView mPhotoImageView;

        @Bind(R.id.user_name_text_view)
        TextView mUsernameTextView;

        @Bind(R.id.enable_check_box)
        CheckBox mEnableCheckBox;

        @Bind(R.id.message_text_view)
        TextView mMessageTextView;

        @Bind(R.id.time_text_view)
        TextView mTimeTextView;

        public RecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(RecordViewHolder.this, itemView);
            mEnableCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> getPresenter().onEnableClicked(isChecked));
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
