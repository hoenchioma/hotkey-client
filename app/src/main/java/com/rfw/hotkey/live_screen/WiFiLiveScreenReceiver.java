package com.rfw.hotkey.live_screen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.rfw.hotkey.net.Connection;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static com.rfw.hotkey.util.Utils.getIPAddress;

public abstract class WiFiLiveScreenReceiver implements LiveScreenReceiver {
    private static final String TAG = "WiFiLiveScreenReceiver";

    public static final int MAX_DATA_SIZE = 65536;
    public static final int SOCKET_TIMEOUT = 1000; // in milliseconds
    public static final int MAX_SOCKET_TIMEOUT_COUNT = 30;

    private DatagramSocket socket;
    private volatile boolean running = false;
    private DatagramPacket dataBuffPacket;

    private String localIpAddress;
    private final Object localIpMonitor = new Object();

    public WiFiLiveScreenReceiver() throws SocketException {
        socket = new DatagramSocket();
        byte[] dataBuff = new byte[MAX_DATA_SIZE];
        dataBuffPacket = new DatagramPacket(dataBuff, dataBuff.length);
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
            JSONObject packet = new JSONObject();

            packet.put("type", "liveScreen");
            packet.put("command", "start");
            packet.put("ipAddress", getLocalIpAddress());
            packet.put("port", socket.getLocalPort());
            packet.put("screenSizeX", screenSizeX);
            packet.put("screenSizeY", screenSizeY);

            ConnectionManager.getInstance().sendPacket(packet);

            running = true;
            new Receiver().start();
        } catch (JSONException e) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class Receiver extends Thread {
        @Override
        public void run() {
            try {
                socket.setSoTimeout(SOCKET_TIMEOUT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            int socketTimeoutCount = 0;
            while (running) {
                try {
                    socket.receive(dataBuffPacket);
                    if (dataBuffPacket.getLength() == 0) break;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(
                            dataBuffPacket.getData(), 0, dataBuffPacket.getLength());
//                    Log.i(TAG, "run: image size: " + dataBuffPacket.getLength());
                    onFrameReceive(bitmap);
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
