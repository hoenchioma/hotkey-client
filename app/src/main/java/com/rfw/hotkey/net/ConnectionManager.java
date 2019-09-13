package com.rfw.hotkey.net;

import android.os.Build;
import android.util.Log;

import androidx.databinding.ObservableBoolean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionManager {
    private static final String TAG = "ConnectionManager";

    private static ConnectionManager instance;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String computerName;
    public ObservableBoolean connected = new ObservableBoolean(false);

    private ConnectionManager() { }

    /**
     * Get singleton instance of class through lazy initialization
     * @return singleton instance of class
     */
    synchronized public static ConnectionManager getInstance() {
        if (instance == null) instance = new ConnectionManager();
        return instance;
    }

    public String getComputerName() {
        return computerName;
    }

    synchronized public void connect(String ipAddress, int port) throws IOException {
        socket = new Socket(ipAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        if (handshake()) connected.set(true);
    }

    synchronized public void send(JSONObject message) {
        out.println(message);
    }

    /**
     * Exchange device names with server desktop
     *
     * @return if handshake was successful
     */
    private boolean handshake() {
        try {
            out.println(getDeviceName());
            computerName = in.readLine();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "handshake: Handshake failed closing connection", e);
            return false;
        }
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalizeFirstLetter(model);
        } else {
            return capitalizeFirstLetter(manufacturer) + " " + model;
        }
    }

    private static String capitalizeFirstLetter(String s) {
        if (s == null || s.length() == 0) return "";
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
