package com.qwert2603.vkautomessage.base;

import android.os.SystemClock;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qwert2603.vkautomessage.model.Identifiable;
import com.qwert2603.vkautomessage.recycler.ItemTouchHelperAdapter;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Базовый адаптер для {@link RecyclerView} для шаблона MVP.
 * Может передавать callback'и о нажатии и долгом нажатии на отдельный элемент.
 * {@link #setClickCallback(ClickCallback)}, {@link #setLongClickCallback(LongClickCallback)}.
 * Позволяет выделять отдельный элемент {@link #setSelectedItemPosition(int)}.
 *
 * @param <M>  тип модели, отображаемой в каждом элементе.
 * @param <VH> тип объекта (ViewHolder), отвечающего за отображение данных в отдельном элементе.
 * @param <P>  тип презентера, организующего работу отдельного элемента.
 */
public abstract class BaseRecyclerViewAdapter
        <M extends Identifiable, VH extends BaseRecyclerViewAdapter.RecyclerViewHolder, P extends BasePresenter>
        extends RecyclerView.Adapter<VH>
        implements ItemTouchHelperAdapter {

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
    public void setSelectedItemPosition(int position) {
        mRecyclerViewSelector.setSelectedPosition(position);
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
        mRecyclerViewSelector.showWhetherItemSelected(holder.mItemView, position);
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

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {
        LogUtils.d("BaseRecyclerViewAdapter onItemDismiss" + viewHolder);
        mItemSwipeDismissCallback.onItemDismiss(viewHolder.getAdapterPosition());
    }

    /**
     * Добавить список объектов модели для отображения.
     *
     * @param modelList список объектов модели для добавления.
     */
    public void insertModelList(List<M> modelList) {
        mModelList = modelList;
        notifyItemRangeInserted(0, mModelList.size());
    }

    /**
     * Заменить список объектов модели для отображения.
     *
     * @param newList новый список объектов модели для отображения.
     */
    public void replaceModelList(List<M> newList) {
        List<M> oldList = mModelList;
        if (newList != mModelList) {
            mModelList = newList;
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
        LogUtils.d("DiffUtil.calculateDiff " + (SystemClock.elapsedRealtime() - b) + " ms");
        LogUtils.printCurrentStack();
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * Класс для выделения отдельного элемента.
     */
    private class RecyclerViewSelector {
        private int mSelectedPosition = -1;

        /**
         * Установить позицию выделенного элемент.
         *
         * @param selectedPosition позиция выделенного элемента.
         */
        void setSelectedPosition(int selectedPosition) {
            LogUtils.d("RecyclerViewSelector setSelectedPosition " + selectedPosition);
            int oldSelectedPosition = mSelectedPosition;
            mSelectedPosition = selectedPosition;
            notifyItemChanged(oldSelectedPosition);
            notifyItemChanged(mSelectedPosition);
        }

        /**
         * Отобразить выделен элемент или нет.
         *
         * @param itemView view элемента.
         * @param position позиция элемента.
         */
        void showWhetherItemSelected(View itemView, int position) {
            itemView.setSelected(position == mSelectedPosition);
        }
    }

    /**
     * Класс ViewHolder, отвечающий за отображение данных в отдельном элементе
     * и хранящий ссылки на отображаемые View (TextView, например).
     */
    public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder implements BaseView {
        public View mItemView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            // назначаем callback'и для клика и долгого клика по элементу.
            mItemView.setOnClickListener(v -> {
                int layoutPosition = getLayoutPosition();
                if (mClickCallback != null && layoutPosition != RecyclerView.NO_POSITION) {
                    mClickCallback.onItemClicked(layoutPosition);
                }
            });
            mItemView.setOnLongClickListener(v -> {
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
    }

}