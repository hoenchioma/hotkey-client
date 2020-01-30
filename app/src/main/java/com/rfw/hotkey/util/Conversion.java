package com.rfw.hotkey.util;

import com.google.common.base.Charsets;

/**
 * Class containing static methods for interconversion
 * between value types
 *
 * @author Raheeb Hassan
 */
public final class Conversion {

    private Conversion() {}

    /**
     * Convert int to byte array
     *
     * @param value  int to be converted
     * @param res    byte array for placing output
     * @param offset offset for res (where in res the output is placed in)
     */
    public static void intToByteArray(int value, byte[] res, int offset) {
        res[offset] = (byte) (value >> 24);
        res[offset + 1] = (byte) (value >> 16);
        res[offset + 2] = (byte) (value >> 8);
        res[offset + 3] = (byte) value;
    }

    /**
     * Convert byte array to int
     *
     * @param bytes  byte arr to be converted
     * @param offset offset for bytes (where in bytes is the input located)
     * @return the required int
     */
    public static int intFromByteArray(byte[] bytes, int offset) {
        return bytes[offset] << 24
                | (bytes[offset + 1] & 0xFF) << 16
                | (bytes[offset + 2] & 0xFF) << 8
                | (bytes[offset + 3] & 0xFF);
    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes toConvert
     * @return hexValue
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (byte aByte : bytes) {
            int intVal = aByte & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     *
     * @param str which to be converted
     * @return array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes(Charsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

}
