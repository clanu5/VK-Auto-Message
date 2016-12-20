package com.qwert2603.vkautomessage.base.list;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.base.delete_item.DeleteItemDialog;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;
import com.qwert2603.vkautomessage.model.Identifiable;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.recycler.SimpleOnItemTouchHelperCallback;
import com.qwert2603.vkautomessage.util.AndroidUtils;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Фрагмент для отображения сообщения об ошибке/загрузке/пустом списке или самого список.
 * Обрабатывает запросы на удаление элемента и переход к подробностям об элементе.
 *
 * @param <T> тип элемента списка
 */
public abstract class ListFragment<T extends Identifiable> extends NavigationFragment<ListPresenter> implements ListView<T> {

    private static final int POSITION_EMPTY_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    protected static final int REQUEST_DELETE_ITEM = 1;
    protected static final int REQUEST_DETAILS_FOT_ITEM = 2;

    @BindView(R.id.content_root_view)
    protected View mContentRootView;

    @BindView(R.id.view_animator)
    protected ViewAnimator mViewAnimator;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    protected RecyclerItemAnimator mRecyclerItemAnimator;

    protected SimpleOnItemTouchHelperCallback mSimpleOnItemTouchHelperCallback;

    @NonNull
    protected abstract BaseRecyclerViewAdapter<T, ?, ?> getAdapter();

    private boolean mContentEverShown = false;

    private boolean mUiEnabled = true;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ButterKnife.bind(ListFragment.this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                //return super.getExtraLayoutSpace(state);
                return 400;
            }
        });
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 10);
        mRecyclerView.setAdapter(getAdapter());


        getAdapter().setClickCallback(position1 -> {
            if (mUiEnabled) {
                getPresenter().onItemAtPositionClicked(position1);
            }
        });
        getAdapter().setLongClickCallback(position1 -> {
            if (mUiEnabled) {
                getPresenter().onItemAtPositionLongClicked(position1);
            }
        });
        getAdapter().setItemSwipeDismissCallback(position -> {
            // чтобы элемент вернулся в свое исходное положение после swipe.
            getAdapter().notifyItemChanged(position);

            if (mUiEnabled) {
                getPresenter().onItemDismissed(position);
            }
        });

        mToolbar.setOnClickListener(v -> getPresenter().onToolbarClicked());

        mSimpleOnItemTouchHelperCallback = new SimpleOnItemTouchHelperCallback(getAdapter(), Color.TRANSPARENT, ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_black_24dp));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleOnItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerItemAnimator = new RecyclerItemAnimator();
        mRecyclerView.setItemAnimator(mRecyclerItemAnimator);
        mRecyclerView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                mRecyclerView.getViewTreeObserver().removeOnDrawListener(this);

                // тут считается, что высота элемента всегда равна высоте элемента-пользователя.
                // высота элемента-записи отличается несильно, так что этим можно пренебречь.
                float childHeight = getResources().getDimension(R.dimen.item_user_height);
                int itemsPerScreen = 1 + (int) (mRecyclerView.getHeight() / childHeight);
                LogUtils.d("itemsPerScreen == " + itemsPerScreen);
                mRecyclerItemAnimator.setItemsPerScreen(itemsPerScreen);
            }
        });

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> getPresenter().onReloadList());

        return view;
    }

    @Override
    public void onDestroyView() {
        mRecyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("onActivityResult " + requestCode + " " + resultCode + " " + data);

        switch (requestCode) {
            case REQUEST_DELETE_ITEM:
                int deletingItemId = data.getIntExtra(DeleteItemDialog.EXTRA_ITEM_TO_DELETE_ID, -1);
                if (resultCode == Activity.RESULT_OK) {
                    getPresenter().onItemDeleteSubmitted(deletingItemId);
                } else {
                    getPresenter().onItemDeleteCanceled(deletingItemId);
                }
                break;
            case REQUEST_DETAILS_FOT_ITEM:
                // TODO: 05.12.2016 при возвращении от активити, созданной через TaskStackBuilder onActivityResult не вызывается =(
                if (data == null) {
                    LogUtils.e("onActivityResult REQUEST_DETAILS_FOT_ITEM data == null");
                    break;
                }
                LogUtils.d("onActivityResult REQUEST_DETAILS_FOT_ITEM " + " " + data.getIntExtra(BaseActivity.EXTRA_ITEM_ID, -1));
                if (resultCode == Activity.RESULT_OK) {
                    int id = data.getIntExtra(BaseActivity.EXTRA_ITEM_ID, -1);
                    getPresenter().onReturnFromItemDetails(id);
                }
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
        if (!mContentEverShown) {
            mContentEverShown = true;
            onFirstContentShow(null);
        }
        setViewAnimatorDisplayedChild(POSITION_EMPTY_TEXT_VIEW);
    }

    @Override
    public void showList(List<T> list) {
        setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
        LogUtils.d("showList " + mContentEverShown);
        if (!mContentEverShown) {
            mContentEverShown = true;
            onFirstContentShow(list);
        } else {
            getAdapter().replaceModelList(list);
        }
    }

    protected void onFirstContentShow(@Nullable List<T> list) {
        if (list != null) {
            LogUtils.d("onFirstContentShow");
            mRecyclerItemAnimator.setAlwaysAnimateEnter(true);
            mRecyclerItemAnimator.setDelayEnter(true);
            AndroidUtils.runOnUI(() -> getAdapter().insertModelList(list), 750);
        }
    }

    @Override
    public void moveToDetailsForItem(T item, boolean newItem, int newItemPosition) {
        LogUtils.d("moveToDetailsForItem" + newItemPosition + " _ " + item);
        mRecyclerView.scrollToPosition(newItemPosition);
        if (newItem) {
            AndroidUtils.runOnUI(() -> {
                RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForItemId(item.getId());
                if (viewHolder != null) {
                    viewHolder.itemView.setPressed(true);
                }
                moveToDetailsForItem(item);
            }, RecyclerItemAnimator.ENTER_DURATION + 50);
        } else {
            moveToDetailsForItem(item);
        }
    }

    protected abstract void moveToDetailsForItem(T item);

    @Override
    public void setItemSelectionState(int position, boolean select) {
        getAdapter().setItemSelectionState(position, select);
    }

    @Override
    public void selectAllItems() {
        getAdapter().selectAllItems();
    }

    @Override
    public void unSelectAllItems() {
        getAdapter().unSelectAllItems();
    }

    @Override
    public void startListSelectionMode() {
        startActionMode(R.layout.user_list_action_mode)
                .findViewById(R.id.delete)
                .setOnClickListener(v -> Snackbar.make(mContentRootView, "delete", Snackbar.LENGTH_SHORT).show());
    }

    @Override
    protected void onActionModeRestored(View view) {
        view.findViewById(R.id.delete)
                .setOnClickListener(v -> Snackbar.make(mContentRootView, "delete restored", Snackbar.LENGTH_SHORT).show());
    }

    @Override
    protected void onActionModeCancelled() {
        getPresenter().onActionModeCancelled();
    }

    @Override
    public void stopListSelectionMode() {
        stopActionMode();
    }

    @Override
    public void notifyItemRemoved(int position) {
        getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyItemInserted(int position, int id) {
        LogUtils.d("notifyItemInserted " + position + " " + id);
        mRecyclerItemAnimator.setAlwaysAnimateEnter(false);
        mRecyclerItemAnimator.setDelayEnter(false);
        mRecyclerItemAnimator.addItemToAnimateEnter(id);
        getAdapter().notifyItemInserted(position);
    }

    @Override
    public void notifyItemsUpdated(List<Integer> updatedPositions) {
        LogUtils.d("updatedPositions " + updatedPositions);
        for (Integer updatedUserPosition : updatedPositions) {
            getAdapter().notifyItemChanged(updatedUserPosition);
        }
    }

    @Override
    public void scrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    public void enableUI() {
        LogUtils.d(getClass() + " enableUI");
        mUiEnabled = true;
        mRecyclerView.setOnTouchListener(null);
    }

    @Override
    public void disableUI() {
        LogUtils.d(getClass() + " disableUI");
        mUiEnabled = false;
        mRecyclerView.setOnTouchListener((v, event) -> true);
    }

    private void setViewAnimatorDisplayedChild(int position) {
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
        mViewAnimator.setVisibility(position != POSITION_EMPTY_VIEW ? View.VISIBLE : View.GONE);
    }

    protected void prepareRecyclerViewForTransition() {
        mRecyclerView.stopScroll();
        mRecyclerItemAnimator.endAnimations();
    }

}
