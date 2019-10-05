package com.rfw.hotkey.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rfw.hotkey.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageButton keyboardButton;
    ImageButton connectionButton;
    ImageButton mouseButton;

    private View contextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new ConnectionsFragment());

        connectionButton = findViewById(R.id.connectionButtonID);
        keyboardButton = findViewById(R.id.keyboardButtonID);
        mouseButton = findViewById(R.id.mouseButtonID);

        connectionButton.setOnClickListener(view -> replaceFragment(new ConnectionsFragment()));
        keyboardButton.setOnClickListener(view -> replaceFragment(new KeyboardFragment()));
        mouseButton.setOnClickListener(view -> replaceFragment(new MouseFragment()));
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }
}
