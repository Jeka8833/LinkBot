package com.Jeka8833.LinkBot;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQL {

    public static Integer shiftWeek = 0;
    public static Integer onNotification = 0;
    public static final Map<Integer, String> urls = new HashMap<>();
    public static final List<User> users = new ArrayList<>();

    private static Connection connection;
    private static Statement statement;
    private static String host;
    private static String userName;
    private static String password;
    private static String bdName;

    public static void connect(final String host, final String userName, final String password, final String bdName) throws SQLException {
        MySQL.host = host;
        MySQL.userName = userName;
        MySQL.password = password;
        MySQL.bdName = bdName;
        reconnect();
    }

    private static void checkConnection() {
        try {
            if (!connection.isValid(1)) {
                reconnect();
            }
        } catch (SQLException throwables) {
            try {
                reconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void reconnect() throws SQLException {
        close();
        connection = DriverManager.getConnection("jdbc:mysql://" + host, userName, password);
        statement = connection.createStatement();
    }

    public static void read() {
        checkConnection();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + bdName + ".setting");
            while (resultSet.next()) {
                switch (resultSet.getInt(1)) {
                    case 0:
                        shiftWeek = resultSet.getInt(3);
                        break;
                    case 1:
                        onNotification = resultSet.getInt(3);
                        break;
                }
            }
            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + bdName + ".link;");
            while (resultSet.next()) {
                urls.put(resultSet.getInt(1), resultSet.getString(2));
            }
            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + bdName + ".notification;");
            users.clear();
            while (resultSet.next()) {
                users.add(new User(resultSet.getLong(1), resultSet.getByte(2), resultSet.getByte(3), resultSet.getString(4)));
            }
            resultSet.close();
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
        checkConnection();
        switch (table) {
            case SETTING:
                try {
                    statement.executeUpdate("REPLACE INTO " + bdName + ".setting(idsetting, parametr, value) VALUES(0,'weekShift', " + shiftWeek + ");");
                    statement.executeUpdate("REPLACE INTO " + bdName + ".setting(idsetting, parametr, value) VALUES(1,'notification', " + onNotification + ");");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case LINK:
                for (Map.Entry<Integer, String> entry : urls.entrySet()) {
                    try {
                        statement.executeUpdate("REPLACE INTO " + bdName + ".link(lessonId, linkcol) VALUES(" + entry.getKey() + ", '" + entry.getValue() + "');");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            case NOTIFICATION:
                for (User user : users) {
                    try {
                        statement.executeUpdate("REPLACE INTO " + bdName + ".notification(user_id, notification, isAdmin, skipLesson) VALUES(" + user.chatId + ", " + user.notification + ", " + user.isAdmin + ", '" + user.skipLesson + "');");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
        }
    }

    public static void close() throws SQLException {
        if (statement != null)
            statement.close();
        if (connection != null)
            connection.close();
    }

    public enum Table {
        LINK,
        NOTIFICATION,
        SETTING
    }
}
