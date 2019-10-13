package com.rfw.hotkey.net;

import androidx.core.util.Consumer;
import androidx.databinding.ObservableBoolean;

import org.json.JSONObject;

public interface Connection {
    /**
     * getter for the Observable boolean
     * representing the state of the connection (active or not)
     */
    ObservableBoolean getActive();

    /**
     * getter for the type of the connection
     */
    Type getType();

    /**
     * getter for the computer name (server device)
     */
    String getComputerName();

    /**
     * connect to server (based on connection type) asynchronously
     * and perform some sort of handshake (to get computer name)
     * (when overriding call super method after implementation)
     */
    default void connect() {
        getActive().set(true);
        onConnect(true);
    }

    /**
     * close the connection
     * (when overriding call super method after implementation)
     */
    default void disconnect() {
        getActive().set(false);
        onDisconnect();
    }

    /**
     * called after connecting
     * (meant to be overridden)
     * @param success whether the connection was successful
     */
    default void onConnect(boolean success) {}
    /**
     * called after disconnecting
     * (meant to be overridden)
     */
    default void onDisconnect() {}

    /**
     * send a JSON packet asynchronously
     * @param packet JSON object representing the packet to be sent
     */
    void sendJSONPacket(JSONObject packet);

    /**
     * send a JSON packet and receive a response immediately
     * @param receivedPacketHandler function to handle received packet
     */
    void sendAndReceiveJSONPacket(JSONObject packetToSend, Consumer<JSONObject> receivedPacketHandler);

    /**
     * Defines the network type of the connection
     * (the type of network that will be used to do the transfer)
     */
    enum Type {
        WIFI,
        BLUETOOTH,
        INTERNET,
        NONE
    }
}
