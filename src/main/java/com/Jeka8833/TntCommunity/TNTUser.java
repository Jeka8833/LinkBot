package com.Jeka8833.TntCommunity;

import com.Jeka8833.TntCommunity.packet.packets.BlockModulesPacket;
import com.Jeka8833.dataBase.TNTClientDB;
import org.java_websocket.WebSocket;

import java.util.*;

public class TNTUser {

    public static final byte STATUS_ONLINE = 3;
    public static final byte STATUS_AFK = 2;
    public static final byte STATUS_INVISIBLE = 1;
    public static final byte STATUS_OFFLINE = 0;

    public final UUID user;
    public final UUID key;
    public String version;
    public String gameInfo;

    public long timeLogin;
    private long lastTimePacket;

    public long activeModules;

    public long forceBlock;
    public long forceActive;

    public byte donate;
    public byte status;

    public int fight;

    public TNTUser(final UUID user, final UUID key, final String version) {
        this.user = user;
        this.key = key;
        this.version = version;
    }

    public static void login(final WebSocket socket, final UUID user, final String version) {
        TNTClientDB.readAsync(Collections.singletonList(user), users -> {
            if (users.isEmpty())
                throw new NullPointerException("Returned collection is empty");
            final TNTUser tntUser = users.get(0);
            tntUser.version = version;
            tntUser.timeLogin = System.currentTimeMillis();
            TNTClientDB.writeList.add(tntUser);
            Server.serverSend(socket, new BlockModulesPacket(tntUser.forceBlock, tntUser.forceActive));
            TNTUser.addUser(tntUser);
        });
    }

    public void heartBeat() {
        lastTimePacket = System.currentTimeMillis();
        System.out.println(user + " " + activeModules);
    }

    public boolean isUserDead() {
        return System.currentTimeMillis() - lastTimePacket > 30_000;
    }

    public boolean isClient() {
        return version != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TNTUser tntUser)) return false;

        return Objects.equals(user, tntUser.user);
    }

    @Override
    public int hashCode() {
        return user != null ? user.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TNTUser{" +
                "user=" + user +
                ", key=" + key +
                ", version='" + version + '\'' +
                ", gameInfo='" + gameInfo + '\'' +
                ", timeLogin=" + timeLogin +
                ", lastTimePacket=" + lastTimePacket +
                ", activeModules=" + activeModules +
                ", forceBlock=" + forceBlock +
                ", forceActive=" + forceActive +
                ", donate=" + donate +
                ", status=" + status +
                ", fight=" + fight +
                '}';
    }

    public static void addUser(final TNTUser tntUser) {
        keyUserList.put(tntUser.key, tntUser);
        uuidUserList.put(tntUser.user, tntUser);
        tntUser.heartBeat();
    }

    public static final Map<UUID, TNTUser> keyUserList = new HashMap<>();
    public static final Map<UUID, TNTUser> uuidUserList = new HashMap<>();
}
