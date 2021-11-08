package com.Jeka8833.dataBase;

import com.Jeka8833.LinkBot.Main;
import com.Jeka8833.LinkBot.User;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkBotDB {

    public static Integer shiftWeek = 0;
    public static Integer onNotification = 0;
    public static final Map<Integer, String> urls = new HashMap<>();
    public static final List<User> users = new ArrayList<>();

    public static void read() {
        DatabaseManager.db.checkConnect();

        try (ResultSet resultSet = DatabaseManager.db.statement.executeQuery("SELECT * FROM \"LB_Setting\"")) {
            while (resultSet.next()) {
                switch (resultSet.getString(1)) {
                    case "weekShift" -> shiftWeek = resultSet.getInt(2);
                    case "notification" -> onNotification = resultSet.getInt(2);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try (ResultSet resultSet = DatabaseManager.db.statement.executeQuery("SELECT * FROM \"LB_Links\"")) {
            while (resultSet.next()) {
                urls.put(resultSet.getInt(1), resultSet.getString(2));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try (ResultSet resultSet = DatabaseManager.db.statement.executeQuery("SELECT * FROM \"LB_Users\"")) {
            users.clear();
            while (resultSet.next()) {
                users.add(new User(resultSet.getLong(1), resultSet.getByte(2),
                        resultSet.getBoolean(3), resultSet.getString(4)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void write() {
        write(Table.LINK);
        write(Table.SETTING);
        write(Table.NOTIFICATION);
    }

    public static void write(final Table table) {
        DatabaseManager.db.checkConnect();
        switch (table) {
            case SETTING:
                try {
                    DatabaseManager.db.statement.executeUpdate("INSERT INTO \"LB_Setting\" (\"Name\", \"Value\") " +
                            "VALUES ('weekShift', " + shiftWeek + "), ('notification', " + onNotification + ") " +
                            "ON CONFLICT (\"Name\") DO UPDATE SET \"Value\" = EXCLUDED.\"Value\"");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case LINK:
                try {
                    final StringBuilder sb = new StringBuilder("INSERT INTO \"LB_Links\" (\"id\", \"link\") VALUES ");
                    for (Map.Entry<Integer, String> entry : urls.entrySet()) {
                        sb.append('(').append(entry.getKey()).append(",'").append(entry.getValue()).append("'),");
                    }
                    sb.delete(sb.length() - 1, sb.length())
                            .append("ON CONFLICT (\"id\") DO UPDATE SET \"link\" = EXCLUDED.\"link\"");
                    DatabaseManager.db.statement.executeUpdate(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case NOTIFICATION:
                try {
                    final StringBuilder sb = new StringBuilder("INSERT INTO \"LB_Users\" (\"id\", \"timeNotification\"," +
                            " \"isAdmin\", \"skipLesson\") VALUES ");
                    for (User user : users) {
                        sb.append('(').append(user.chatId).append(',').append(user.notification).append(',')
                                .append(user.isAdmin).append(",'").append(user.skipLesson).append("'),");
                    }
                    sb.delete(sb.length() - 1, sb.length())
                            .append("ON CONFLICT (\"id\") DO UPDATE SET \"timeNotification\" = EXCLUDED.\"timeNotification\"," +
                                    " \"skipLesson\" = EXCLUDED.\"skipLesson\"");
                    DatabaseManager.db.statement.executeUpdate(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public enum Table {
        LINK,
        NOTIFICATION,
        SETTING
    }

}
