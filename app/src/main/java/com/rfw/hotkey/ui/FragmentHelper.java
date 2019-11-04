package com.rfw.hotkey.ui;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

public class FragmentHelper {
    private Activity activity;
    private FragmentManager fragmentManager;
    private int fragmentContainerID;

    private Map<Class, Fragment.SavedState> fragmentSavedStates = new HashMap<>();
    private String currentFragmentTag = null;

    public FragmentHelper(Activity activity, FragmentManager fragmentManager, int fragmentContainerID) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.fragmentContainerID = fragmentContainerID;
    }

    /**
     * replace current fragment with another one
     *
     * @param saveState whether the state of the previous fragment will be saved
     * @param restoreState whether the state of the new fragment will be loaded (if saved before)
     */
    public void replaceFragment(@NonNull Fragment newFragment, boolean saveState, boolean restoreState) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        saveAndLoadState(newFragment, saveState, restoreState);
        currentFragmentTag = getDefaultTag(newFragment); // use class name as tag
        fragmentTransaction.replace(fragmentContainerID, newFragment, currentFragmentTag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public void replaceFragment(@NonNull Fragment newFragment) {
        replaceFragment(newFragment, true, true);
    }

    public void pushFragment(@NonNull Fragment newFragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        saveAndLoadState(newFragment, true, false);
        currentFragmentTag = getDefaultTag(newFragment); // use class name as tag
        fragmentTransaction.replace(fragmentContainerID, newFragment, currentFragmentTag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null); // added to back stack so that it can be loaded
        fragmentTransaction.commit();
    }

    private void saveAndLoadState(@NonNull Fragment newFragment, boolean saveState, boolean restoreState) {
        // save old fragment state
        if (saveState) {
            Fragment oldFragment = getVisibleFragment();
            if (oldFragment != null && oldFragment.isAdded()) {
                fragmentSavedStates.put(oldFragment.getClass(), fragmentManager.saveFragmentInstanceState(oldFragment));
            }
        }

        if (restoreState) {
            // restore state of new fragment (if saved)
            if (!newFragment.isAdded() && fragmentSavedStates.containsKey(newFragment.getClass())) {
                newFragment.setInitialSavedState(fragmentSavedStates.get(newFragment.getClass()));
            }
        }
    }

    public static String getDefaultTag(@NonNull Fragment fragment) {
        return fragment.getClass().getCanonicalName(); // use class name as tag
    }

    public @Nullable Fragment getVisibleFragment() {
        if (currentFragmentTag == null) return null;
        else return fragmentManager.findFragmentByTag(currentFragmentTag);

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        List<Fragment> fragments = fragmentManager.getFragments();
//        for (Fragment fragment : fragments) {
//            if (fragment != null && fragment.isVisible())
//                return fragment;
//        }
//        return null;
    }
}
