package com.Jeka8833.TntCommunity.packet.packets;

import com.Jeka8833.TntCommunity.Server;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.util.Util;
import com.Jeka8833.dataBase.TNTClientBDManager;
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
            socket.close(101, "Auth server is overloading");
        } else {
            executor.execute(() -> {
                if (Util.checkKey(this.user, key)) {
                    socket.setAttachment(key);

                    TNTClientBDManager.readOrCashUser(this.user, tntUser -> {
                        if (tntUser == null) {
                            socket.close(103, "BD Down");
                        } else {
                            tntUser.key = key;
                            tntUser.version = version;
                            tntUser.timeLogin = System.currentTimeMillis();
                            TNTUser.addUser(tntUser);
                            TNTClientBDManager.writeUser(this.user, null);
                            Server.serverSend(socket, new BlockModulesPacket(tntUser.forceBlock, tntUser.forceActive));
                        }
                    });
                } else {
                    socket.close(102, "Fail login, maybe Hypixel API down");
                }
            });
        }
    }
}
