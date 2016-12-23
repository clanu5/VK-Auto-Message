package com.qwert2603.vkautomessage.integer_view.vector_integer_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.integer_view.IntegerView;

/**
 * Integer drawables are taken from https://github.com/alexjlockwood/adp-delightful-details
 */
public class VectorIntegerView extends ViewAnimator implements IntegerView {

    private static final String DIGIT_KEY = "com.qwert2603.layouttest.DIGIT_KEY";
    private static final String SUPER_STATE_KEY = "com.qwert2603.layouttest.SUPER_STATE_KEY";

    /*package*/ static final int DIGIT_NTH = 10;
    /*package*/ static final int DIGIT_MINUS = 11;

    private DigitAdapter mDigitAdapter;

    public VectorIntegerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.vector_digit_view, this, true);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VectorIntegerView);
        int digit = typedArray.getInteger(R.styleable.VectorIntegerView_vector_integer, 0);
        typedArray.recycle();

        mDigitAdapter = new DigitAdapter();
        recyclerView.setAdapter(mDigitAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));
        recyclerView.setItemAnimator(new DigitItemAnimator());
        setInteger(digit, false);
    }

    @Override
    public void setInteger(int digit, boolean animate) {
        mDigitAdapter.setInteger(digit);
        if (!animate) {
            mDigitAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getInteger() {
        return mDigitAdapter.getInteger();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState());
        bundle.putInt(DIGIT_KEY, getInteger());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setInteger(bundle.getInt(DIGIT_KEY), false);
            state = bundle.getParcelable(SUPER_STATE_KEY);
        }
        super.onRestoreInstanceState(state);
    }
}
