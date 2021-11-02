package com.Jeka8833.TntCommunity;

import com.Jeka8833.dataBase.TNTClientDB;

import java.util.*;

public class TNTUser {

    public static final byte STATUS_ONLINE = 3;
    public static final byte STATUS_AFK = 2;
    public static final byte STATUS_INVISIBLE = 1;
    public static final byte STATUS_OFFLINE = 0;

    public final UUID user;
    public final UUID key;
    public final String version;

    public long timeLogin;
    public long lastTimePacket;

    public long activeModules;
    public long blockModules;

    public byte donate;
    public byte status;

    public boolean needWrite;

    public TNTUser(final UUID user, final UUID key, final String version) {
        this.user = user;
        this.key = key;
        this.version = version;
    }

    public static void login(final UUID user) {
        TNTClientDB.readAsync(Collections.singletonList(user), users -> {
            if (users.isEmpty())
                throw new NullPointerException("Returned collection is empty");
            final TNTUser tntUser = users.get(0);
            tntUser.timeLogin = System.currentTimeMillis();
            TNTClientDB.writeList.add(tntUser);
        });
    }

    public void heartBeat() {
        lastTimePacket = System.currentTimeMillis();
    }

    public static void addUser(final TNTUser tntUser) {
        keyUserList.put(tntUser.key, tntUser);
        uuidUserList.put(tntUser.user, tntUser);
    }

    public static final Map<UUID, TNTUser> keyUserList = new HashMap<>();
    public static final Map<UUID, TNTUser> uuidUserList = new HashMap<>();
}
