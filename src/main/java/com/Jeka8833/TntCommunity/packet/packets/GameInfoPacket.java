package com.Jeka8833.TntCommunity.packet.packets;

import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class GameInfoPacket implements Packet {

    private String gameInfo;

    @Override
    public void write(PacketOutputStream stream) throws IOException {
        throw new NullPointerException("Fail read packet");
    }

    @Override
    public void read(PacketInputStream stream) throws IOException {
        gameInfo = stream.readUTF();
    }

    @Override
    public void serverProcess(WebSocket socket, TNTUser user) {
        user.gameInfo = gameInfo;
    }
}
