package com.qwert2603.vkautomessage.base.list;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.base.delete_item.DeleteItemDialog;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;
import com.qwert2603.vkautomessage.integer_view.vector_integer_view.VectorIntegerView;
import com.qwert2603.vkautomessage.model.Identifiable;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.recycler.SimpleOnItemTouchHelperCallback;
import com.qwert2603.vkautomessage.util.AndroidUtils;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
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

    private static final long DEFAULT_DISABLE_UI_DURATION = 500;

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

    private ImageButton mActionModeDeleteButton;
    private ImageButton mActionModeSelectAllButton;
    private ImageButton mActionModeEnableAllButton;
    private VectorIntegerView mVectorIntegerView;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ButterKnife.bind(ListFragment.this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 400;
            }
        });
        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).setInitialPrefetchItemCount(6);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 10);
        mRecyclerView.setAdapter(getAdapter());


        getAdapter().setClickCallback(position -> {
            mRecyclerView.scrollToPosition(position);
            getPresenter().onItemAtPositionClicked(position);
        });
        getAdapter().setLongClickCallback(position -> {
            mRecyclerView.scrollToPosition(position);
            getPresenter().onItemAtPositionLongClicked(position);
        });
        getAdapter().setItemSwipeDismissCallback(position -> {
            // TODO: 24.12.2016 undo button
            getPresenter().onItemDismissed(position);
        });

        mToolbar.setOnClickListener(v -> getPresenter().onScrollToTopClicked());

        mSimpleOnItemTouchHelperCallback = new SimpleOnItemTouchHelperCallback(getAdapter(), Color.TRANSPARENT, ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_black_24dp));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleOnItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerItemAnimator = new RecyclerItemAnimator();
        mRecyclerView.setItemAnimator(mRecyclerItemAnimator);
        mRecyclerView.setHasFixedSize(true);

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> {
            getPresenter().onReloadList();
//            disableUI(DEFAULT_DISABLE_UI_DURATION);
        });

        if (mFloatingActionMode.getOpened()) {
            initActionModeViews();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        mRecyclerView.setAdapter(null);
        mActionModeSelectAllButton = null;
        mActionModeDeleteButton = null;
        mActionModeEnableAllButton = null;
        mVectorIntegerView = null;
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
        getAdapter().replaceModelList(new ArrayList<>());
        setViewAnimatorDisplayedChild(POSITION_LOADING_TEXT_VIEW);
    }

    @Override
    public void showError() {
        getAdapter().replaceModelList(new ArrayList<>());
        setViewAnimatorDisplayedChild(POSITION_ERROR_TEXT_VIEW);
    }

    /**
     * Whether item list or emptyTextView were show earlier.
     */
    private boolean mContentEverShown = false;

    /**
     * List of items to show.
     * Used to show list after enter transition finish.
     */
    private List<T> mListToShow = new ArrayList<>();

    @Override
    public void showEmpty() {
        // to allow recycler animate items removing, if there were items.
        getAdapter().replaceModelList(new ArrayList<>());

        setViewAnimatorDisplayedChild(POSITION_EMPTY_TEXT_VIEW);
        mContentEverShown = true;
    }

    @Override
    public void showList(List<T> list) {
        LogUtils.d("showList");
        if (!mContentEverShown) {
            long enterDuration = getActivity().getWindow().getEnterTransition().getDuration();
            mListToShow = list;
            AndroidUtils.runOnUI(() -> {
                if (!mContentEverShown) {
                    mContentEverShown = true;
                    setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
                    mRecyclerItemAnimator.setDelayEnter(true);
                    mRecyclerItemAnimator.setAnimateEnterMode(RecyclerItemAnimator.AnimateEnterMode.ALL);
                    getAdapter().replaceModelList(mListToShow);
                    mListToShow = new ArrayList<>();
                }
            }, enterDuration + 50);
            return;
        }
        setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
        mRecyclerItemAnimator.setDelayEnter(false);
        mRecyclerItemAnimator.setAnimateEnterMode(RecyclerItemAnimator.AnimateEnterMode.LAST);
        getAdapter().replaceModelList(list);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateItem(int position) {
        LogUtils.d("updateItem " + position);
        BaseRecyclerViewAdapter.RecyclerViewHolder viewHolder = (BaseRecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            getAdapter().updateItem(viewHolder);
        }
    }

    @Override
    public void moveToDetailsForItem(int itemId, boolean newItem, int newItemPosition) {
        LogUtils.d("moveToDetailsForItem" + newItemPosition + " _ " + itemId);
        if (newItem) {
            mRecyclerView.scrollToPosition(newItemPosition);
            AndroidUtils.runOnUI(() -> {
                RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForItemId(itemId);
                if (viewHolder != null) {
                    viewHolder.itemView.setPressed(true);
                }
                moveToDetailsForItem(itemId);
            }, RecyclerItemAnimator.ENTER_DURATION + 80);
        } else {
            moveToDetailsForItem(itemId);
        }
    }

    protected abstract void moveToDetailsForItem(int itemId);

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
        startActionMode(R.layout.action_mode_user_list);
        initActionModeViews();
    }

    @Override
    protected void onActionModeCancelling() {
        getPresenter().onSelectionModeCancelled();
    }

    @Override
    public void stopListSelectionMode() {
        stopActionMode();
    }

    private void initActionModeViews() {
        mActionModeDeleteButton = (ImageButton) mFloatingActionMode.findViewById(R.id.delete);
        mActionModeDeleteButton.setOnClickListener(v -> getPresenter().onDeleteSelectedClicked());

        mActionModeSelectAllButton = (ImageButton) mFloatingActionMode.findViewById(R.id.select_all);
        mActionModeSelectAllButton.setOnClickListener(v -> getPresenter().onSelectAllClicked());

        mActionModeEnableAllButton = (ImageButton) mFloatingActionMode.findViewById(R.id.enable_all);
        mActionModeEnableAllButton.setOnClickListener(v -> getPresenter().onSelectAllClicked());

        mVectorIntegerView = (VectorIntegerView) mFloatingActionMode.findViewById(R.id.integer_view);
    }

    @Override
    public void showSelectedItemsCount(int count) {
        mVectorIntegerView.setInteger(count, true);
    }

    @Override
    public void showItemsDeleted(int count) {
        Snackbar.make(mContentRootView, getString(R.string.items_deleted_format, count), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> getPresenter().onUndoDeletionClicked())
                .show();
    }

    @Override
    public void scrollToTop() {
        mRecyclerView.scrollToPosition(0);
    }

    private void setViewAnimatorDisplayedChild(int position) {
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
        mViewAnimator.setVisibility(position != POSITION_EMPTY_VIEW ? View.VISIBLE : View.GONE);
    }

    @CallSuper
    protected void prepareRecyclerViewForTransition() {
        mRecyclerView.stopScroll();
        mRecyclerItemAnimator.endAnimations();
    }

}
