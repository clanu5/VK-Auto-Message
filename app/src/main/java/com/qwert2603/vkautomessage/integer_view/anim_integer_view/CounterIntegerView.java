package com.qwert2603.vkautomessage.integer_view.anim_integer_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.integer_view.IntegerView;

public class CounterIntegerView extends LinearLayout implements IntegerView {

    private static final String DIGIT_KEY = "com.qwert2603.layouttest.DIGIT_KEY";
    private static final String SUPER_STATE_KEY = "com.qwert2603.layouttest.SUPER_STATE_KEY";

    private int mInteger;

    private TextView mStableTextView;
    private TextView mInTextView;
    private TextView mOutTextView;

    public CounterIntegerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        View view = LayoutInflater.from(context).inflate(R.layout.anim_integer_view, this, true);
        mStableTextView = (TextView) view.findViewById(R.id.stable);
        mInTextView = (TextView) view.findViewById(R.id.in);
        mOutTextView = (TextView) view.findViewById(R.id.out);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CounterIntegerView);
        mInteger = typedArray.getInteger(R.styleable.CounterIntegerView_counter_integer, 0);
        int textColor = typedArray.getColor(R.styleable.CounterIntegerView_android_textColor, Color.BLACK);
        float defaultTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 24, getResources().getDisplayMetrics());
        float textSize = typedArray.getDimension(R.styleable.CounterIntegerView_android_textSize, defaultTextSize);
        typedArray.recycle();

        mStableTextView.setTextColor(textColor);
        mStableTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mInTextView.setTextColor(textColor);
        mInTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mOutTextView.setTextColor(textColor);
        mOutTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        setIntegerWithoutAnimation(mInteger);
    }

    @Override
    public int getInteger() {
        return mInteger;
    }

    @Override
    public void setInteger(int integer, boolean animate) {
        if (!animate) {
            mInteger = integer;
            setIntegerWithoutAnimation(mInteger);
            return;
        }

        int oldNumber = mInteger;
        mInteger = integer;

        String b = String.valueOf(oldNumber);
        String e = String.valueOf(mInteger);

        if (((oldNumber > 0) != (mInteger > 0)) || (b.length() != e.length())) {
            mStableTextView.setText("");
            do_switch(oldNumber, mInteger, String.valueOf(oldNumber), String.valueOf(mInteger));
            return;
        }

        int s = 0;
        for (int i = 0; i < b.length(); i++) {
            if (b.charAt(i) != e.charAt(i)) {
                break;
            }
            ++s;
        }

        mStableTextView.setText(e.substring(0, s));
        do_switch(oldNumber, mInteger, b.substring(s, b.length()), e.substring(s, e.length()));
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
            setIntegerWithoutAnimation(bundle.getInt(DIGIT_KEY));
            state = bundle.getParcelable(SUPER_STATE_KEY);
        }
        super.onRestoreInstanceState(state);
    }

    private void do_switch(int bi, int ei, String b, String e) {
        mInTextView.setText(e);
        mOutTextView.setText(b);
        mInTextView.setVisibility(VISIBLE);
        mOutTextView.setVisibility(VISIBLE);

        if (ei > bi) {
            mInTextView.setTranslationY(getHeight());
            mInTextView.animate().translationY(0);
            mOutTextView.setTranslationY(0);
            mOutTextView.animate().translationY(-1 * getHeight());
        } else {
            mInTextView.setTranslationY(-1 * getHeight());
            mInTextView.animate().translationY(0);
            mOutTextView.setTranslationY(0);
            mOutTextView.animate().translationY(getHeight());
        }

    }

    private void setIntegerWithoutAnimation(int integer) {
        mInteger = integer;
        mInTextView.setVisibility(GONE);
        mOutTextView.setVisibility(GONE);
        mStableTextView.setText(String.valueOf(mInteger));
    }
}
