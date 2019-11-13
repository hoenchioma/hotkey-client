package com.rfw.hotkey.live_screen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.rfw.hotkey.net.Connection;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.util.Constants;
import com.rfw.hotkey.util.Device;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public abstract class WiFiLiveScreenReceiver implements LiveScreenReceiver {
    private static final String TAG = "WiFiLiveScreenReceiver";

    private Socket socket;
    private DataInputStream in;
    private volatile boolean running = false;

    private Thread connectionThread;

    private WeakReference<Context> context;

    public WiFiLiveScreenReceiver(Context context) {
        this.context = new WeakReference<>(context);
    }

    /**
     * get the connection type of receiver
     */
    @Override
    public Connection.Type getConnectionType() {
        return Connection.Type.WIFI;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private String getLocalIpAddress() throws IllegalStateException {
        if (context.get() == null)
            throw new IllegalStateException("Cannot find application context");
        WifiManager wifiManager = (WifiManager) context.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) throw new IllegalStateException("Error getting WiFi manager");
        return Device.getWiFiIPAddress(wifiManager);
    }

    @Override
    public void start(int screenSizeX, int screenSizeY, float fps, float compressRatio) {
        try {
            ServerSocket serverSocket = new ServerSocket(0); // bind to any available port
            Log.i(TAG, "start: server port: " + serverSocket.getLocalPort());

            serverSocket.setSoTimeout(Constants.SERVER_SOCKET_TIMEOUT);

            JSONObject packet = new JSONObject()
                    .put("type", "liveScreen")
                    .put("command", "start")
                    .put("ipAddress", getLocalIpAddress())
                    .put("port", serverSocket.getLocalPort())
                    .put("screenSizeX", screenSizeX)
                    .put("screenSizeY", screenSizeY)
                    .put("fps", fps)
                    .put("compressRatio", compressRatio);

            connectionThread = new Thread(() -> {
                try {
                    socket = serverSocket.accept();
                    socket.setSoTimeout(Constants.LiveScreen.SOCKET_RECEIVE_TIMEOUT);
                    in = new DataInputStream(socket.getInputStream());
                } catch (SocketTimeoutException e) {
                    Log.e(TAG, "start: serverSocket.accept() timed out", e);
                    onError(e, false);
                } catch (IOException e) {
                    Log.e(TAG, "start: error connecting to live screen sender", e);
                    onError(e, false);
                }
            });
            connectionThread.start();
            ConnectionManager.getInstance().sendPacket(packet);

            running = true;
            new Receiver().start();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "start: error occurred while staring receiver", e);
            onError(e, true);
        }
    }

    @Override
    public void stop() {
        try {
            JSONObject packet = new JSONObject();

            packet.put("type", "liveScreen");
            packet.put("command", "stop");

            ConnectionManager.getInstance().sendPacket(packet);

            running = false; // turn off receiver

            in.close();
            socket.close();
        } catch (NullPointerException ignored) {
            // eat null pointer exceptions
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "stop: error closing socket", e);
            onError(e, false);
        }
    }

    private class Receiver extends Thread {
        @Override
        public void run() {
            try {
                connectionThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (socket != null && in != null) {
                while (running) {
                    try {
                        int dataLength = in.readInt();
                        if (dataLength == 0) break;

                        if (dataLength > 0) {
                            byte[] buff = new byte[dataLength];
                            in.readFully(buff, 0, buff.length);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length);
                            onFrameReceive(bitmap);
                        } else {
                            running = false; // on receiving a 0 length package close socket
                        }
                    } catch (SocketTimeoutException e) {
                        Log.i(TAG, "Receiver.run: socket receiver timed out");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                running = false;
            } else {
                Log.e(TAG, "Receiver.run: connection not established, cannot start receiver");
                onError(new Exception("Connection not established, cannot start receiver"), true);
            }
        }
    }
}
