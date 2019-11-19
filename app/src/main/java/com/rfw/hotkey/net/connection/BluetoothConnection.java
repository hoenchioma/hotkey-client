package com.rfw.hotkey.net.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * Subclass of Connection for bluetooth based connection
 *
 * @author Raheeb Hassan
 */
public class BluetoothConnection extends Connection {
    // the unique UUID used by server for hosting the bluetooth device
    private static final String BLUETOOTH_SERVICE_UUID = "35ba7039-f3f2-4617-91f9-64c9c56bb437";

    private BluetoothDevice device;
    private BluetoothSocket socket;

    /**
     * Constructs a bluetooth based connection with specified device
     *
     * @param device the bt device to connect to
     */
    public BluetoothConnection(BluetoothDevice device) {
        this.device = device;
    }

    /**
     * Find device with specified bluetooth address and make connection with it
     *
     * @param bluetoothAddress bluetooth address of the device to connect to
     */
    public BluetoothConnection(String bluetoothAddress) {
        this(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothAddress));
    }

    @Override
    public Type getType() {
        return Type.BLUETOOTH;
    }

    @Override
    protected synchronized void connectUtil() throws IOException {
        UUID uuid = UUID.fromString(BLUETOOTH_SERVICE_UUID);
        socket = device.createRfcommSocketToServiceRecord(uuid);
        socket.connect();
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
