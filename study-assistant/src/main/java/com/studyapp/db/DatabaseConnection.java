package com.studyapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/study_assistant";
    private static String USER;
    private static String PASS;

    public static void setCredentials(String username, String password) {
        USER = username;
        PASS = password;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
