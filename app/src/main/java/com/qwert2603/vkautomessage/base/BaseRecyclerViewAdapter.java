package com.qwert2603.vkautomessage.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qwert2603.vkautomessage.R;

import java.util.List;

public abstract class BaseRecyclerViewAdapter<M, VH extends BaseRecyclerViewAdapter.RecyclerViewHolder, P extends BasePresenter>
        extends RecyclerView.Adapter<VH> {

    public interface ClickCallbacks {
        void onItemClicked(int position);
    }

    public interface LongClickCallbacks {
        void onItemLongClicked(int position);
    }

    private List<M> mModelList;
    private ClickCallbacks mClickCallbacks;
    private LongClickCallbacks mLongClickCallbacks;
    private RecyclerViewSelector mRecyclerViewSelector = new RecyclerViewSelector();

    public BaseRecyclerViewAdapter(List<M> modelList) {
        mModelList = modelList;
    }

    public void setLongClickCallbacks(LongClickCallbacks longClickCallbacks) {
        mLongClickCallbacks = longClickCallbacks;
    }

    public void setClickCallbacks(ClickCallbacks clickCallbacks) {
        mClickCallbacks = clickCallbacks;
    }

    public void setSelectedItemPosition(int position) {
        mRecyclerViewSelector.setSelectedPosition(position);
    }

    protected abstract P createPresenter(M model);

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.bindPresenter(createPresenter(mModelList.get(position)));
        mRecyclerViewSelector.setItemViewBackground(holder.mItemView, position);
    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
        holder.unbindPresenter();
    }

    @Override
    public boolean onFailedToRecycleView(VH holder) {
        holder.unbindPresenter();
        return super.onFailedToRecycleView(holder);
    }

    public boolean isShowingList(List<M> list) {
        return mModelList.equals(list);
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

    public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder implements BaseView {
        private P mPresenter;
        public View mItemView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemView.setOnClickListener(v -> {
                if (mClickCallbacks != null) {
                    mClickCallbacks.onItemClicked(getLayoutPosition());
                }
            });
            mItemView.setOnLongClickListener(v -> {
                if (mLongClickCallbacks != null) {
                    mLongClickCallbacks.onItemLongClicked(getLayoutPosition());
                }
                return false;
            });
        }

        protected final P getPresenter() {
            return mPresenter;
        }

        @SuppressWarnings("unchecked")
        public void bindPresenter(P presenter) {
            if (mPresenter != null) {
                unbindPresenter();
            }
            mPresenter = presenter;
            mPresenter.bindView(RecyclerViewHolder.this);
            mPresenter.onViewReady();
        }

        public void unbindPresenter() {
            mPresenter.onViewNotReady();
            mPresenter.unbindView();
            mPresenter = null;
        }
    }

}
