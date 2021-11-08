package com.Jeka8833.TntCommunity.packet.packets;

import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class BlockModulesPacket implements Packet {

    private final long modules;

    public BlockModulesPacket() {
        this(0);
    }

    public BlockModulesPacket(final long modules) {
        this.modules = modules;
    }

    @Override
    public void write(PacketOutputStream stream) throws IOException {
        stream.writeLong(modules);
    }

    @Override
    public void read(PacketInputStream stream) throws IOException {
        throw new NullPointerException("Fail read packet");
    }

    @Override
    public void serverProcess(WebSocket socket, TNTUser user) {
        throw new NullPointerException("Fail process packet");
    }
}
