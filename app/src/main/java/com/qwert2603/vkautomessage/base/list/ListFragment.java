package com.qwert2603.vkautomessage.base.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.base.delete_item.DeleteItemDialog;
import com.qwert2603.vkautomessage.base.in_out_animation.InOutAnimationFragment;
import com.qwert2603.vkautomessage.model.Identifiable;
import com.qwert2603.vkautomessage.navigation.ToolbarHolder;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.recycler.SimpleOnItemTouchHelperCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Фрагмент для отображения сообщения об ошибке/загрузке/пустом списке или самого список.
 * Обрабатывает запросы на удаление элемента и переход к подробностям об элементе.
 *
 * @param <T> тип элемента списка
 */
public abstract class ListFragment<T extends Identifiable> extends InOutAnimationFragment<ListPresenter> implements ListView<T> {

    private static final int POSITION_EMPTY_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    protected static final int REQUEST_DELETE_ITEM = 1;
    protected static final int REQUEST_DETAILS_FOT_ITEM = 2;

    @BindView(R.id.view_animator)
    protected ViewAnimator mViewAnimator;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    protected RecyclerItemAnimator mRecyclerItemAnimator;

    @NonNull
    protected abstract BaseRecyclerViewAdapter<T, ?, ?> getAdapter();

    @LayoutRes
    protected abstract int getLayoutRes();

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);

        ButterKnife.bind(ListFragment.this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()){
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 500;
            }
        });
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 20);
        mRecyclerView.setAdapter(getAdapter());


        getAdapter().setClickCallback(getPresenter()::onItemAtPositionClicked);
        getAdapter().setLongClickCallback(getPresenter()::onItemAtPositionLongClicked);
        getAdapter().setItemSwipeDismissCallback(position -> {
            // чтобы элемент вернулся в свое исходное положение после swipe.
            getAdapter().notifyItemChanged(position);

            getPresenter().onItemDismissed(position);
        });

        ((ToolbarHolder) getActivity()).getToolbarTitle().setOnClickListener(v -> getPresenter().onToolbarClicked());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleOnItemTouchHelperCallback(getAdapter()));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerItemAnimator = new RecyclerItemAnimator();
        mRecyclerView.setItemAnimator(mRecyclerItemAnimator);

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> getPresenter().onReloadList());

        return view;
    }

    @Override
    public void onDestroyView() {
        ((ToolbarHolder) getActivity()).getToolbarTitle().setOnClickListener(null);
        mRecyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: 29.11.2016 загружать список при возвращении от DETAILS_FOT_ITEM, а не в onResume (на всех активити)
        getPresenter().onReloadList();
    }

    @Override
    public void onDestroy() {
        // TODO: 26.11.2016 скрывать ресайклер при уничтожении активити
        //mRecyclerView.setVisibility(View.INVISIBLE);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_DELETE_ITEM:
                int deletingItemId = data.getIntExtra(DeleteItemDialog.EXTRA_ITEM_TO_DELETE_ID, 0);
                if (resultCode == Activity.RESULT_OK) {
                    getPresenter().onItemDeleteSubmitted(deletingItemId);
                } else {
                    getPresenter().onItemDeleteCanceled(deletingItemId);
                }
                break;
            case REQUEST_DETAILS_FOT_ITEM:
                getPresenter().onReadyToAnimateIn();
                break;
        }
    }

    @Override
    public void showLoading() {
        setViewAnimatorDisplayedChild(POSITION_LOADING_TEXT_VIEW);
    }

    @Override
    public void showError() {
        setViewAnimatorDisplayedChild(POSITION_ERROR_TEXT_VIEW);
    }

    @Override
    public void showEmpty() {
        setViewAnimatorDisplayedChild(POSITION_EMPTY_TEXT_VIEW);
    }

    @Override
    public void showListEnter(List<T> list) {
        setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
        getAdapter().insertModelList(list);
    }

    @Override
    public void showList(List<T> list) {
        setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
        getAdapter().replaceModelList(list);
    }

    @Override
    public void showItemSelected(int position) {
        getAdapter().setSelectedItemPosition(position);
    }

    @Override
    public void notifyItemRemoved(int position) {
        getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyItemInserted(int position, int id) {
        mRecyclerItemAnimator.addItemToAnimateEnter(id);
        getAdapter().notifyItemInserted(position);
    }

    @Override
    public void scrollListToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void scrollListToBottom() {
        // TODO: 29.11.2016 сделать плавный скроллинг на другой конец списка
        // может, сначала scrollToPosition, а потом smoothScrollToPosition
        mRecyclerView.smoothScrollToPosition(getAdapter().getItemCount() - 1);
    }

    @Override
    public void scrollToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void animateAllItemsEnter(boolean animate) {
        mRecyclerItemAnimator.setAlwaysAnimateEnter(animate);
    }

    @Override
    public void delayEachItemEnterAnimation(boolean delay) {
        mRecyclerItemAnimator.setDelayEnter(delay);
    }

    private void setViewAnimatorDisplayedChild(int position) {
        mRecyclerView.setVisibility(position == POSITION_EMPTY_VIEW ? View.VISIBLE : View.GONE);
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }

}
