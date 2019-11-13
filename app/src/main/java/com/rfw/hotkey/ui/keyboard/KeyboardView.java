package com.rfw.hotkey.ui.keyboard;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class KeyboardView extends FrameLayout {
    private static final String TAG = "KeyboardView";

    public KeyboardView(Context context) {
        this(context, null, 0);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    // toggle whether the keyboard is showing when the view is clicked
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            performClick();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).showSoftInput(this, InputMethodManager.SHOW_FORCED);
        return true;
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.actionLabel = null;
        outAttrs.inputType = InputType.TYPE_NULL; // set input type to default
        // set fullEditor to false, so that only raw key presses are generated (caught by onKeyListener)
        return new BaseInputConnection(this, false);
    }
}