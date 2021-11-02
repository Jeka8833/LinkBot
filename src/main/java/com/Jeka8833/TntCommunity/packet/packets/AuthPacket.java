package com.Jeka8833.TntCommunity.packet.packets;

import com.Jeka8833.TntCommunity.HypixelAPI;
import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AuthPacket implements Packet {

    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    public UUID user;
    public UUID key;
    public String version;

    @Override
    public void write(final PacketOutputStream stream) throws IOException {
        throw new NullPointerException("Fail write packet");
    }

    @Override
    public void read(final PacketInputStream stream) throws IOException {
        user = stream.readUUID();
        key = stream.readUUID();
        version = stream.readUTF();
    }

    @Override
    public void serverProcess(final WebSocket socket, final TNTUser user) {
        if (executor.getQueue().size() == executor.getMaximumPoolSize()) {
            socket.close(1, "Auth server is overloading");
            return;
        }
        executor.execute(() -> {
            if (HypixelAPI.checkKey(this.user, key)) {
                socket.setAttachment(key);
                TNTUser.login(this.user);
            } else {
                socket.close(2, "Fail login, maybe Hypixel API down");
            }
        });
    }
}
