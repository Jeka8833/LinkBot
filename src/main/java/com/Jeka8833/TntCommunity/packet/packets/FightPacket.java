package com.Jeka8833.TntCommunity.packet.packets;

import com.Jeka8833.TntCommunity.Server;
import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class FightPacket implements Packet {

    private int playerFight = 0;

    private final Collection<WebSocket> activeConnection;

    public FightPacket() {
        this(null);
    }

    public FightPacket(Collection<WebSocket> activeConnection) {
        this.activeConnection = activeConnection;
    }

    @Override
    public void write(PacketOutputStream stream) throws IOException {
        final List<TNTUser> users = activeConnection.stream()
                .map(webSocket -> TNTUser.keyUserList.get((UUID) webSocket.getAttachment()))
                .filter(tntUser -> tntUser != null && tntUser.fight != 0).toList();

        stream.writeByte(users.size());
        for (TNTUser user : users) {
            stream.writeUUID(user.user);
            stream.writeInt(user.fight);
        }
    }

    @Override
    public void read(PacketInputStream stream) throws IOException {
        playerFight = stream.readInt();
    }

    @Override
    public void serverProcess(WebSocket socket, TNTUser user) {
        user.fight = playerFight;
        Server.serverSend(socket, new FightPacket(Server.server.getConnections()));
    }
}
