package com.rfw.hotkey.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rfw.hotkey.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ImageButton keyboardButton;
    ImageButton connectionButton;
    ImageButton mouseButton;
    ImageButton extraButton;
    private View contextView;

    private Map<Class, Fragment.SavedState> fragmentSavedStates = new HashMap<>();
    private String currentFragmentTag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new ConnectionsFragment());

        extraButton = findViewById(R.id.extraButton);
        connectionButton = findViewById(R.id.connectionButton);
        keyboardButton = findViewById(R.id.keyboardButton);
        mouseButton = findViewById(R.id.mouseButton);

        extraButton.setOnClickListener(view -> replaceFragment(new ExtraFragment()));
        connectionButton.setOnClickListener(view -> replaceFragment(new ConnectionsFragment()));
        keyboardButton.setOnClickListener(view -> replaceFragment(new KeyboardFragment()));
        mouseButton.setOnClickListener(view -> replaceFragment(new MacrosFragment()));
    }

    public void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // save old fragment state
        Fragment oldFragment = getVisibleFragment();
        if (oldFragment != null && oldFragment.isAdded()) {
            fragmentSavedStates.put(oldFragment.getClass(), fragmentManager.saveFragmentInstanceState(oldFragment));
        }

        // restore state of new fragment (if saved)
        if (!newFragment.isAdded() && fragmentSavedStates.containsKey(newFragment.getClass())) {
            newFragment.setInitialSavedState(fragmentSavedStates.get(newFragment.getClass()));
        }

        currentFragmentTag = newFragment.getClass().getCanonicalName(); // use class name as tag
        fragmentTransaction.replace(R.id.frameContainer, newFragment, currentFragmentTag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public Fragment getVisibleFragment() {
        if (currentFragmentTag == null) return null;
        else return getSupportFragmentManager().findFragmentByTag(currentFragmentTag);

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        List<Fragment> fragments = fragmentManager.getFragments();
//        for (Fragment fragment : fragments) {
//            if (fragment != null && fragment.isVisible())
//                return fragment;
//        }
//        return null;
    }
}
