package com.qwert2603.vkautomessage.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Базовый адаптер для {@link RecyclerView} для шаблона MVP.
 * Может передавать callback'и о нажатии и долгом нажатии на отдельный элемент.
 * {@link #setClickCallbacks(ClickCallbacks)}, {@link #setLongClickCallbacks(LongClickCallbacks)}.
 * Позволяет выделять отдельный элемент {@link #setSelectedItemPosition(int)}.
 *
 * @param <M>  тип модели, отображаемой в каждом элементе.
 * @param <VH> тип объекта (ViewHolder), отвечающего за отображение данных в отдельном элементе.
 * @param <P>  тип презентера, организующего работу отдельного элемента.
 */
public abstract class BaseRecyclerViewAdapter<M, VH extends BaseRecyclerViewAdapter.RecyclerViewHolder, P extends BasePresenter>
        extends RecyclerView.Adapter<VH> {

    /**
     * Callback для нажатия на элемент.
     */
    public interface ClickCallbacks {
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
    public interface LongClickCallbacks {
        /**
         * Долгое нажатие на элемент.
         *
         * @param position позиция элемента, на который было долгое нажатие
         */
        void onItemLongClicked(int position);
    }

    private List<M> mModelList;
    private ClickCallbacks mClickCallbacks;
    private LongClickCallbacks mLongClickCallbacks;
    private RecyclerViewSelector mRecyclerViewSelector = new RecyclerViewSelector();

    /**
     * @param modelList список объектов модели.
     */
    public BaseRecyclerViewAdapter(List<M> modelList) {
        mModelList = modelList;
    }

    /**
     * Назначить callback для нажатия на элемент.
     *
     * @param clickCallbacks callback для нажатия на элемент.
     */
    public void setClickCallbacks(ClickCallbacks clickCallbacks) {
        mClickCallbacks = clickCallbacks;
    }

    /**
     * Назначить callback для долгого нажатия на элемент.
     *
     * @param longClickCallbacks callback для долгого нажатия на элемент.
     */
    public void setLongClickCallbacks(LongClickCallbacks longClickCallbacks) {
        mLongClickCallbacks = longClickCallbacks;
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
        // назначаем модель viewHolder'у элемента.
        holder.setModel(mModelList.get(position));
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
        super.onViewRecycled(holder);
        // отвязываем презентер от переработанного представления.
        holder.unbindPresenter();
    }

    @Override
    public boolean onFailedToRecycleView(VH holder) {
        // в случае ошибки переработки отвязяваем презентер.
        holder.unbindPresenter();
        return super.onFailedToRecycleView(holder);
    }

    /**
     * Сравнить произвольный список с отображаемым.
     *
     * @param list список для сравнения.
     * @return отображается переданный список или нет?
     */
    public boolean isShowingList(List<M> list) {
        return mModelList == list;
    }

    /**
     * Класс для выделения отдельного элемента.
     */
    public class RecyclerViewSelector {
        private int mSelectedPosition = -1;

        /**
         * Установить позицию выделенного элемент.
         *
         * @param selectedPosition позиция выделенного элемента.
         */
        public void setSelectedPosition(int selectedPosition) {
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
        public void showWhetherItemSelected(View itemView, int position) {
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
                if (mClickCallbacks != null) {
                    mClickCallbacks.onItemClicked(getLayoutPosition());
                }
            });
            mItemView.setOnLongClickListener(v -> {
                if (mLongClickCallbacks != null) {
                    mLongClickCallbacks.onItemLongClicked(getLayoutPosition());
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