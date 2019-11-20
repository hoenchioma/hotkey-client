package com.rfw.hotkey.live_screen;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rfw.hotkey.net.connection.Connection;

import static com.rfw.hotkey.util.Constants.LiveScreen.COMPRESS_RATIO;
import static com.rfw.hotkey.util.Constants.LiveScreen.FPS;

/**
 * Receiver for live screen frames from server
 *
 * @author Raheeb Hassan
 */
public interface LiveScreenReceiver {
    /**
     * get the connection type of receiver
     */
    Connection.Type getConnectionType();

    default void start(int screenSizeX, int screenSizeY) {
        start(screenSizeX, screenSizeY, FPS, COMPRESS_RATIO);
    }

    void start(int screenSizeX, int screenSizeY, float fps, float compressRatio);

    void stop();

    boolean isRunning();

    /**
     * method called when a frame is received
     * (meant to be overloaded)
     */
    void onFrameReceive(@NonNull Bitmap bitmap);

    /**
     * method to be called when error occurs
     * (meant to be overridden)
     * @param e exception that occurred
     * @param isFatal whether the concerned activity should be closed
     */
    void onError(@Nullable Exception e, boolean isFatal);
}
