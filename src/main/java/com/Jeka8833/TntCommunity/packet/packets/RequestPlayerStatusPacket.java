package com.Jeka8833.TntCommunity.packet.packets;

import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketManager;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
import com.Jeka8833.dataBase.TNTClientDB;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RequestPlayerStatusPacket implements Packet {

    private final List<UUID> users = new ArrayList<>();

    @Override
    public void write(PacketOutputStream stream) throws IOException {
        throw new NullPointerException("Fail write packet");
    }

    @Override
    public void read(PacketInputStream stream) throws IOException {
        for (int i = 0; i < stream.readByte(); i++)
            users.add(stream.readUUID());
    }

    @Override
    public void serverProcess(WebSocket socket, TNTUser user) {
        TNTClientDB.readAsync(users, users1 -> PacketManager.serverSend(socket, new SendPlayerStatusPacket(users1)));
    }
}
