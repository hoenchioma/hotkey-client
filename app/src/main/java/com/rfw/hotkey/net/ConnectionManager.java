package com.rfw.hotkey.net;

import androidx.core.util.Consumer;
import androidx.databinding.ObservableField;

import org.json.JSONObject;

import java.util.Objects;

public class ConnectionManager {
    private static final String TAG = ConnectionManager.class.getCanonicalName();

    // singleton instance of class
    private static ConnectionManager instance;

    public ObservableField<PacketTransferConnection> connection = new ObservableField<>();

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

    public void makeConnection(PacketTransferConnection connection) {
        setConnection(connection);
        connection.connect();
    }

    public void setConnection(PacketTransferConnection connection) {
        this.connection.set(connection);
    }

    public boolean isConnectionActive() {
        return connection.get() != null && Objects.requireNonNull(connection.get()).getActive().get();
    }

    public void sendPacket(JSONObject packet) {
        Objects.requireNonNull(connection.get()).sendPacket(packet);
    }

    public void sendAndReceivePacket(JSONObject packetToSend, Consumer<JSONObject> receivedPacketHandler) {
        Objects.requireNonNull(connection.get()).sendAndReceivePacket(packetToSend, receivedPacketHandler);
    }

    public void closeConnection() throws IllegalArgumentException {
        if (connection.get() == null) throw new IllegalArgumentException("connection cannot be closed, as connection does not exist");
        connection.get().disconnect();
        connection.set(null);
    }
}
