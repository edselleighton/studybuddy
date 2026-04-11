package com.studyapp.db;

import com.studyapp.controller.CustomException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseConnection {

    private static final String BASE_URL  = "jdbc:mysql://localhost:3306/";
    private static final String DB_URL    = "jdbc:mysql://localhost:3306/study_assistant";
    private static final String DB_NAME   = "study_assistant";

    private static String USER;
    private static String PASS;

    public static void setCredentials(String username, String password) {
        USER = username;
        PASS = password;
    }

    public static boolean authenticate(String username, String password) {
        try (Connection conn = DriverManager.getConnection(BASE_URL, username, password)) {
            return conn != null;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void initializeDatabase() throws CustomException {
        System.out.println("\n------ INITIALIZING DATABASE -----------\n");
        String sql = loadSqlScript("/db/TestDB.sql");

        try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASS);
             Statement  stmt = conn.createStatement()) {

            for (String statement : sql.split(";")) {
                String trimmed = statement.strip();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }catch (SQLException e){
            throw new CustomException("Error initializing database.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private static String loadSqlScript(String resourcePath) {
        InputStream is = DatabaseConnection.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalStateException("SQL init script not found: " + resourcePath);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read SQL init script", e);
        }
    }
}