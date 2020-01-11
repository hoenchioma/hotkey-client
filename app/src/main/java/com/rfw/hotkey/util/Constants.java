package com.rfw.hotkey.util;

/**
 * Interface containing constants and default values
 * (used across the application)
 *
 * @author Raheeb Hassan
 */
public interface Constants {
    int SERVER_SOCKET_TIMEOUT = 5000; // in milliseconds

    interface Net {
        int SOCKET_CONNECT_TIMEOUT = 2000; // in milliseconds
        int CONNECT_TIMEOUT = SOCKET_CONNECT_TIMEOUT;
        int SOCKET_RECEIVE_TIMEOUT = 1000; // in milliseconds
        int HEART_BEAT_INTERVAL = 1000;
    }

    interface LiveScreen {
        float FPS = 60.0f;
        int IMAGE_QUALITY = 25;
        float COMPRESS_RATIO = IMAGE_QUALITY / 100.0f;
        int SOCKET_RECEIVE_TIMEOUT = 5000;
        int MOUSE_SENSITIVITY_PERC = 100;
        double MOUSE_SENSITVITY = 1.0;
    }

    interface Gamepad {
        int RIGHT_STICK_SENSITIVITY_PERC = 100;
        double RIGHT_STICK_SENSITIVITY = 1.0;
    }

    interface Mouse {
        int MOUSE_SENSITIVITY_PERC = 100;
        double MOUSE_SENSITVITY = 1.0;
    }
}
