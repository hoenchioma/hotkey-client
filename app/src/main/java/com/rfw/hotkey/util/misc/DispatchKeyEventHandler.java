package com.rfw.hotkey.util.misc;

import android.view.KeyEvent;

/**
 * Interface for fragments to implement so that the enclosing Activity
 * can propagate dispatchKeyEvent method to current fragment
 *
 * @author Raheeb Hassan
 */
public interface DispatchKeyEventHandler {
    /**
     * Similar to the Activity.dispatchKeyEvent(event) method
     * except it can return null
     *
     * @return return true if event was handled, null if not handled
     */
    default Boolean dispatchKeyEvent(KeyEvent event) {
        return null;
    }
}
