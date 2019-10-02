package com.rfw.hotkey.net;

import androidx.annotation.Nullable;

import org.json.JSONObject;

/**
 * Connection interface (to implement for different types of connection)
 */
public interface Connection {
    /**
     * send packet to server
     * @param packet JSON object to send
     */
    void sendPacket(JSONObject packet);

    @Nullable String getComputerName();

    boolean isActive();
}
