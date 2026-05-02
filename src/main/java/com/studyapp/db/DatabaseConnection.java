package com.studyapp.db;

import com.studyapp.controller.CustomException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String BASE_URL  = "jdbc:mysql://localhost:3306/";
    private static final String DB_URL    = "jdbc:mysql://localhost:3306/study_assistant";
    private static final String DB_NAME   = "study_assistant";
    private static final String SCHEMA_SCRIPT = "/db/TestDB.sql";
    private static final String SAMPLE_DATA_SCRIPT = "/db/SampleData.sql";

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

        try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASS)) {
            boolean databaseExisted = databaseExists(conn, DB_NAME);

            executeSqlScript(conn, SCHEMA_SCRIPT);
            if (!databaseExisted) {
                executeSqlScript(conn, SAMPLE_DATA_SCRIPT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Error initializing database: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private static boolean databaseExists(Connection conn, String dbName) throws SQLException {
        String sql = "SELECT 1 FROM information_schema.schemata WHERE schema_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dbName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void executeSqlScript(Connection conn, String resourcePath) throws Exception {
        InputStream is = DatabaseConnection.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalStateException("SQL init script not found: " + resourcePath);
        }

        try (Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder statementBuilder = new StringBuilder();
            String currentDelimiter = ";";
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--") || trimmedLine.startsWith("#")) {
                    continue;
                }

                if (trimmedLine.toUpperCase().startsWith("DELIMITER")) {
                    currentDelimiter = trimmedLine.substring(9).trim();
                    continue;
                }

                statementBuilder.append(line).append("\n");

                if (trimmedLine.endsWith(currentDelimiter)) {
                    String sql = statementBuilder.toString()
                            .replace(currentDelimiter, "")
                            .trim();

                    if (!sql.isEmpty()) {
                        stmt.execute(sql);
                    }
                    statementBuilder.setLength(0);
                }
            }
        }
    }
}
