package com.Jeka8833.TntCommunity.packet.packets;

import com.Jeka8833.TntCommunity.Server;
import com.Jeka8833.TntCommunity.TNTUser;
import com.Jeka8833.TntCommunity.packet.Packet;
import com.Jeka8833.TntCommunity.packet.PacketInputStream;
import com.Jeka8833.TntCommunity.packet.PacketOutputStream;
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

    private int closeCode;
    private String closeMessage;

    public AuthPacket() {

    }

    public AuthPacket(int closeCode, String closeMessage) {
        this.closeCode = closeCode;
        this.closeMessage = closeMessage;
    }

    @Override
    public void write(final PacketOutputStream stream) throws IOException {
        stream.write(closeCode);
        stream.writeUTF(closeMessage);
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
            Server.serverSend(socket, new AuthPacket(5, "This server is overloading"));
            socket.close();
        } else {
            if (TNTUser.keyUserList.containsKey(key)) {
                final UUID realUUID = TNTUser.keyUserList.get(key).user;
                TNTUser.removeUser(realUUID);
            }

            executor.execute(() -> {
                int status = Util.checkKey(this.user, key);
                switch (status) {
                    case Util.GOOD_AUTH -> {
                        socket.setAttachment(key);

                        TNTClientBDManager.readOrCashUser(this.user, tntUser -> {
                            final TNTUser account = tntUser == null ? new TNTUser(this.user, this.key, this.version) : tntUser;
                            account.key = key;
                            account.version = version;
                            account.timeLogin = System.currentTimeMillis();
                            TNTUser.addUser(account);
                            TNTClientBDManager.writeUser(this.user, null);
                            Server.serverSend(socket, new BlockModulesPacket(account.forceBlock, account.forceActive));
                        });
                    }
                    case Util.FAIL_AUTH -> {
                        Server.serverSend(socket, new AuthPacket(Util.FAIL_AUTH,
                                "Fail authentication, incorrect user"));
                        socket.close();
                    }
                    case Util.FAIL_CONNECTION -> {
                        Server.serverSend(socket, new AuthPacket(Util.FAIL_CONNECTION,
                                "Internal server error"));
                        socket.close();
                    }
                    case Util.KEY_THROTTLING -> {
                        Server.serverSend(socket, new AuthPacket(Util.KEY_THROTTLING,
                                "Key throttling"));
                        socket.close();
                    }
                    case Util.FAIL_PARSE -> {
                        Server.serverSend(socket, new AuthPacket(Util.FAIL_PARSE,
                                "Fail process the server data"));
                        socket.close();
                    }
                }
            });
        }
    }
}
