package com.rfw.hotkey.net.connection;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.databinding.ObservableBoolean;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;

import static com.rfw.hotkey.util.Device.getDeviceName;

public abstract class Connection {
    private static final String TAG = "Connection";

    private static final String SERVER_UUID = "8fbdf1a6-1185-43a7-952a-3f38f6af0c36";
    private static final int SERVER_VERSION = 1;

    private ObservableBoolean active = new ObservableBoolean(false);
    private String computerName;

    protected BufferedReader in;
    protected PrintWriter out;

    public abstract Type getType();

    public ObservableBoolean getActive() {
        return active;
    }

    public String getComputerName() {
        return computerName;
    }

    /**
     * called after connecting
     * (meant to be overridden)
     *
     * @param success      whether the connection was successful
     * @param errorMessage error message if connection was unsuccessful (null otherwise)
     */
    protected void onConnect(boolean success, @Nullable String errorMessage) {}

    /**
     * called after disconnecting
     * (meant to be overridden)
     */
    protected void onDisconnect() {}

    /**
     * Exchange device names with server
     */
    private void handshake() throws IOException, AssertionError {
        // send handshake packet
        try {
            sendPacketUtil(new JSONObject()
                    .put("type", "connectionRequest")
                    .put("deviceName", getDeviceName())
                    .put("connectionType", "normal")
                    .put("serverUuid", SERVER_UUID)
                    .put("serverVersion", SERVER_VERSION)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // receive handshake response packet
        String response = in.readLine();
        try {
            JSONObject receivedPacket = new JSONObject(new JSONTokener(response));
            if (!receivedPacket.getString("type").equals("connectionResponse")) throw new AssertionError();
            if (!receivedPacket.getBoolean("success")) throw new AssertionError("connection unsuccessful");
            if (!receivedPacket.getString("serverUuid").equals(SERVER_UUID) || receivedPacket.getInt("serverVersion") != SERVER_VERSION) {
                throw new AssertionError("server mismatch");
            }
            computerName = receivedPacket.getString("deviceName");
        } catch (JSONException e) {
            throw new AssertionError("error in received packet");
        }
    }

    /**
     * connect to server (based on connection type) asynchronously
     * and perform some sort of handshake (to get computer name)
     */
    @SuppressLint("StaticFieldLeak")
    public void connect() {
        new AsyncTask<Void, Void, Void>() {
            boolean success = false;
            String errorMessage;

            @Override
            protected Void doInBackground(Void... args) {
                try {
                    connectUtil();
                    handshake();
                    success = true;
                    Log.i(Connection.TAG, "connect.doInBackground: connected successfully to " + computerName);
                } catch (AssertionError | IOException e) {
                    Log.e(Connection.TAG, "connect.doInBackground: error connecting", e);
                    success = false;
                    errorMessage = e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void arg) {
                super.onPostExecute(arg);
                if (success) active.set(true);
                onConnect(success, errorMessage);
            }
        }.execute();
    }

    protected abstract void connectUtil() throws IOException;

    /**
     * close the connection
     */
    @SuppressLint("StaticFieldLeak")
    public void disconnect() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    disconnectUtil();
                } catch (IOException e) {
                    Log.e(Connection.TAG, "disconnect.doInBackground: error while disconnecting", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                onDisconnect();
            }
        }.execute();
    }

    protected abstract void disconnectUtil() throws IOException;

    /**
     * send a JSON packet asynchronously
     *
     * @param packet JSON object representing the packet to be sent
     */
    @SuppressLint("StaticFieldLeak")
    public void sendJSONPacket(JSONObject packet) {
        new AsyncTask<Void, Void, Void>() {
            boolean disconnect = false;

            @Override
            protected Void doInBackground(Void... args) {
                sendPacketUtil(packet);
                try {
                    if (out.checkError()) { // error while sending indicated broken connection
                        Log.e(Connection.TAG, "sendJSONPacket.doInBackground: broken connection", new RuntimeException("broken connection"));
                        Log.i(Connection.TAG, "sendJSONPacket.doInBackground: closing connection ...");
                        disconnectUtil(); // close connection (broken pipe)
                        disconnect = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (disconnect) {
                    active.set(false);
                    onDisconnect();
                }
            }
        }.execute();
    }

    /**
     * send a JSON packet and receive a response immediately
     *
     * @param receivedPacketHandler function to handle received packet
     */
    @SuppressLint("StaticFieldLeak")
    public void sendAndReceiveJSONPacket(JSONObject packetToSend, Consumer<JSONObject> receivedPacketHandler) {
        new AsyncTask<Void, Void, Void>() {
            JSONObject receivedPacket = null;
            boolean disconnect = false;

            @Override
            protected Void doInBackground(Void... args) {
                sendPacketUtil(packetToSend);
                try {
                    if (out.checkError()) { // error while sending indicated broken connection
                        Log.e(Connection.TAG, "sendAndReceiveJSONPacket.doInBackground: broken connection", new RuntimeException("broken connection"));
                        Log.i(Connection.TAG, "sendAndReceiveJSONPacket.doInBackground: closing connection ...");
                        disconnectUtil(); // close connection (broken pipe)
                        disconnect = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    String response = in.readLine();
                    receivedPacket = new JSONObject(new JSONTokener(response));
                } catch (SocketTimeoutException e) {
                    Log.e(Connection.TAG, "sendAndReceiveJSONPacket.doInBackground: receive timed out", e);
                } catch (IOException e) { // error while reading from stream indicated broken connection
                    Log.e(Connection.TAG, "sendAndReceiveJSONPacket.doInBackground: broken connection", e);
                    Log.i(Connection.TAG, "sendAndReceiveJSONPacket.doInBackground: closing connection ...");
                    try {
                        disconnectUtil();
                        disconnect = true;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!disconnect) {
                    if (receivedPacket != null) {
                        receivedPacketHandler.accept(receivedPacket);
                    } else {
                        Log.e(Connection.TAG, "sendAndReceive.onPostExecute: receivedPacket is null", new RuntimeException());
                    }
                } else {
                    active.set(false);
                    onDisconnect();
                }
            }
        }.execute();
    }

    protected synchronized void sendPacketUtil(JSONObject packet) {
        out.println(packet);
    }

    /**
     * Defines the network type of the connection
     * (the type of network that will be used to do the transfer)
     */
    public enum Type {
        WIFI,
        BLUETOOTH,
        INTERNET,
        NONE
    }
}
