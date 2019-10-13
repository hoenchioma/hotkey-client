package com.rfw.hotkey.net;

import androidx.core.util.Consumer;

import org.json.JSONObject;

/**
 * Connection for specifically packet transfer
 * (mostly for one way packet transfer (sending))
 */
public interface PacketTransferConnection extends Connection {
    /**
     * send a JSON packet asynchronously
     * @param packet JSON object representing the packet to be sent
     */
    void sendPacket(JSONObject packet);

    /**
     * send a JSON packet and receive a response immediately
     * @param receivedPacketHandler function to handle received packet
     */
    void sendAndReceivePacket(JSONObject packetToSend, Consumer<JSONObject> receivedPacketHandler);
}
