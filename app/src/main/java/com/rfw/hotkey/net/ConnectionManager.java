package com.rfw.hotkey.net;

import androidx.databinding.ObservableField;

import org.json.JSONObject;

import java.util.Objects;

public class ConnectionManager {
    private static final String TAG = ConnectionManager.class.getCanonicalName();

    // singleton instance of class
    private static ConnectionManager instance;

    public ObservableField<Connection> connection = new ObservableField<>();

    private ConnectionManager() {
    }

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
        connection.connect(true);
    }

    public void setConnection(Connection connection) {
        this.connection.set(connection);
    }

    public boolean isConnectionActive() {
        return connection.get() != null && Objects.requireNonNull(connection.get()).active.get();
    }

    public void sendPacket(JSONObject packet) {
        Objects.requireNonNull(connection.get()).sendPacket(packet);
    }
}
