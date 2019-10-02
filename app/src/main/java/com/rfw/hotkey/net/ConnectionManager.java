package com.rfw.hotkey.net;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionManager {
    private static final String TAG = ConnectionManager.class.getCanonicalName();

    // singleton instance of class
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

    @Nullable
    public String getComputerName() {
        return computerName;
    }

    synchronized private void connectUtil(String ipAddress, int port) throws IOException {
        socket = new Socket(ipAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        if (handshake()) connected.set(true);
    }

    synchronized private void sendUtil(String message) {
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

    /**
     * Async task to connect to server in the background
     */
    public static class ConnectTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = ConnectTask.class.getCanonicalName();

        private String ipAddress;
        private int port;

        public ConnectTask(String ipAddress, int port) {
            this.ipAddress = ipAddress;
            this.port = port;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ConnectionManager.getInstance().connectUtil(ipAddress, port);
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: Error connecting", e);
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Async task to send packet (in the background)
     */
    public static class SendTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = SendTask.class.getCanonicalName();

        private String message;

        public SendTask(String message) {
            this.message = message;
        }

        public SendTask(JSONObject packet) {
            this(packet.toString());
        }

        @Override
        protected Void doInBackground(Void... args) {
            try {
                ConnectionManager.getInstance().sendUtil(message);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Error sending packet", e);
            }
            return null;
        }
    }
}
