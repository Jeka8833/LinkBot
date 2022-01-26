package com.Jeka8833.dataBase;

import com.Jeka8833.TntCommunity.Server;
import com.Jeka8833.TntCommunity.TNTUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TNTClientBDManager {
    private static final Logger logger = LogManager.getLogger(TNTClientBDManager.class);
    private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Map<UUID, UserQuire> users = new HashMap<>();

    public static void init() {
        final Thread thread = new Thread(() -> {
            while (true) {
                try {
                    if (Server.server != null && Server.server.getConnections() != null)
                        logger.info("Current online: " + Server.server.getConnections().size());


                    forceWrite();
                    forceRead();


                    final Iterator<TNTUser> userIterator = TNTUser.keyUserList.values().iterator();
                    while (userIterator.hasNext()) {
                        final TNTUser tntUser = userIterator.next();
                        if (tntUser.isUserDead()) {
                            userIterator.remove();
                            TNTUser.user2key.remove(tntUser.user);
                        }
                    }

                    users.values().removeIf(userQuire -> !userQuire.isNeed());

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        logger.error("Fail sleep... -_-", e);
                    }
                } catch (Exception e) {
                    logger.warn("BD error: ", e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static void forceRead() {
        final List<UserQuire> needRead = users.values().stream().filter(UserQuire::isRead).toList();
        if (!needRead.isEmpty()) {
            try {
                read(needRead);
            } catch (Exception e) {
                logger.warn("Block read error, transition to single read method");
                for (UserQuire quire : needRead) {
                    try {
                        read(Collections.singletonList(quire));
                    } catch (SQLException ex) {
                        logger.warn("User Read Error: " + quire.user + ", error: ", ex);
                    }
                }
            }
        }
    }

    public static void forceWrite() {
        final List<UserQuire> needWrite = users.values().stream().filter(UserQuire::isWrite).toList();
        if (!needWrite.isEmpty()) {
            try {
                write(needWrite);
            } catch (Exception e) {
                logger.warn("Block write error, transition to single write method");
                for (UserQuire quire : needWrite) {
                    try {
                        write(Collections.singletonList(quire));
                    } catch (SQLException ex) {
                        logger.warn("User Write Error: " + quire.user + ", error: ", ex);
                    }
                }
            }
        }
    }

    public static void readOrCashUser(final UUID uuid, final UserCallback callback) {
        if (TNTUser.user2key.containsKey(uuid) && TNTUser.keyUserList.containsKey(TNTUser.user2key.get(uuid)))
            callback.update(TNTUser.keyUserList.get(TNTUser.user2key.get(uuid)));
        else
            readUser(uuid, callback);
    }

    public static void readOrCashUser(final List<UUID> users, final UsersCallback callbackList, final boolean fillDefault) {
        final List<UUID> needRequest = new ArrayList<>();
        final List<TNTUser> returnUsers = new ArrayList<>();
        for (UUID uuid : users) {
            if (TNTUser.user2key.containsKey(uuid) && TNTUser.keyUserList.containsKey(TNTUser.user2key.get(uuid)))
                returnUsers.add(TNTUser.keyUserList.get(TNTUser.user2key.get(uuid)));
            else
                needRequest.add(uuid);
        }
        if (needRequest.isEmpty()) {
            callbackList.update(returnUsers);
        } else {
            readUsers(needRequest, tntUsers -> {
                returnUsers.addAll(tntUsers);
                callbackList.update(returnUsers);
            }, fillDefault);
        }
    }

    private static void read(final List<UserQuire> userList) throws SQLException {
        DatabaseManager.db.checkConnect();
        final StringBuilder sb = new StringBuilder("SELECT * FROM \"TC_Players\" WHERE \"user\" IN (");
        for (UserQuire quire : userList)
            sb.append("'").append(quire.user).append("',");
        sb.delete(sb.length() - 1, sb.length()).append(")");

        try (ResultSet resultSet = DatabaseManager.db.statement.executeQuery(sb.toString())) {
            try {
                while (resultSet.next()) {
                    final TNTUser tntUser = new TNTUser(resultSet.getObject("user", UUID.class),
                            resultSet.getObject("key", UUID.class), resultSet.getString("version"));

                    final Date date = resultSet.getTimestamp("timeLogin");
                    tntUser.timeLogin = date == null ? System.currentTimeMillis() : date.getTime();
                    tntUser.forceBlock = resultSet.getLong("blockModules");
                    tntUser.donate = resultSet.getByte("donate");
                    tntUser.status = resultSet.getByte("status");


                    for (UserQuire quire : userList) {
                        if (quire.user.equals(tntUser.user)) {
                            quire.callRead(tntUser);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("Fail read data");
            }
        }

        for (UserQuire quire : userList)
            quire.callRead(null);
    }

    private static void write(final List<UserQuire> userList) throws SQLException {
        DatabaseManager.db.checkConnect();
        final StringBuilder sb = new StringBuilder("INSERT INTO \"TC_Players\" (\"user\", \"key\", \"version\", " +
                "\"timeLogin\", \"blockModules\", \"donate\", \"status\") VALUES ");
        for (UserQuire quire : userList) {
            final TNTUser user = TNTUser.keyUserList.get(TNTUser.user2key.get(quire.user));
            sb.append("('").append(user.user)
                    .append("','").append(user.key).append("','")
                    .append(user.version).append("','")
                    .append(formatter.format(new Date(user.timeLogin))).append("',")
                    .append(user.forceBlock).append(",")
                    .append(user.donate).append(",")
                    .append(user.status).append("),");
        }
        sb.delete(sb.length() - 1, sb.length())
                .append(" ON CONFLICT (\"user\") DO UPDATE SET \"key\" = EXCLUDED.\"key\", " +
                        "\"version\" = EXCLUDED.\"version\", \"timeLogin\" = EXCLUDED.\"timeLogin\", " +
                        "\"blockModules\" = EXCLUDED.\"blockModules\", \"donate\" = EXCLUDED.\"donate\", " +
                        "\"status\" = EXCLUDED.\"status\"");

        DatabaseManager.db.statement.executeUpdate(sb.toString()); // Throw


        for (UserQuire quire : userList)
            quire.callWrite(TNTUser.keyUserList.get(TNTUser.user2key.get(quire.user)));
    }

    /**
     * @param callbackList May not return some users if there is a failure in the database or other circumstances.
     */
    public static void readUsers(final List<UUID> users, final UsersCallback callbackList, final boolean fillDefault) {
        if (callbackList == null) {
            for (UUID uuid : users) readUser(uuid, null);
        } else {
            final AtomicInteger readCount = new AtomicInteger();
            final List<TNTUser> returnUsers = new ArrayList<>(users.size());
            for (UUID uuid : users) {
                readUser(uuid, tntUser -> {
                    readCount.getAndIncrement();

                    if (tntUser != null) // Check timeout
                        returnUsers.add(tntUser);
                    else if (fillDefault)
                        returnUsers.add(new TNTUser(uuid, null, null));

                    if (users.size() <= readCount.get())
                        callbackList.update(returnUsers);
                });
            }
        }
    }

    /**
     * @param callbackList May not return some users if there is a failure in the database or other circumstances.
     */
    public static void writeUsers(final List<UUID> users, final UsersCallback callbackList) {
        if (callbackList == null) {
            for (UUID uuid : users) writeUser(uuid, null);
        } else {
            final AtomicInteger readCount = new AtomicInteger();
            final List<TNTUser> returnUsers = new ArrayList<>(users.size());
            for (UUID uuid : users) {
                writeUser(uuid, tntUser -> {
                    readCount.getAndIncrement();

                    if (tntUser != null)  // Check timeout
                        returnUsers.add(tntUser);
                    else if (users.size() <= readCount.get())
                        callbackList.update(returnUsers);
                });
            }
        }
    }

    /**
     * @param callback Will return null in callback if the database response has timed out or other circumstances.
     */
    public static void readUser(final UUID uuid, final UserCallback callback) {
        final UserQuire quire = users.getOrDefault(uuid, new UserQuire(uuid));
        quire.needRead();
        if (callback != null)
            quire.addReadCallback(callback);
        users.put(quire.user, quire);
    }

    /**
     * @param callback Will return null in callback if the database response has timed out or other circumstances.
     */
    public static void writeUser(final UUID uuid, final UserCallback callback) {
        final UserQuire quire = users.getOrDefault(uuid, new UserQuire(uuid));
        quire.needWrite();
        if (callback != null)
            quire.addWriteCallback(callback);
        users.put(quire.user, quire);
    }

    private static class UserQuire {
        private final UUID user;
        private final List<UserCallback> readCallbackList = new ArrayList<>();
        private final List<UserCallback> writeCallbackList = new ArrayList<>();
        private final long timeout = System.currentTimeMillis() + 30_000; // Wait 30 second

        private boolean needRead;
        private boolean needWrite;

        public UserQuire(UUID user) {
            this.user = user;
        }

        public boolean isNeed() {
            if (timeout < System.currentTimeMillis()) {
                logger.warn("Timeout get or read information for user: " + user);
                callRead(null);
                callWrite(null);
                return false;
            }
            return needRead || needWrite;
        }

        /*
         * Write always in priority
         * */
        public boolean isWrite() {
            return needWrite && TNTUser.user2key.containsKey(user) &&
                    TNTUser.keyUserList.containsKey(TNTUser.user2key.get(user));
        }

        /*
         * Read is not priority
         * */
        public boolean isRead() {
            return needRead && !isWrite();
        }

        public void callRead(final TNTUser tntUser) {
            for (UserCallback callback : readCallbackList)
                callback.update(tntUser);
            readCallbackList.clear();
            needRead = false;
        }

        public void callWrite(final TNTUser tntUser) {
            for (UserCallback callback : writeCallbackList)
                callback.update(tntUser);
            writeCallbackList.clear();
            needWrite = false;
        }

        /*
         * Throw null in callback when timeout
         * */
        public void addReadCallback(final @Nonnull UserCallback callback) {
            readCallbackList.add(callback);
        }

        /*
         * Throw null in callback when timeout
         * */
        public void addWriteCallback(final @Nonnull UserCallback callback) {
            writeCallbackList.add(callback);
        }

        public void needRead() {
            this.needRead = true;
        }

        public void needWrite() {
            this.needWrite = true;
        }
    }

    public interface UserCallback {
        void update(final @Nullable TNTUser tntUser);
    }

    public interface UsersCallback {
        void update(final List<TNTUser> tntUsers);
    }
}
