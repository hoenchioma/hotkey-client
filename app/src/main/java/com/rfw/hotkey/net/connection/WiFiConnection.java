package com.rfw.hotkey.net.connection;

import com.rfw.hotkey.util.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Subclass of Connection for WiFi/LAN based connection
 */
public class WiFiConnection extends Connection {
    private static final String TAG = "WiFiConnection";

    private String ipAddress;
    private int port;
    private int connectTimeOut;

    private Socket socket;

    /**
     * Constructs a WiFi based connection using ip address and port
     *
     * @param connectTimeOut the maximum time spent waiting to connect (in milliseconds)
     */
    public WiFiConnection(String ipAddress, int port, int connectTimeOut) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.connectTimeOut = connectTimeOut;
    }

    public WiFiConnection(String ipAddress, int port) {
        this(ipAddress, port, Constants.Net.SOCKET_CONNECT_TIMEOUT);
    }

    @Override
    public Type getType() {
        return Type.WIFI;
    }

    @Override
    protected synchronized void connectUtil() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(ipAddress, port), connectTimeOut);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }


    @Override
    protected synchronized void disconnectUtil() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
