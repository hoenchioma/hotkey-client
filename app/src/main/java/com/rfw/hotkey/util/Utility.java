package com.rfw.hotkey.util;

import android.os.Build;

/**
 * A static class containing useful utility methods
 */
public final class Utility {

    /**
     * Returns the device name of the current device
     * @return returns the device name (manufacturer + model) of current device
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalizeFirstLetter(model);
        } else {
            return capitalizeFirstLetter(manufacturer) + " " + model;
        }
    }

    private static String capitalizeFirstLetter(String s) {
        if (s == null || s.length() == 0) return "";
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /* Private constructor so it can't be instantiated */
    private Utility() {}
}
