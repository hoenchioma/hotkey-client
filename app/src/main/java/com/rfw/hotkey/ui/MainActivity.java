package com.rfw.hotkey.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.rfw.hotkey.R;
import com.rfw.hotkey.ui.connections.ConnectionsFragment;
import com.rfw.hotkey.ui.keyboard.KeyboardFragment;
import com.rfw.hotkey.ui.mouse.MouseFragment;

public class MainActivity extends AppCompatActivity {
    private View contextView;

    private MaterialButton keyboardButton;
    private MaterialButton connectionButton;
    private MaterialButton mouseButton;
    private MaterialButton extrasButton;
    private MaterialButton settingsButton;

    private FragmentHelper fragmentHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentHelper = new FragmentHelper(this, getSupportFragmentManager(), R.id.frameContainer);

        replaceFragment(new ConnectionsFragment());

        extrasButton     = findViewById(R.id.extrasButton     );
        connectionButton = findViewById(R.id.connectionButton );
        keyboardButton   = findViewById(R.id.keyboardButton   );
        mouseButton      = findViewById(R.id.mouseButton      );
        settingsButton   = findViewById(R.id.settingsButton   );

        extrasButton    .setOnClickListener(view -> replaceFragment(new ExtrasFragment()      ));
        connectionButton.setOnClickListener(view -> replaceFragment(new ConnectionsFragment() ));
        keyboardButton  .setOnClickListener(view -> replaceFragment(new KeyboardFragment()    ));
        mouseButton     .setOnClickListener(view -> replaceFragment(new MouseFragment()       ));

        settingsButton  .setOnClickListener(view -> startActivity(new Intent(this, SettingsActivity.class)));

        // set default values for settings (in case preference activity hasn't been invoked yet)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }


    public void replaceFragment(@NonNull Fragment newFragment, boolean saveState, boolean restoreState) {
        fragmentHelper.replaceFragment(newFragment, saveState, restoreState);
    }

    public void replaceFragment(@NonNull Fragment newFragment) {
        fragmentHelper.replaceFragment(newFragment);
    }

    public void pushFragment(@NonNull Fragment newFragment) {
        fragmentHelper.pushFragment(newFragment);
    }

    public @Nullable Fragment getVisibleFragment() {
        return fragmentHelper.getVisibleFragment();
    }
}
