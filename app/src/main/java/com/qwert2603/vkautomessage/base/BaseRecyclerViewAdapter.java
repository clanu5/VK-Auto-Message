package com.qwert2603.vkautomessage.base;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qwert2603.vkautomessage.model.Identifiable;
import com.qwert2603.vkautomessage.recycler.ItemTouchHelperAdapter;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Базовый адаптер для {@link RecyclerView} для шаблона MVP.
 * <p>
 * Может передавать callback'и о нажатии и долгом нажатии на отдельный элемент, а также о том, что элемент был swiped.
 * {@link #setClickCallback(ClickCallback)},
 * {@link #setLongClickCallback(LongClickCallback)},
 * {@link #setItemSwipeDismissCallback(ItemSwipeDismissCallback)}
 * <p>
 * Позволяет выделять отдельный элемент: {@link #setItemSelectionState(int, boolean)}.
 *
 * @param <M>  тип модели, отображаемой в каждом элементе.
 * @param <VH> тип объекта (ViewHolder), отвечающего за отображение данных в отдельном элементе.
 * @param <P>  тип презентера, организующего работу отдельного элемента.
 */
public abstract class BaseRecyclerViewAdapter
        <M extends Identifiable, VH extends BaseRecyclerViewAdapter.RecyclerViewHolder, P extends BasePresenter>
        extends RecyclerView.Adapter<VH>
        implements ItemTouchHelperAdapter {

    private static final String PAYLOAD_SELECTED_STATE_CHANGED = "PAYLOAD_SELECTED_STATE_CHANGED";

    /**
     * Callback для нажатия на элемент.
     */
    public interface ClickCallback {
        /**
         * Нажатие на элемент.
         *
         * @param position позиция нажатого элемента
         */
        void onItemClicked(int position);
    }

    /**
     * Callback для долгого нажатия на элемент.
     */
    public interface LongClickCallback {
        /**
         * Долгое нажатие на элемент.
         *
         * @param position позиция элемента, на который было долгое нажатие
         */
        void onItemLongClicked(int position);
    }

    /**
     * Callback для swiped-dismissed применительно к элементу.
     */
    public interface ItemSwipeDismissCallback {
        /**
         * Элемент был swiped-dismissed.
         *
         * @param position позиция элемента, который был swiped-dismissed.
         */
        void onItemDismiss(int position);
    }

    private volatile List<M> mModelList = new ArrayList<>();
    private ClickCallback mClickCallback;
    private LongClickCallback mLongClickCallback;
    private ItemSwipeDismissCallback mItemSwipeDismissCallback;
    private RecyclerViewSelector mRecyclerViewSelector = new RecyclerViewSelector();

    public BaseRecyclerViewAdapter() {
        setHasStableIds(true);
    }

    /**
     * Назначить callback для нажатия на элемент.
     *
     * @param clickCallback callback для нажатия на элемент.
     */
    public void setClickCallback(ClickCallback clickCallback) {
        mClickCallback = clickCallback;
    }

    /**
     * Назначить callback для долгого нажатия на элемент.
     *
     * @param longClickCallback callback для долгого нажатия на элемент.
     */
    public void setLongClickCallback(LongClickCallback longClickCallback) {
        mLongClickCallback = longClickCallback;
    }


    public void setItemSwipeDismissCallback(ItemSwipeDismissCallback itemSwipeDismissCallback) {
        mItemSwipeDismissCallback = itemSwipeDismissCallback;
    }

    /**
     * Установить позицию выделенного элемент.
     *
     * @param position позиция выделенного элемента.
     */
    public void setItemSelectionState(int position, boolean select) {
        mRecyclerViewSelector.setPositionSelectionState(position, select);
    }

    public void unSelectAllItems() {
        mRecyclerViewSelector.unSelectAll();
    }

    public void selectAllItems() {
        mRecyclerViewSelector.selectAll();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(VH holder, int position) {
        LogUtils.d("onBindViewHolder " + holder + " " + position);
        M model = mModelList.get(position);
        // назначаем модель viewHolder'у элемента.
        holder.setModel(model);
        holder.bindPresenter();
        // отображаем выделен элемент или нет.
        mRecyclerViewSelector.showWhetherItemSelected(holder);
        // clear info about previous removes.
        holder.setSwiped(false);
    }

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if (payloads.contains(PAYLOAD_SELECTED_STATE_CHANGED)) {
            mRecyclerViewSelector.showWhetherItemSelected(holder);
            return;
        }
        onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }

    @Override
    public void onViewRecycled(VH holder) {
        LogUtils.d("onViewRecycled " + holder);
        super.onViewRecycled(holder);
        // отвязываем презентер от переработанного представления.
        holder.unbindPresenter();
    }

    @Override
    public boolean onFailedToRecycleView(VH holder) {
        LogUtils.e("onFailedToRecycleView " + holder);
        // в случае ошибки переработки отвязяваем презентер.
        holder.unbindPresenter();
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public long getItemId(int position) {
        return mModelList.get(position).getId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {
        LogUtils.d("BaseRecyclerViewAdapter onItemDismiss" + viewHolder);
        mItemSwipeDismissCallback.onItemDismiss(viewHolder.getAdapterPosition());
        ((RecyclerViewHolder) viewHolder).setSwiped(true);
    }

    /**
     * Заменить список объектов модели для отображения.
     *
     * @param newList новый список объектов модели для отображения.
     */
    public void replaceModelList(List<M> newList) {
        List<M> oldList = mModelList;
        if (newList != mModelList) {
            mModelList = new ArrayList<>(newList);
        }

        long b = SystemClock.elapsedRealtime();
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(oldList.get(oldItemPosition), newList.get(newItemPosition));
            }
        });
        long time = SystemClock.elapsedRealtime() - b;
        LogUtils.d("DiffUtil.calculateDiff " + time + " ms");
        if (time > 10) {
            LogUtils.e("DiffUtil.calculateDiff is too long: " + time + " ms");
        }
        LogUtils.printCurrentStack();
        diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {
                LogUtils.d("onInserted " + position + " " + count);
            }

            @Override
            public void onRemoved(int position, int count) {
                LogUtils.d("onRemoved " + position + " " + count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                LogUtils.d("onInserted " + fromPosition + " " + toPosition);
            }

            @Override
            public void onChanged(int position, int count, Object payload) {
                LogUtils.d("onChanged " + position + " " + count);
            }
        });
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * Update item view without recycling VH.
     *
     * @param holder VH containing view for item
     */
    @SuppressWarnings("unchecked")
    public void updateItem(@NonNull RecyclerViewHolder holder, M item) {
        holder.unbindPresenter();
        mModelList.set(holder.getAdapterPosition(), item);

        onBindViewHolder((VH) holder, holder.getAdapterPosition());
    }

    /**
     * Класс для выделения элементов.
     */
    private class RecyclerViewSelector {
        private Set<Long> mSelectedIds = new HashSet<>();

        void setSelectedIds(Set<Long> selectedIds) {
            mSelectedIds = selectedIds;
            notifyItemRangeChanged(0, getItemCount(), PAYLOAD_SELECTED_STATE_CHANGED);
        }

        void selectAll() {
            Set<Long> set = new HashSet<>();
            for (M m : mModelList) {
                set.add((long) m.getId());
            }
            setSelectedIds(set);
        }

        void unSelectAll() {
            setSelectedIds(new HashSet<>());
        }

        /**
         * Установить состояние выделения для элемента.
         *
         * @param position позиция элемента.
         * @param select   если true, элемент будет выделен, иначе с него будет снято выделение.
         */
        void setPositionSelectionState(int position, boolean select) {
            long id = getItemId(position);
            LogUtils.d("RecyclerViewSelector setItemSelectionState " + id + " " + position + " " + select);
            if (select) {
                if (mSelectedIds.add(id)) {
                    notifyItemChanged(position, PAYLOAD_SELECTED_STATE_CHANGED);
                }
            } else {
                if (mSelectedIds.remove(id)) {
                    notifyItemChanged(position, PAYLOAD_SELECTED_STATE_CHANGED);
                }
            }
        }

        /**
         * Отобразить выделен элемент или нет.
         */
        void showWhetherItemSelected(RecyclerView.ViewHolder viewHolder) {
            viewHolder.itemView.setSelected(mSelectedIds.contains(getItemId(viewHolder.getAdapterPosition())));
        }
    }

    /**
     * Класс ViewHolder, отвечающий за отображение данных в отдельном элементе
     * и хранящий ссылки на отображаемые View (TextView, например).
     */
    public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder implements BaseView {

        /**
         * Whether item was deleted by swipe.
         */
        private boolean mIsSwiped = false;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            // назначаем callback'и для клика и долгого клика по элементу.
            itemView.setOnClickListener(v -> {
                int layoutPosition = getLayoutPosition();
                if (mClickCallback != null && layoutPosition != RecyclerView.NO_POSITION) {
                    mClickCallback.onItemClicked(layoutPosition);
                }
            });
            itemView.setOnLongClickListener(v -> {
                int layoutPosition = getLayoutPosition();
                if (mLongClickCallback != null && layoutPosition != RecyclerView.NO_POSITION) {
                    mLongClickCallback.onItemLongClicked(layoutPosition);
                }
                return true;
            });
        }

        /**
         * Получить презентер, организующий работу этого элемента.
         *
         * @return презентер, организующий работу элемента.
         */
        protected abstract P getPresenter();

        /**
         * Назначить модель для этого элемента.
         *
         * @param model объект модели.
         */
        protected abstract void setModel(M model);

        /**
         * Привязать презентер, предназначенный для этого ViewHolder'a.
         */
        @SuppressWarnings("unchecked")
        public void bindPresenter() {
            getPresenter().bindView(RecyclerViewHolder.this);
            getPresenter().onViewReady();
        }

        /**
         * Отвязать презентер, предназначенный для этого ViewHolder'a.
         */
        public void unbindPresenter() {
            getPresenter().onViewNotReady();
            getPresenter().unbindView();
        }

        public int getItemsCount() {
            return BaseRecyclerViewAdapter.this.getItemCount();
        }

        public boolean isSwiped() {
            return mIsSwiped;
        }

        public void setSwiped(boolean swiped) {
            mIsSwiped = swiped;
        }
    }

}