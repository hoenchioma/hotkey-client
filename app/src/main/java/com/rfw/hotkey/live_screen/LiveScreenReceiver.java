package com.rfw.hotkey.live_screen;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.rfw.hotkey.net.Connection;

public interface LiveScreenReceiver {
    /**
     * get the connection type of receiver
     */
    Connection.Type getConnectionType();

    void start(int screenSizeX, int screenSizeY);

    void stop();

    boolean isRunning();

    /**
     * method called when a frame is received
     * (meant to be overloaded)
     */
    void onFrameReceive(@NonNull Bitmap bitmap);
}
