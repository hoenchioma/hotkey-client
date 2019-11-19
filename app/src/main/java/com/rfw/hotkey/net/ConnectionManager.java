package com.rfw.hotkey.net;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.rfw.hotkey.net.connection.Connection;

import org.json.JSONObject;

/**
 * Singleton class which manages connections
 */
public class ConnectionManager {
    private static final String TAG = "ConnectionManager";

    // singleton instance of class
    private static ConnectionManager instance;

    public ObservableField<Connection> connection = new ObservableField<>();

    private ConnectionManager() { }

    /**
     * Get singleton instance of class through lazy initialization
     *
     * @return singleton instance of class
     */
    synchronized public static ConnectionManager getInstance() {
        if (instance == null) instance = new ConnectionManager();
        return instance;
    }

    /**
     * Set the active connection of ConnectionManager and then connect to it
     * @param connection the connection to connect to
     */
    public void makeConnection(Connection connection) {
        setConnection(connection);
        connection.connect();
    }

    public void setConnection(Connection connection) {
        this.connection.set(connection);
    }

    /**
     * Whether there is a connection and if so whether it it active
     */
    @SuppressWarnings("ConstantConditions")
    public boolean isConnectionActive() {
        return connection.get() != null && connection.get().getActive().get();
    }

    /**
     * Returns ObservableBoolean representing the state of current connection (null if no connection)
     */
    @SuppressWarnings("ConstantConditions")
    public ObservableBoolean getConnectionActive() {
        return connection.get() != null ? connection.get().getActive() : null;
    }

    /**
     * send a JSON packet to connected device
     * (if not connected log an error)
     *
     * @param packet JSON object representing the packet to be sent
     */
    @SuppressWarnings("ConstantConditions")
    public void sendPacket(@NonNull JSONObject packet) {
        if (!isConnectionActive()) Log.e(TAG, "sendPacket: attempt to send package using inactive connection");
        else connection.get().sendJSONPacket(packet);
    }

    /**
     * send a JSON packet to connected device and receive a response immediately (uses default timeout)
     * (if not connected log an error)
     *
     * @param receivedPacketHandler function to handle received packet
     *                              it may receive null if packet receive failed
     */
    @SuppressWarnings("ConstantConditions")
    public void sendAndReceivePacket(@NonNull JSONObject packetToSend, @NonNull Consumer<JSONObject> receivedPacketHandler) {
        if (!isConnectionActive()) Log.e(TAG, "sendAndReceivePacket: attempt to send package using inactive connection");
        else connection.get().sendAndReceiveJSONPacket(packetToSend, receivedPacketHandler);
    }

    /**
     * send a JSON packet to connected device and receive a response immediately
     * (if not connected log an error)
     *
     * @param receivedPacketHandler function to handle received packet
     *                              it may receive null if packet receive failed
     * @param timeOut time in milliseconds after which the receive times out
     */
    @SuppressWarnings("ConstantConditions")
    public void sendAndReceivePacket(@NonNull JSONObject packetToSend, @NonNull Consumer<JSONObject> receivedPacketHandler, long timeOut) {
        if (!isConnectionActive()) Log.e(TAG, "sendAndReceivePacket: attempt to send package using inactive connection");
        else connection.get().sendAndReceiveJSONPacket(packetToSend, receivedPacketHandler, timeOut);
    }

    /**
     * Close and remove current connection
     * (log an error if not connection exists)
     */
    @SuppressWarnings("ConstantConditions")
    public void closeConnection() {
        if (connection.get() == null) Log.e(TAG, "connection cannot be closed, as connection does not exist");
        else {
            connection.get().disconnect();
            connection.set(null);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public Connection.Type getConnectionType() {
        return connection.get() == null ? Connection.Type.NONE : connection.get().getType();
    }

    /**
     * Get the device of the currently connected computer
     * @return the device name (null if not connected)
     */
    @SuppressWarnings("ConstantConditions")
    public String getComputerName() {
        return connection.get() == null ? null : connection.get().getComputerName();
    }
}
