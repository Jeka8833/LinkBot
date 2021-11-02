package com.Jeka8833.TntCommunity.packet.packets;

import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.List;

public record SendPlayerStatusPacket(List<TNTUser> users) implements Packet {

    @Override
    public void write(PacketOutputStream stream) throws IOException {
        stream.writeByte(users.size());
        for (TNTUser user : users) {
            final boolean isTNTUser = user.key != null;
            stream.writeUUID(user.user);
            stream.writeBoolean(isTNTUser);
            if (isTNTUser) {
                stream.writeByte(user.donate);
                stream.writeUTF(user.version);
                stream.writeByte(switch (user.status) {
                    case TNTUser.STATUS_ONLINE -> 3;
                    case TNTUser.STATUS_AFK -> 2;
                    default -> 0;
                });
            }
        }
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
