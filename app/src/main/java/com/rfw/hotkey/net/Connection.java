package com.rfw.hotkey.net;

import androidx.databinding.ObservableBoolean;

/**
 * interface to represent a Connection
 * (for handling connections in ConnectionManager)
 */
public interface Connection {
    /**
     * getter for the Observable boolean
     * representing the state of the connection (active or not)
     */
    ObservableBoolean getActive();

    /**
     * getter for the type of the connection
     */
    ConnectionType getType();

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

    // enums for defining various attributes

    /**
     * Defines the network type of the connection
     * (the type of network that will be used to do the transfer)
     */
    enum ConnectionType {
        LAN,
        BLUETOOTH,
        REMOTE_SERVER
    }
}
