package com.rfw.hotkey.util.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;

import com.rfw.hotkey.R;

/**
 * Custom EditTextPreferences which supports
 * - automatically showing value in summary (with formatting)
 * - support only number input mode
 */
public class MyEditTextPref extends EditTextPreference {
    // format for building summary from value (use default summary if null)
    private String summaryBuilder = null;
    // whether the inputType would be "numberSigned"
    private boolean onlyNum = false;

    public MyEditTextPref(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public MyEditTextPref(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public MyEditTextPref(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyEditTextPref(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        processAttributes(context, attrs);
        if (summaryBuilder != null) setAutomaticSummary();
        if (onlyNum) setNumOnly();
    }

    private void processAttributes(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyEditTextPref);
            summaryBuilder = a.getString(R.styleable.MyEditTextPref_summaryBuilder);
            onlyNum = a.getBoolean(R.styleable.MyEditTextPref_onlyNum, onlyNum);
            a.recycle();
        }
    }

    private void setAutomaticSummary() {
        setSummaryProvider((SummaryProvider<EditTextPreference>) preference -> {
            String text = preference.getText();
            return String.format(summaryBuilder, text);
        });
    }

    private void setNumOnly() {
        setOnBindEditTextListener(editText ->
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED));
    }
}
