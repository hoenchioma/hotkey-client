package com.rfw.hotkey.net;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.util.Supplier;
import androidx.databinding.ObservableBoolean;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Connection abstract class (to store various info related to connection with server)
 */
public abstract class Connection {
    private static final String TAG = "Connection";

    public ObservableBoolean active = new ObservableBoolean(false);
    public String type;
    public String computerName;

    // the view to show messages in
    @Nullable protected Supplier<View> contextViewSupplier;

    protected Connection(String type, @Nullable Supplier<View> contextViewSupplier) {
        this.type = type;
        this.contextViewSupplier = contextViewSupplier;
    }

    /**
     * connect to server (based on connection type)
     * and perform some sort of handshake (to get computer name)
     * and make a snackbar to show result
     *
     * @param showMessage whether to make snackbar to show result
     */
    public abstract void connect(boolean showMessage);

    public abstract void sendPacket(JSONObject packet);

    public abstract void close() throws IOException;
}
