package com.rfw.hotkey.util;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.google.common.base.Charsets;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * A static class containing useful utility methods
 */
public final class Utils {

    private Utils() {}

    /**
     * Get an int saved by preference library (as String)
     *
     * @param key          key of the saved value
     * @param defaultValue default value to return if key not found
     * @return the saved int
     */
    public static int getIntPref(SharedPreferences sharedPreferences, String key, int defaultValue) {
        try {
            String val = sharedPreferences.getString(key, null);
            return val == null ? defaultValue : Integer.parseInt(val);
        } catch (ClassCastException e) {
            return sharedPreferences.getInt(key, defaultValue);
        }
    }

    /**
     * Get an float saved by preference library (as String)
     *
     * @param key          key of the saved value
     * @param defaultValue default value to return if key not found
     * @return the saved float
     */
    public static float getFloatPref(SharedPreferences sharedPreferences, String key, float defaultValue) {
        try {
            String val = sharedPreferences.getString(key, null);
            return val == null ? defaultValue : Float.parseFloat(val);
        } catch (ClassCastException e) {
            return sharedPreferences.getFloat(key, defaultValue);
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename which to be converted to string
     * @return String value of File
     * @throws IOException if error occurs
     */
    public static String loadFileAsString(String filename) throws IOException {
        final int BUFLEN = 1024;
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), Charsets.UTF_8) : new String(baos.toByteArray());
        }
    }

    public static @Nullable Constructor getMatchingConstructor(Class<?> c, Object[] args)
            throws IllegalArgumentException {
        for (Constructor constructor: c.getConstructors()) {
            Class[] parameters = constructor.getParameterTypes();
            if (args.length == parameters.length) {
                boolean match = true;
                for (int i = 0; i < args.length; i++) {
                    if (!args[i].getClass().equals(parameters[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) return constructor;
            }
        }
        return null;
    }
}
