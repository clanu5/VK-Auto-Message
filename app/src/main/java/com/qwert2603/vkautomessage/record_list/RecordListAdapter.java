package com.qwert2603.vkautomessage.record_list;

import android.support.v7.widget.RecyclerView;
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

public class RecordListAdapter extends BaseRecyclerViewAdapter<Record, RecordListAdapter.RecordViewHolder, RecordPresenter> {

    public interface RecordEnableChangedCallback {
        /**
         * Запись была включена или выключена.
         *
         * @param position позиция измененное записи.
         * @param enabled  true, если запись была включена.
         */
        void onRecordEnableChanged(int position, boolean enabled);
    }

    private RecordEnableChangedCallback mRecordEnableChangedCallback;

    public RecordListAdapter() {
    }

    public void setRecordEnableChangedCallback(RecordEnableChangedCallback recordEnableChangedCallback) {
        mRecordEnableChangedCallback = recordEnableChangedCallback;
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
            mEnableCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int layoutPosition = getLayoutPosition();
                if (mRecordEnableChangedCallback != null && layoutPosition != RecyclerView.NO_POSITION) {
                    mRecordEnableChangedCallback.onRecordEnableChanged(layoutPosition, isChecked);
                }
                getPresenter().onEnableClicked(isChecked);
            });
        }

        @Override
        protected RecordPresenter getPresenter() {
            return mRecordPresenter;
        }

        @Override
        public void bindPresenter() {
            super.bindPresenter();
            mRecordPresenter.onReadyToAnimate();
        }

        @Override
        protected void setModel(Record record) {
            mRecordPresenter.setRecord(record);
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
            mMessageTextView.setText(message);
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
        public void animateEnter() {
            mRecordPresenter.onAnimateEnterFinished();
        }

        @Override
        public void animateExit() {
            mRecordPresenter.onAnimateExitFinished();
        }

        @Override
        public void animateIn(boolean withLargeDelay) {
            mRecordPresenter.onAnimateInFinished();
        }

        @Override
        public void animateOut() {
            mRecordPresenter.onAnimateOutFinished();
        }

        @Override
        public void performBackPressed() {
        }
    }
}
