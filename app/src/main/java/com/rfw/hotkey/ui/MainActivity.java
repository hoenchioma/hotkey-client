package com.rfw.hotkey.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

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
//        keyboardButton.setOnClickListener(view -> {
//            if (ConnectionManager.getInstance().connected.get()) {
//                replaceFragment(new KeyboardFragment());
//            } else {
//                Snackbar.make(view, "Please connect first", Snackbar.LENGTH_SHORT);
//            }
//        });
        keyboardButton.setOnClickListener(view -> replaceFragment(new KeyboardFragment()));

        mouseButton.setOnClickListener((view -> replaceFragment(new MouseFragment())));
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }
}
