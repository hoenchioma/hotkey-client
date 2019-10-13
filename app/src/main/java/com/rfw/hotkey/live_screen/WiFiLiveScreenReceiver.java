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

import static com.rfw.hotkey.util.Utils.getIPAddress;
import static com.rfw.hotkey.util.Utils.intFromByteArray;

public abstract class WiFiLiveScreenReceiver implements LiveScreenReceiver {
    private static final String TAG = "WiFiLiveScreenReceiver";

    private static final int MAX_DATA_SIZE = 65536;

    private DatagramSocket socket;
    private volatile boolean running = false;
    private DatagramPacket dataBuffPacket;

    private String localIpAddress;

    public WiFiLiveScreenReceiver() throws SocketException, InterruptedException {
        socket = new DatagramSocket();
        byte[] dataBuff = new byte[MAX_DATA_SIZE];
        dataBuffPacket = new DatagramPacket(dataBuff, dataBuff.length);
        // get the local IP address in separate thread
        Thread temp = new Thread(() -> localIpAddress = getIPAddress(true));
        temp.start(); temp.join();
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

    @Override
    public void start(int screenSizeX, int screenSizeY) {
        try {
            JSONObject packet = new JSONObject();

            packet.put("type", "liveScreen");
            packet.put("command", "start");
            packet.put("ipAddress", localIpAddress);
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
            running = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class Receiver extends Thread {
        @Override
        public void run() {
            while (running) {
                try {
                    socket.receive(dataBuffPacket);
                    if (dataBuffPacket.getLength() == 0) break;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(dataBuffPacket.getData(), 0, dataBuffPacket.getLength());
                    Log.i(TAG, "run: image size: " + dataBuffPacket.getLength());
                    onFrameReceive(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
