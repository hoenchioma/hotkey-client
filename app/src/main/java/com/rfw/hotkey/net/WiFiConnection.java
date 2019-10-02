package com.rfw.hotkey.net;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class WiFiConnection implements Connection {
    private static long MAX_IDLE_TIME = 2000;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String computerName;
    public ObservableBoolean active = new ObservableBoolean(false);

    private Timer idleTimer;

    /**
     * send packet to server
     * @param packet JSON object to send
     */
    @Override
    public void sendPacket(JSONObject packet) {

    }

    @Nullable
    @Override
    public String getComputerName() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    private class DisconnectTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (socket.isConnected()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
