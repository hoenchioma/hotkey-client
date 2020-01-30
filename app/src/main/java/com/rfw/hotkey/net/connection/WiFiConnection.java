package com.rfw.hotkey.net.connection;

import com.google.common.base.Charsets;
import com.rfw.hotkey.util.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Subclass of Connection for WiFi/LAN based connection
 */
public class WiFiConnection extends Connection {
    private static final String TAG = "WiFiConnection";

    private String ipAddress;
    private int port;
    private int connectTimeOut;

    private DatagramSocket datagramSocket;

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
        datagramSocket = new DatagramSocket();
    }

    @Override
    protected synchronized void sendPacketUtil(JSONObject packet, boolean udp) throws IOException {
        if (udp) {
            try {
                byte[] payload = packet.toString().getBytes(Charsets.UTF_8);
                DatagramPacket datagramPacket = new DatagramPacket(payload, payload.length, InetAddress.getByName(ipAddress), port);
                datagramSocket.send(datagramPacket);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            super.sendPacketUtil(packet, false);
        }
    }

    @Override
    protected synchronized void disconnectUtil() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
