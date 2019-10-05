package com.rfw.hotkey.net;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.util.Supplier;

import com.google.android.material.snackbar.Snackbar;
import com.rfw.hotkey.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.rfw.hotkey.util.Utility.getDeviceName;

public class WiFiConnection extends Connection {
    private static final String TAG = "WiFiConnection";

    private String ipAddress;
    private int port;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public WiFiConnection(String ipAddress, int port, @Nullable Supplier<View> contextViewSupplier) {
        super("Wi-Fi", contextViewSupplier);
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void connect(boolean showMessage) {
        new AsyncTask<Void, Void, Void>() {
            boolean success = false;

            @Override
            protected Void doInBackground(Void... args) {
                try {
                    connectUtil();
                    handshake();
                    success = true;
                    Log.i(TAG, "doInBackground: connected successfully to " + computerName);
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: error connecting", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void arg) {
                super.onPostExecute(arg);
                if (showMessage && contextViewSupplier != null && contextViewSupplier.get() != null) {
                    try {
                        Snackbar.make(contextViewSupplier.get(),
                                success ? R.string.connection_success : R.string.connection_error,
                                Snackbar.LENGTH_SHORT
                        ).show();
                    } catch (Exception e) {
                        Log.e(TAG, "onPostExecute: failed to create snackbar to show connection result", e);
                    }
                }
                active.set(true);
            }
        }.execute();
    }

    synchronized private void connectUtil() throws IOException {
        socket = new Socket(ipAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Exchange device names with server
     */
    synchronized private void handshake() throws IOException {
        out.println(getDeviceName());
        computerName = in.readLine();
    }

    synchronized private void disconnect() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

    @Override
    public void close() throws IOException {
        disconnect();
        active.set(false);
        if (contextViewSupplier != null && contextViewSupplier.get() != null) {
            // make snackbar to show that connection is closed
            try {
                Snackbar.make(contextViewSupplier.get(), R.string.connection_closed, Snackbar.LENGTH_SHORT);
            } catch (Exception e) {
                Log.e(TAG, "close: failed to create snackbar to notify connection closed", e);
            }
        }
    }

    /**
     * send packet to server
     *
     * @param packet JSON object to send
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public void sendPacket(JSONObject packet) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                try {
                    sendUtil(packet.toString());

                    // check for server disconnect (broken pipe)
                    if (out.checkError()) {
                        Log.e(TAG, "SendTask.doInBackground: error sending packet");
                        close(); // close connection (broken pipe)
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    /**
     * Internal helper class to send String messages to server
     * (synchronized to prevent collision while sending)
     *
     * @param message String to be sent
     */
    synchronized private void sendUtil(String message) {
        out.println(message);
    }
}
