package com.rfw.hotkey.net;

import androidx.core.util.Consumer;
import androidx.databinding.ObservableBoolean;

import org.json.JSONObject;

/**
 * Connection abstract class (to store various info related to the connection with server)
 */
public abstract class Connection {
    private static final String TAG = "Connection";

    public ObservableBoolean active = new ObservableBoolean(false); // indicated whether the connection is active
    public String type; // string indicating the type of connection
    public String computerName; // server computer name (obtained by handshake)

    protected Connection(String type) {
        this.type = type;
    }

    /**
     * connect to server (based on connection type) asynchronously
     * and perform some sort of handshake (to get computer name)
     * (when overriding call super method after implementation)
     */
    public void connect() {
        active.set(true);
        onConnect(true);
    }

    /**
     * send a JSON packet asynchronously
     *
     * @param packet JSON object representing the packet to be sent
     */
    public abstract void sendPacket(JSONObject packet);

    /**
     * send a JSON packet and receive a response immediately
     * @param receivedPacketHandler function to handle received packet
     */
    public abstract void sendAndReceivePacket(JSONObject packetToSend, Consumer<JSONObject> receivedPacketHandler);

    /**
     * close the connection
     * (when overriding call super method after implementation)
     */
    public void disconnect() {
        active.set(false);
        onDisconnect();
    }

    /**
     * called after connecting
     * (meant to be overridden)
     * @param success whether the connection was successful
     */
    protected void onConnect(boolean success) {
    }

    /**
     * called after disconnecting
     * (meant to be overridden)
     */
    protected void onDisconnect() {
    }
}
