package com.Jeka8833.TntCommunity.packet.packets;

import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class ActiveModulesPacket implements Packet {

    private long activeModules = 0;

    @Override
    public void write(PacketOutputStream stream) throws IOException {
        throw new NullPointerException("Fail write packet");
    }

    @Override
    public void read(PacketInputStream stream) throws IOException {
        activeModules = stream.readLong();
    }

    @Override
    public void serverProcess(WebSocket socket, final TNTUser user) {
        user.activeModules = activeModules;
    }
}
