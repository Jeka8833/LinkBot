package com.Jeka8833.TntCommunity.packet;

import com.Jeka8833.TntCommunity.TNTUser;
import org.java_websocket.WebSocket;

import java.io.IOException;

public interface Packet {

    void write(final PacketOutputStream stream) throws IOException;

    void read(final PacketInputStream stream) throws IOException;

    void serverProcess(final WebSocket socket, final TNTUser user);
}
