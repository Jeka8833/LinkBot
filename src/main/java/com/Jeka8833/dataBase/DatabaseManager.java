package com.Jeka8833.dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private Connection connection;
    public Statement statement;

    private final String host;
    private final String userName;
    private final String password;

    public DatabaseManager(String host, String userName, String password) {
        this.host = host;
        this.userName = userName;
        this.password = password;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(host, userName, password);
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkConnect() {
        try {
            if (!connection.isValid(1)) {
                close();
                connect();
            }
        } catch (SQLException throwables) {
            close();
            connect();
        }
    }

    public void close() {
        try {
            if (connection != null)
                connection.close();
            if (statement != null)
                statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
