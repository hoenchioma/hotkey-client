package com.rfw.hotkey.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.rfw.hotkey.R;
import com.rfw.hotkey.ui.connections.ConnectionsFragment;
import com.rfw.hotkey.ui.keyboard.KeyboardFragment;
import com.rfw.hotkey.ui.mouse.MouseFragment;
import com.rfw.hotkey.util.misc.DispatchKeyEventHandler;

/**
 * The main activity of the application
 * (entry point of the entire application)
 *
 * @author Raheeb Hassan
 */
public class MainActivity extends AppCompatActivity {
    private View contextView;

    private MaterialButton keyboardButton;
    private MaterialButton connectionButton;
    private MaterialButton mouseButton;
    private MaterialButton extrasButton;
    private MaterialButton settingsButton;

    private FragmentHelper fragmentHelper;

    private MaterialButton highlightedButton;
    private int curFragIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentHelper = new FragmentHelper(this, getSupportFragmentManager(), R.id.frameContainer);

        extrasButton = findViewById(R.id.extrasButton);
        connectionButton = findViewById(R.id.connectionButton);
        keyboardButton = findViewById(R.id.keyboardButton);
        mouseButton = findViewById(R.id.mouseButton);
        settingsButton = findViewById(R.id.settingsButton);

        keyboardButton.setOnClickListener(view -> {
            replaceFragmentWithSlideHoriz(new KeyboardFragment(), 1);
            highlightButton(keyboardButton);
        });
        connectionButton.setOnClickListener(view -> {
            replaceFragmentWithSlideHoriz(new ConnectionsFragment(), 2);
            highlightButton(connectionButton);
        });
        mouseButton.setOnClickListener(view -> {
            replaceFragmentWithSlideHoriz(new MouseFragment(), 3);
            highlightButton(mouseButton);
        });
        extrasButton.setOnClickListener(view -> {
            replaceFragmentWithSlideHoriz(new ExtrasFragment(), 4);
            highlightButton(extrasButton);
        });

        settingsButton.setOnClickListener(view -> {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
        });

        // connections fragment shown on start
        replaceFragment(new ConnectionsFragment(), 2);
        highlightButton(connectionButton);

        // set default values for settings (in case preference activity hasn't been invoked yet)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    // override dispatchKeyEvent to propagate it to current fragment
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Fragment currFrag = fragmentHelper.getCurrentFragment();
        try {
            assert currFrag != null;
            return ((DispatchKeyEventHandler) currFrag).dispatchKeyEvent(event);
        } catch (Exception e) {
            return super.dispatchKeyEvent(event);
        }
    }

    public void replaceFragment(@NonNull Fragment newFragment, int newFragIndex) {
        fragmentHelper.clearBackStack();
        fragmentHelper.replaceFragment(newFragment);
        curFragIndex = newFragIndex;
    }

    public void replaceFragmentWithSlideHoriz(@NonNull Fragment newFragment, int newFragIndex) {
        fragmentHelper.clearBackStack();
        // use index to decide whether to use slide right or left animation
        if (newFragIndex < curFragIndex) {
            fragmentHelper.replaceFragmentWithAnim(newFragment,
                    new int[]{
                            R.anim.fragment_slide_in_right,
                            R.anim.fragment_slide_out_left
                    }
            );
        } else if (newFragIndex > curFragIndex) {
            fragmentHelper.replaceFragmentWithAnim(newFragment,
                    new int[]{
                            R.anim.fragment_slide_in_left,
                            R.anim.fragment_slide_out_right
                    }
            );
        }
        curFragIndex = newFragIndex;
    }

    public void pushFragment(@NonNull Fragment newFragment) {
        fragmentHelper.pushFragment(newFragment);
    }

    public void pushFragmentWithSlideVert(@NonNull Fragment newFragment) {
        fragmentHelper.pushFragmentWithAnim(newFragment,
                new int[]{
                        R.anim.fragment_slide_in_down,
                        R.anim.fragment_slide_out_up,
                        R.anim.fragment_slide_in_up,
                        R.anim.fragment_slide_out_down
                }
        );
    }

    public void popFragment() { fragmentHelper.popBackStack(); }

    private void highlightButton(MaterialButton button) {
        // un-highlight previous button
        if (highlightedButton != null) {
            highlightedButton.setIconTint(
                    ContextCompat.getColorStateList(this, R.color.colorHighlight)
            );
            highlightedButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, android.R.color.transparent)
            );
        }
        // highlight current button
        highlightedButton = button;
        highlightedButton.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.colorPrimary)
        );
        highlightedButton.setIconTint(
                ContextCompat.getColorStateList(this, R.color.colorAccent));
    }

    public @Nullable
    Fragment getVisibleFragment() {
        return fragmentHelper.getVisibleFragment();
    }
}
