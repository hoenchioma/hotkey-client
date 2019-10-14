package com.rfw.hotkey.live_screen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.rfw.hotkey.net.Connection;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static com.rfw.hotkey.util.Utils.getIPAddress;

public abstract class WiFiLiveScreenReceiver implements LiveScreenReceiver {
    private static final String TAG = "WiFiLiveScreenReceiver";

    //    public static final int MAX_DATA_SIZE = 65536;
    public static final int SOCKET_TIMEOUT = 5000; // in milliseconds
    public static final int MAX_SOCKET_TIMEOUT_COUNT = 30;
    public static final float SENDER_FPS = 500;

    private Socket socket;
    private DataInputStream in;
    private volatile boolean running = false;
//    private byte[] buff = new byte[MAX_DATA_SIZE];

    private String localIpAddress;
    private final Object localIpMonitor = new Object();

    private Thread connectionThread;

    public WiFiLiveScreenReceiver() {
        // get the local IP address in separate thread (to avoid network on UI thread)
        new Thread(() -> {
            synchronized (localIpMonitor) {
                localIpAddress = getIPAddress(true);
            }
        }).start();
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

    private String getLocalIpAddress() {
        // wait until the lock is released by the thread setting localIPAddress
        synchronized (localIpMonitor) {
            return localIpAddress;
        }
    }

    @Override
    public void start(int screenSizeX, int screenSizeY) {
        try {
            ServerSocket serverSocket = new ServerSocket(0); // bind to any available port
            Log.i(TAG, "start: server port: " + serverSocket.getLocalPort());

            JSONObject packet = new JSONObject();

            packet.put("type", "liveScreen");
            packet.put("command", "start");
            packet.put("ipAddress", getLocalIpAddress());
            packet.put("port", serverSocket.getLocalPort());
            packet.put("screenSizeX", screenSizeX);
            packet.put("screenSizeY", screenSizeY);
            packet.put("fps", SENDER_FPS);

            connectionThread = new Thread(() -> {
                try {
                    serverSocket.setSoTimeout(SOCKET_TIMEOUT);
                    socket = serverSocket.accept();
                    in = new DataInputStream(socket.getInputStream());
                } catch (SocketTimeoutException e) {
                    Log.e(TAG, "start: ServerSocket timed out", e);
                } catch (IOException e) {
                    Log.e(TAG, "start: error connecting to live screen sender", e);
                }
            });
            connectionThread.start();
            ConnectionManager.getInstance().sendPacket(packet);

            running = true;
            new Receiver().start();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "stop: error closing socket", e);
        }
    }

    private class Receiver extends Thread {
        @Override
        public void run() {
            try {
                connectionThread.join();
                socket.setSoTimeout(SOCKET_TIMEOUT);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int socketTimeoutCount = 0;
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
                    if (++socketTimeoutCount > MAX_SOCKET_TIMEOUT_COUNT) {
                        Log.e(TAG, "Receiver.run: socket idle for too long, exiting ...", e);
                        running = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            running = false;
        }
    }
}
