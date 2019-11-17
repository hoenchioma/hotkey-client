package com.rfw.hotkey.net;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimerTask;

/**
 * A TimerTask to send a dummy packet to check if the connection is still alive
 *
 * @author Raheeb Hassan
 */
public class ConnectionHeartbeat extends TimerTask {
    private static final String TAG = "ConnectionHeartbeat";

    private static final int INVALID_PACKET_THRESHOLD = 5;
    private static final long LATENCY_REPORT_INTERVAL = 60000; // ms

    private static long globalStartTime = System.currentTimeMillis();
    private static double avgLatency = 0;
    private static int count = 0;
    private static int invalidPacketCount = 0;

    @Override
    public void run() {
        if (ConnectionManager.getInstance().isConnectionActive()) {
            long startTime = System.currentTimeMillis();
            try {
                ConnectionManager.getInstance().sendAndReceivePacket(
                        new JSONObject()
                                .put("type", "ping")
                                .put("pingBack", true),
                        receivedPacket -> {
                            try {
                                if (!receivedPacket.getString("type").equals("ping")) {
                                    throw new IllegalArgumentException();
                                }

                                double latency = (System.currentTimeMillis() - startTime) / 2.0;
                                avgLatency = (avgLatency * count + latency) / (++count);

                                if (System.currentTimeMillis() - globalStartTime > LATENCY_REPORT_INTERVAL) {
                                    Log.i(TAG, "run: " + String.format("connection latency %.2f ms", avgLatency));

                                    globalStartTime = System.currentTimeMillis();
                                    avgLatency = count = 0;
                                    invalidPacketCount = 0;
                                }
                            } catch (Exception e) {
                                invalidPacketCount++;
                                Log.e(TAG, "run: error in ping packet", e);

                                if (invalidPacketCount > INVALID_PACKET_THRESHOLD) {
                                    ConnectionManager.getInstance().closeConnection();
                                    invalidPacketCount = 0;
                                }
                            }
                        }
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
