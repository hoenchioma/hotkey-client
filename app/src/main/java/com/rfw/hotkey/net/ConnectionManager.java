package com.rfw.hotkey.net;

import android.util.Log;

import androidx.core.util.Consumer;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.rfw.hotkey.net.connection.Connection;

import org.json.JSONObject;

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

    public void makeConnection(Connection connection) {
        setConnection(connection);
        connection.connect();
    }

    public void setConnection(Connection connection) {
        this.connection.set(connection);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean isConnectionActive() {
        return connection.get() != null && connection.get().getActive().get();
    }

    @SuppressWarnings("ConstantConditions")
    public ObservableBoolean getConnectionActive() {
        return connection.get() != null ? connection.get().getActive() : null;
    }

    @SuppressWarnings("ConstantConditions")
    public void sendPacket(JSONObject packet) {
        if (!isConnectionActive()) Log.e(TAG, "sendPacket: attempt to send package using inactive connection");
        else connection.get().sendJSONPacket(packet);
    }

    @SuppressWarnings("ConstantConditions")
    public void sendAndReceivePacket(JSONObject packetToSend, Consumer<JSONObject> receivedPacketHandler) {
        if (!isConnectionActive()) Log.e(TAG, "sendAndReceivePacket: attempt to send package using inactive connection");
        else connection.get().sendAndReceiveJSONPacket(packetToSend, receivedPacketHandler);
    }

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

    @SuppressWarnings("ConstantConditions")
    public String getComputerName() {
        return connection.get() == null ? null : connection.get().getComputerName();
    }
}
