package com.rfw.hotkey.ui;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper class for switching and handling fragments
 *
 * @author Raheeb Hassan
 */
public class FragmentHelper {
    private Activity activity;
    private FragmentManager fragmentManager;
    private int fragmentContainerID;

    // hashmap for saving the states of the fragments
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
     * @param anim integer array with the ids for the animations (size = 2 or 4)
     *             anim[0]: entry animation for new fragment
     *             anim[1]: exit animation for old fragment
     *             (if size == 4)
     *             anim[2], anim[3]: same as anim[0], anim[1] but for when reversing fragment transition using backstack
     */
    public void replaceFragment(@NonNull Fragment newFragment, boolean saveState, boolean restoreState, boolean addToBackStack, int[] anim) {
        if (anim != null && anim.length != 4 && anim.length != 2) throw new AssertionError();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        saveAndLoadState(newFragment, saveState, restoreState);

        if (anim != null) {
            if (anim.length == 2) fragmentTransaction.setCustomAnimations(anim[0], anim[1]);
            else fragmentTransaction.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
        }

        currentFragmentTag = getDefaultTag(newFragment); // use class name as tag
        fragmentTransaction.replace(fragmentContainerID, newFragment, currentFragmentTag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (addToBackStack) fragmentTransaction.addToBackStack(currentFragmentTag + "-" + getDefaultTag(newFragment));

        fragmentTransaction.commit();
    }

    public void replaceFragment(@NonNull Fragment newFragment) {
        replaceFragment(newFragment, true, true, false, null);
    }

    public void replaceFragmentWithAnim(@NonNull Fragment newFragment, int[] anim) {
        replaceFragment(newFragment, true, true, false, anim);
    }

    public void pushFragment(@NonNull Fragment newFragment) {
        replaceFragment(newFragment, true, false, true, null);
    }

    public void pushFragmentWithAnim(@NonNull Fragment newFragment, int[] anim) {
        replaceFragment(newFragment, true, false, true, anim);
    }

    private void saveAndLoadState(@NonNull Fragment newFragment, boolean saveState, boolean restoreState) {
        // save old fragment state
        if (saveState) {
            Fragment oldFragment = getVisibleFragment();
            if (oldFragment != null && oldFragment.isAdded()) {
                fragmentSavedStates.put(oldFragment.getClass(), fragmentManager.saveFragmentInstanceState(oldFragment));
            }
        }
        // restore state of new fragment (if saved)
        if (restoreState) {
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

    public void popBackStack() {
        fragmentManager.popBackStack();
    }

    public void clearBackStack() {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
    }

    public Fragment getCurrentFragment() {
        return fragmentManager.findFragmentByTag(currentFragmentTag);
    }
}
