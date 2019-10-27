package com.rfw.hotkey.util;

public interface Constants {
    int SERVER_SOCKET_TIMEOUT = 5000; // in milliseconds

    interface Net {
        int SOCKET_CONNECT_TIMEOUT = 2000; // in milliseconds
        int CONNECT_TIMEOUT = SOCKET_CONNECT_TIMEOUT;
        int SOCKET_RECEIVE_TIMEOUT = 1000; // in milliseconds
    }

    interface LiveScreen {
        float FPS = 60.0f;
        int IMAGE_QUALITY = 25;
        float COMPRESS_RATIO = IMAGE_QUALITY / 100.0f;
        int SOCKET_RECEIVE_TIMEOUT = 5000;
    }
}
