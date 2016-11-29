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
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qwert2603.vkautomessage.util.StringUtils.noMore;

public class RecordListAdapter extends BaseRecyclerViewAdapter<Record, RecordListAdapter.RecordViewHolder, RecordPresenter> {

    public RecordListAdapter() {
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.d("RecordListAdapter onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_record, parent, false);
        return new RecordViewHolder(view);
    }

    public class RecordViewHolder
            extends BaseRecyclerViewAdapter<Record, ?, RecordPresenter>.RecyclerViewHolder
            implements RecordView {

        private static final int MESSAGE_LENGTH_LIMIT = 52;

        @BindView(R.id.enable_check_box)
        CheckBox mEnableCheckBox;

        @BindView(R.id.message_text_view)
        TextView mMessageTextView;

        @BindView(R.id.time_text_view)
        TextView mTimeTextView;

        @BindView(R.id.repeat_info_text_view)
        TextView mRepeatInfoTextView;

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
            super.unbindPresenter();
        }

        @Override
        public ImageView getPhotoImageView() {
            return null;
        }

        @Override
        public void showUserName(String userName) {
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
        public void showRepeatInfo(String repeatInfo) {
            mRepeatInfoTextView.setText(repeatInfo);
        }

        @Override
        public void showRepeatType(String repeatType) {
        }

        @Override
        public void showLoading() {
            mMessageTextView.setText(R.string.loading);
            mTimeTextView.setText(R.string.loading);
            mRepeatInfoTextView.setText(R.string.loading);
        }

        @Override
        public void showEditMessage(String message) {
        }

        @Override
        public void showEditTime(int hour, int minute) {
        }

        @Override
        public void showEditRepeatType(int repeatType) {
        }

        @Override
        public void showEditPeriod(int period) {
        }

        @Override
        public void showEditDaysInWeek(int daysInWeek) {
        }

        @Override
        public void showEditDayInYear(int month, int dayOfMonth) {
        }

        @Override
        public void showToast(int stringRes) {
            Toast.makeText(mMessageTextView.getContext(), stringRes, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void animateIn(boolean withLargeDelay) {
        }

        @Override
        public void animateOut(int id) {
        }

        @Override
        public void prepareForIn() {
        }
    }
}
