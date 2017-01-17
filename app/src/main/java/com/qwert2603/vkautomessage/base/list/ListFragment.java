package com.qwert2603.vkautomessage.base.list;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Transition;
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
import com.qwert2603.vkautomessage.util.TransitionUtils;

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
    private static final int POSITION_NOTHING_FOUND_TEXT_VIEW = 4;

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

    private ImageButton mActionModeDeleteButton;
    private ImageButton mActionModeSelectAllButton;
    private ImageButton mActionModeEnableAllButton;
    private VectorIntegerView mVectorIntegerView;

    private boolean mIsEverResumed = false;

    /**
     * Whether item listFromModel or emptyTextView were show earlier.
     */
    private boolean mContentEverShown = false;

    /**
     * List of items to show.
     * Used to show listFromModel when it will be possible (after enter transition finish).
     */
    @Nullable
    private List<T> mDelayedListToShow = null;

    private boolean mIsResumed = false;
    private boolean mIsInTransition = false;
    private boolean mIsInSharedElementTransition = false;

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

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> getPresenter().onReloadList());

        if (mFloatingActionMode.getOpened()) {
            initActionModeViews();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().getEnterTransition().addListener(new TransitionUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                // here we invalidateItemDecorations because mDeleteDrawable in mSimpleOnItemTouchHelperCallback blinks on return to prev. activity.
                mRecyclerView.invalidateItemDecorations();
                LogUtils.d("o4igmnw3io4g " + ListFragment.this.getClass() + " getEnterTransition onTransitionStart " + transition);
                mIsInTransition = true;
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mIsInTransition = false;
                showListDelayed();
                LogUtils.d("o4igmnw3io4g " + ListFragment.this.getClass() + " getEnterTransition onTransitionEnd " + transition);
            }
        });
        getActivity().getWindow().getSharedElementEnterTransition().addListener(new TransitionUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                LogUtils.d("o4igmnw3io4g " + ListFragment.this.getClass() + " getSharedElementEnterTransition onTransitionStart " + transition);
                mIsInSharedElementTransition = true;
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mIsInSharedElementTransition = false;
                showListDelayed();
                LogUtils.d("o4igmnw3io4g " + ListFragment.this.getClass() + " getSharedElementEnterTransition onTransitionEnd " + transition);
            }
        });

        getActivity().getWindow().setEnterTransition(null);
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
    public void onResume() {
        super.onResume();
        if (!mIsEverResumed) {
            mIsEverResumed = true;
            AndroidUtils.runOnUI(() -> {
                if (isResumed()) {
                    mIsResumed = true;
                    showListDelayed();
                }
            }, getResources().getInteger(R.integer.transition_duration));
        } else {
            mIsResumed = true;
            showListDelayed();
        }
        LogUtils.d("o4igmnw3io4g " + getClass() + " onResume");
    }

    @Override
    public void onPause() {
        mIsResumed = false;
        LogUtils.d("o4igmnw3io4g " + getClass() + " onPause");
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("onActivityResult " + requestCode + " " + resultCode + " " + data);
        LogUtils.d("o4igmnw3io4g " + getClass() + " onActivityResult");

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

    private void showListDelayed() {
        LogUtils.d("showListDelayed start " + mDelayedListToShow);
        if (mDelayedListToShow != null) {
            List<T> list = mDelayedListToShow;
            mDelayedListToShow = null;
            showList(list);
        }
        LogUtils.d("showListDelayed end " + mDelayedListToShow);
    }

    @Override
    public void showEmpty() {
        // to allow recycler animate items removing, if there were items.
        getAdapter().replaceModelList(new ArrayList<>());

        setViewAnimatorDisplayedChild(POSITION_EMPTY_TEXT_VIEW);
        mContentEverShown = true;
        mDelayedListToShow = null;
    }

    @Override
    public void showList(@NonNull List<T> list) {
        LogUtils.d("showList " + !mIsResumed + " " + mIsInTransition + " " + mIsInSharedElementTransition);
        if (!mIsResumed || mIsInTransition || mIsInSharedElementTransition) {
            mDelayedListToShow = list;
            return;
        }
        LogUtils.printCurrentStack();

        if (!mContentEverShown) {
            mContentEverShown = true;
            mRecyclerItemAnimator.setDelayEnter(true);
            mRecyclerItemAnimator.setAnimateEnterMode(RecyclerItemAnimator.AnimateEnterMode.ALL);
        } else {
            mRecyclerItemAnimator.setDelayEnter(false);
            mRecyclerItemAnimator.setAnimateEnterMode(RecyclerItemAnimator.AnimateEnterMode.LAST);
        }
        setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
        getAdapter().replaceModelList(list);
    }

    @Override
    public void showNothingFound() {
        getAdapter().replaceModelList(new ArrayList<>());
        setViewAnimatorDisplayedChild(POSITION_NOTHING_FOUND_TEXT_VIEW);
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
    public void moveToDetailsForItem(int itemId, boolean newItem, int itemPosition) {
        LogUtils.d("moveToDetailsForItem" + itemPosition + " _ " + itemId);
        if (itemPosition >= 0) {
            mRecyclerView.scrollToPosition(itemPosition);
        }
        if (newItem) {
            AndroidUtils.runOnUI(() -> {
                RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForItemId(itemId);
                if (viewHolder != null) {
                    viewHolder.itemView.setPressed(true);
                }
                moveToDetailsForItem(itemId);
            }, mRecyclerItemAnimator.getAddDuration() + 80);
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
        if (position != POSITION_EMPTY_VIEW) {
            mRecyclerView.bringToFront();
        } else {
            mViewAnimator.bringToFront();
        }
    }

    @CallSuper
    protected void prepareRecyclerViewForTransition() {
        mRecyclerView.stopScroll();
        mRecyclerItemAnimator.endAnimations();
    }

}
