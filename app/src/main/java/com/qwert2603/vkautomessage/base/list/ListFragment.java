package com.qwert2603.vkautomessage.base.list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.delete_user.DeleteUserDialog;
import com.qwert2603.vkautomessage.model.Identifiable;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.recycler.SimpleOnItemTouchHelperCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Фрагмент для отображения списка и показа in/out-анимаций.
 * Отображает сообщение об ошибке/загрузке/пустом списке или сам список.
 * Обрабатывает запросы на удаление элемента и переход к подробностям об элементе.
 *
 * @param <T> тип элемента списка
 */
public abstract class ListFragment<T extends Identifiable> extends BaseFragment<ListPresenter> implements ListView<T> {

    private static final int POSITION_EMPTY_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    protected static final int REQUEST_DELETE_ITEM = 1;
    protected static final int REQUEST_DETAILS_FOT_ITEM = 2;

    @BindView(R.id.view_animator)
    ViewAnimator mViewAnimator;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @NonNull
    protected abstract BaseRecyclerViewAdapter<T, ?, ?> getAdapter();

    @LayoutRes
    protected abstract int getLayoutRes();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // TODO: 25.11.2016 скроллинг на самый верх при нажатии на тулбар во всех списках
        // TODO: 26.11.2016 скрывать ресайклер при уничтожении активити
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);

        ButterKnife.bind(ListFragment.this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(getAdapter());
        mRecyclerView.setItemAnimator(new RecyclerItemAnimator());

        getAdapter().setClickCallback(getPresenter()::onItemAtPositionClicked);
        getAdapter().setLongClickCallback(getPresenter()::onItemAtPositionLongClicked);
        getAdapter().setItemSwipeDismissCallback(position -> {
            // чтобы элемент вернулся в свое исходное положение после swipe.
            getAdapter().notifyItemChanged(position);

            getPresenter().onItemDismissed(position);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleOnItemTouchHelperCallback(getAdapter()));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> getPresenter().onReloadList());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().onReloadList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getPresenter().onReadyToAnimateIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_DELETE_ITEM:
                int deletingUserId = data.getIntExtra(DeleteUserDialog.EXTRA_USER_TO_DELETE_ID, 0);
                if (resultCode == Activity.RESULT_OK) {
                    getPresenter().onItemDeleteSubmitted(deletingUserId);
                } else {
                    getPresenter().onItemDeleteCanceled(deletingUserId);
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
    public void notifyItemInserted(int position) {
        getAdapter().notifyItemInserted(position);
    }

    protected abstract Animator createInAnimator(boolean withLargeDelay);

    @Override
    public void animateIn(boolean withLargeDelay) {
        Animator inAnimator = createInAnimator(withLargeDelay);
        inAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getPresenter().onAnimateInFinished();
            }
        });
        inAnimator.start();
    }

    protected abstract Animator createOutAnimator();

    @Override
    public void animateOut(int id) {
        Animator outAnimator = createOutAnimator();
        outAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getPresenter().onAnimateOutFinished(id);
            }
        });
        outAnimator.start();
    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void setViewAnimatorDisplayedChild(int position) {
        mRecyclerView.setVisibility(position == POSITION_EMPTY_VIEW ? View.VISIBLE : View.GONE);
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }

}
