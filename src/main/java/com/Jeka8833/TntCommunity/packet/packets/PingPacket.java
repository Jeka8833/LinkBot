package com.Jeka8833.TntCommunity.packet.packets;

import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketManager;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class PingPacket implements Packet {

    private long time;

    public PingPacket() {
        this(System.currentTimeMillis());
    }

    public PingPacket(final long time) {
        this.time = time;
    }

    @Override
    public void write(PacketOutputStream stream) throws IOException {
        stream.writeLong(time);
    }

    @Override
    public void read(PacketInputStream stream) throws IOException {
        time = stream.readLong();
    }

    @Override
    public void serverProcess(WebSocket socket, final TNTUser user) {
        PacketManager.serverSend(socket, new PingPacket(time));
    }
}
