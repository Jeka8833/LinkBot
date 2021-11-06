package com.Jeka8833.dataBase;

import com.Jeka8833.LinkBot.Main;
import com.Jeka8833.TntCommunity.Server;
import com.Jeka8833.TntCommunity.TNTUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TNTClientDB {
    private static final Logger logger = LogManager.getLogger(Server.class);

    private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Set<UUID> readList = Collections.synchronizedSet(new HashSet<>());
    public static final Set<TNTUser> writeList = Collections.synchronizedSet(new HashSet<>());
    private static final List<UpdateDB> callbackList = new ArrayList<>();

    public static void init() {
        final Thread thread = new Thread(() -> {
            while (true) {
                try {
                    readUser();
                } catch (Exception e) {
                    logger.error("Fail read users", e);
                }
                try {
                    writeUser();
                } catch (Exception e) {
                    logger.error("Fail write users", e);
                }
                try {
                    final Iterator<TNTUser> iterator = TNTUser.keyUserList.values().iterator();
                    while (iterator.hasNext()) {
                        final TNTUser user = iterator.next();
                        if (user.isUserDead())
                            iterator.remove();
                        TNTUser.uuidUserList.remove(user.user);
                    }
                } catch (Exception e) {
                    logger.error("Clear old users", e);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.error("Fail sleep... -_-", e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static void readAsync(final List<UUID> users, final UsersCallBack callBack) {
        for (UUID user : users) {
            final TNTUser tntUser = TNTUser.uuidUserList.get(user);
            if (tntUser == null)
                readList.add(user);
            else
                tntUser.heartBeat();
        }
        callbackList.add(() -> {
            final List<TNTUser> outList = new ArrayList<>(users.size());
            for (UUID uuid : users)
                outList.add(TNTUser.uuidUserList.getOrDefault(uuid, new TNTUser(uuid, UUID.randomUUID(), null)));
            callBack.call(outList);
        });
    }

    public static void readUser() throws SQLException {
        Main.db.checkConnect();
        final StringBuilder sb = new StringBuilder("SELECT * FROM \"TC_Users\" WHERE \"user\" IN (");

        final Set<UUID> copy = new HashSet<>(readList);
        readList.clear();

        for (UUID user : copy) {
            sb.append("'").append(user).append("',");
        }
        sb.delete(sb.length() - 1, sb.length()).append(")");

        try (ResultSet resultSet = Main.db.statement.executeQuery(sb.toString())) {
            while (resultSet.next()) {
                final TNTUser userStats = new TNTUser(resultSet.getObject("user", UUID.class),
                        resultSet.getObject("key", UUID.class), resultSet.getString("version"));
                final Date date = resultSet.getTimestamp("timeLogin");
                userStats.timeLogin = date == null ? System.currentTimeMillis() : date.getTime();
                userStats.blockModules = resultSet.getLong("blockModules");
                userStats.donate = resultSet.getByte("donate");
                userStats.status = resultSet.getByte("status");
                TNTUser.addUser(userStats);
            }
        }
        final Iterator<UpdateDB> iterator = callbackList.iterator();
        while (iterator.hasNext()) {
            final UpdateDB updateDB = iterator.next();
            updateDB.call();
            iterator.remove();
        }
    }

    public static void writeUser() throws SQLException {
        Main.db.checkConnect();
        final StringBuilder sb = new StringBuilder("INSERT INTO \"TC_Users\" (\"user\", \"key\", \"version\", \"timeLogin\", \"blockModules\", \"donate\", \"status\") \n" +
                "VALUES ");
        for (TNTUser user : writeList) {
            sb.append("('").append(user.user)
                    .append("','").append(user.key).append("','")
                    .append(user.version).append("','")
                    .append(formatter.format(new Date(user.timeLogin))).append("',")
                    .append(user.blockModules).append(",")
                    .append(user.donate).append(",")
                    .append(user.status).append("),");
        }
        sb.delete(sb.length() - 1, sb.length())
                .append(" ON CONFLICT (\"user\") DO UPDATE SET \"key\" = EXCLUDED.\"key\", \"version\" = EXCLUDED.\"version\", \"timeLogin\" = EXCLUDED.\"timeLogin\", \n" +
                        "\"blockModules\" = EXCLUDED.\"blockModules\", \"donate\" = EXCLUDED.\"donate\", \"status\" = EXCLUDED.\"status\"");
        Main.db.statement.executeUpdate(sb.toString());
    }

    private interface UpdateDB {
        void call();
    }

    public interface UsersCallBack {
        void call(final List<TNTUser> users);
    }

}
