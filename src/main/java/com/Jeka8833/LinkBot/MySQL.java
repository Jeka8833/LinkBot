package com.Jeka8833.LinkBot;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQL {

    public static Integer shiftWeek = 0;
    public static Map<Integer, String> urls = new HashMap<>();
    public static List<User> users = new ArrayList<>();

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
            if (connection.isClosed()) {
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + bdName + ".setting WHERE idsetting = 0;");
            while (resultSet.next()) {
                shiftWeek = resultSet.getInt(3);
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
                users.add(new User(resultSet.getLong(1), resultSet.getByte(2), resultSet.getByte(3)));
            }
            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void write() {
        checkConnection();
        try {
            statement.executeQuery("REPLACE INTO " + bdName + ".setting(idsetting, parametr, value) VALUES(0,'weekShift', " + shiftWeek + ");").close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (Map.Entry<Integer, String> entry : urls.entrySet()) {
            try {
                statement.executeQuery("REPLACE INTO " + bdName + ".link(lessonId, linkcol) VALUES(" + entry.getKey() + ", '" + entry.getValue() + "');").close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        for (User user : users) {
            try {
                statement.executeQuery("REPLACE INTO " + bdName + ".notification(user_id, notification, isAdmin) VALUES(" + user.chatId + ", " + user.isNotification + ", " + user.isAdmin + ");").close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void close() throws SQLException {
        if (statement != null)
            statement.close();
        if (connection != null)
            connection.close();
    }
}
