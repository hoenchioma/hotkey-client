package com.rfw.hotkey.net.connection;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.databinding.ObservableBoolean;

import com.rfw.hotkey.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.rfw.hotkey.util.Device.getDeviceName;

/**
 * Contains necessary info and I/O streams related to a connection
 *
 * @author Raheeb Hassan
 */
public abstract class Connection {
    private static final String TAG = "Connection";

    // unique identifier for corresponding server (must match with server to work)
    private static final String SERVER_UUID = "8fbdf1a6-1185-43a7-952a-3f38f6af0c36";
    // version of corresponding server (must match with server to work)
    private static final int SERVER_VERSION = 2;

    private ObservableBoolean active = new ObservableBoolean(false);
    private String computerName;

    protected BufferedReader in;
    protected PrintWriter out;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

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
            e.printStackTrace();
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
    public void sendJSONPacket(@NonNull JSONObject packet) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... args) {
                boolean disconnect = false;

                sendPacketUtil(packet);
                if (out.checkError()) { // error while sending indicated broken connection
                    Log.e(Connection.TAG, "sendJSONPacket.doInBackground: broken connection", new RuntimeException("broken connection"));
                    Log.i(Connection.TAG, "sendJSONPacket.doInBackground: closing connection ...");
                    try {
                        disconnectUtil(); // close connection (broken pipe)
                    } catch (IOException e) {
                        Log.e(TAG, "sendJSONPacket.doInBackground: error disconnecting", e);
                    }
                    disconnect = true;
                }

                return disconnect;
            }

            @Override
            protected void onPostExecute(Boolean disconnect) {
                if (disconnect) {
                    active.set(false);
                    onDisconnect();
                }
            }
        }.execute();
    }

    /**
     * send a JSON packet and receive a response immediately (uses default timeout)
     *
     * @param receivedPacketHandler function to handle received packet
     *                              it may receive null if packet receive failed
     * @param timeOut time in milliseconds after which the receive times out
     */
    @SuppressLint("StaticFieldLeak")
    public void sendAndReceiveJSONPacket(@NonNull JSONObject packetToSend,
                                         @NonNull Consumer<JSONObject> receivedPacketHandler,
                                         long timeOut) {
        new AsyncTask<Void, Void, Pair<JSONObject, Boolean>>() {

            @Override
            protected Pair<JSONObject, Boolean> doInBackground(Void... args) {
                JSONObject receivedPacket = null;
                AtomicBoolean disconnect = new AtomicBoolean(false);

                sendPacketUtil(packetToSend);
                if (out.checkError()) { // error while sending indicated broken connection
                    Log.e(Connection.TAG, "sendAndReceiveJSONPacket.doInBackground (sending): broken connection", new RuntimeException("broken connection"));
                    Log.i(Connection.TAG, "sendAndReceiveJSONPacket.doInBackground (sending): closing connection ...");
                    try {
                        disconnectUtil(); // close connection (broken pipe)
                    } catch (IOException e) {
                        Log.e(TAG, "sendAndReceiveJSONPacket.doInBackground (sending): error disconnecting", e);
                    }
                    disconnect.set(true);
                }

                if (!disconnect.get()) {
                    try {
                        Future<String> future = executor.submit(() -> {
                            try {
                                String line =  in.readLine();
                                if (line == null) throw new IOException("socket disconnected");
                                return line;
                            } catch (IOException e) { // error while reading from stream indicated broken connection
                                Log.e(Connection.TAG, "sendAndReceiveJSONPacket.doInBackground (receiving): broken connection", e);
                                Log.i(Connection.TAG, "sendAndReceiveJSONPacket.doInBackground (receiving): closing connection ...");
                                try {
                                    disconnectUtil();
                                } catch (IOException ex) {
                                    Log.e(TAG, "sendAndReceiveJSONPacket.doInBackground (receiving): error disconnecting", e);
                                }
                                disconnect.set(true);
                                return null;
                            }
                        });
                        String response = future.get(timeOut, TimeUnit.MILLISECONDS);
                        if (response != null) receivedPacket = new JSONObject(new JSONTokener(response));
                    } catch (JSONException e) {
                        Log.e(TAG, "sendAndReceiveJSONPacket.doInBackground (receiving): invalid packet (could not parse JSON)", e);
                    } catch (TimeoutException e) {
                        Log.e(Connection.TAG, "sendAndReceiveJSONPacket.doInBackground (receiving): receive timed out", e);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                return new Pair<>(receivedPacket, disconnect.get());
            }

            @Override
            protected void onPostExecute(Pair<JSONObject, Boolean> result) {
                JSONObject receivedPacket = result.first;
                boolean disconnect = result.second == null ? false : result.second;
                if (disconnect) {
                    active.set(false);
                    onDisconnect();
                }
                if (receivedPacket == null) {
                    Log.e(Connection.TAG, "sendAndReceive.onPostExecute: receivedPacket is null");
                }
                receivedPacketHandler.accept(receivedPacket);
            }
        }.execute();
    }

    /**
     * send a JSON packet and receive a response immediately
     * (receive times out after default receive timeout period)
     *
     * @param receivedPacketHandler function to handle received packet
     *                              it may receive null if packet receive failed
     */
    @SuppressLint("StaticFieldLeak")
    public void sendAndReceiveJSONPacket(@NonNull JSONObject packetToSend,
                                         @NonNull Consumer<JSONObject> receivedPacketHandler) {
        sendAndReceiveJSONPacket(packetToSend, receivedPacketHandler, Constants.Net.SOCKET_RECEIVE_TIMEOUT);
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
        NONE; // not connected or intermediary state

        @Override
        public @NonNull String toString() {
            switch (this) {
                case WIFI: return "WiFi";
                case BLUETOOTH: return "Bluetooth";
                case INTERNET: return "Internet";
                case NONE: return "None";
                default: throw new IllegalArgumentException();
            }
        }

        public @NonNull String toCamelCaseString() {
            switch (this) {
                case WIFI: return "wiFi";
                case BLUETOOTH: return "bluetooth";
                case INTERNET: return "internet";
                case NONE: return "none";
                default: throw new IllegalArgumentException();
            }
        }
    }
}
