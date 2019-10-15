package com.rfw.hotkey.net;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.util.Consumer;
import androidx.databinding.ObservableBoolean;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.rfw.hotkey.util.Utils.getDeviceName;

public class WiFiConnection implements Connection {
    private static final String TAG = "WiFiConnection";

    private ObservableBoolean active = new ObservableBoolean(false);
    private String computerName;

    private String ipAddress;
    private int port;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public WiFiConnection(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public ObservableBoolean getActive() {
        return active;
    }

    @Override
    public Type getType() {
        return Type.WIFI;
    }

    @Override
    public String getComputerName() {
        return computerName;
    }

    synchronized private void connectUtil() throws IOException {
        socket = new Socket(ipAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Exchange device names with server
     */
    private void handshake() throws IOException {
        // send handshake packet
        JSONObject handshakePacket = new JSONObject();
        try {
            handshakePacket.put("type", "handshake");
            handshakePacket.put("deviceName", getDeviceName());
            handshakePacket.put("connectionType", "normal");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendPacketUtil(handshakePacket);

        // receive handshake response packet
        String response = in.readLine();
        try {
            JSONObject receivedPacket = new JSONObject(new JSONTokener(response));
            if (!receivedPacket.getString("type").equals("handshake")) throw new AssertionError();
            computerName = receivedPacket.getString("deviceName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
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
                    Log.i(TAG, "connect.doInBackground: connected successfully to " + computerName);
                } catch (IOException e) {
                    Log.e(TAG, "connect.doInBackground: error connecting", e);
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


    private synchronized void disconnectUtil() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void disconnect() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    disconnectUtil();
                } catch (IOException e) {
                    Log.e(TAG, "disconnect.doInBackground: error while disconnecting", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                onDisconnect();
            }
        }.execute();
    }

    /**
     * Internal helper class to send String messages to server
     * (synchronized to prevent collision while sending)
     *
     * @param packet JSON packet to be sent
     */
    private synchronized void sendPacketUtil(JSONObject packet) {
        out.println(packet);
    }

    /**
     * send packet to server
     *
     * @param packet JSON object to send
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public void sendJSONPacket(JSONObject packet) {
        new AsyncTask<Void, Void, Void>() {
            boolean disconnect = false;

            @Override
            protected Void doInBackground(Void... args) {
                sendPacketUtil(packet);
                try {
                    if (out.checkError()) { // error while sending indicated broken connection
                        Log.e(TAG, "sendJSONPacket.doInBackground: broken connection", new RuntimeException("broken connection"));
                        Log.i(TAG, "sendJSONPacket.doInBackground: closing connection ...");
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

    @SuppressLint("StaticFieldLeak")
    @Override
    public void sendAndReceiveJSONPacket(JSONObject packetToSend, Consumer<JSONObject> receivedPacketHandler) {
        new AsyncTask<Void, Void, Void>() {
            JSONObject receivedPacket = null;
            boolean disconnect = false;

            @Override
            protected Void doInBackground(Void... args) {
                sendPacketUtil(packetToSend);
                try {
                    if (out.checkError()) { // error while sending indicated broken connection
                        Log.e(TAG, "sendAndReceiveJSONPacket.doInBackground: broken connection", new RuntimeException("broken connection"));
                        Log.i(TAG, "sendAndReceiveJSONPacket.doInBackground: closing connection ...");
                        disconnectUtil(); // close connection (broken pipe)
                        disconnect = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    String response = in.readLine();
                    receivedPacket = new JSONObject(new JSONTokener(response));
                } catch (IOException e) { // error while reading from stream indicated broken connection
                    Log.e(TAG, "sendAndReceiveJSONPacket.doInBackground: broken connection", e);
                    Log.i(TAG, "sendAndReceiveJSONPacket.doInBackground: closing connection ...");
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
                        Log.e(TAG, "sendAndReceive.onPostExecute: receivedPacket is null", new RuntimeException());
                    }
                } else {
                    active.set(false);
                    onDisconnect();
                }
            }
        }.execute();
    }
}
