package com.rfw.hotkey.ui;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rfw.hotkey.R;

public class MainActivity extends AppCompatActivity {

    ImageButton keyboardButton;
    ImageButton connectionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new ConnectionsFragment());

        keyboardButton = findViewById(R.id.keyboardButtonID);
        connectionButton = findViewById(R.id.connectionButtonID);

        keyboardButton.setOnClickListener(view -> replaceFragment(new KeyboardFragment()));
        connectionButton.setOnClickListener(view -> replaceFragment(new ConnectionsFragment()));
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        // fragmentTransaction.addToBackStack(fragment.toString());
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }
}
