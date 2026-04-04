package com.studyapp.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.studyapp.db.DatabaseConnection;

public class CredentialHandler {
    private static final String FILE_PATH = ".env";
    private String username;
    private String password;

    public boolean checkForCred() {
        File file = new File(FILE_PATH);
        return file.exists();
    }

    public boolean readAndValidate() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("USERNAME=")) {
                    username = line.substring("USERNAME=".length());
                } else if (line.startsWith("PASSWORD=")) {
                    password = line.substring("PASSWORD=".length());
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (username == null || password == null) {
            return false;
        }

        if (!DatabaseConnection.authenticate(username, password)) {
            deleteEnvFile();
            return false;
        }

        DatabaseConnection.setCredentials(username, password);
        return true;
    }

    public void write(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("USERNAME=" + username);
            writer.newLine();
            writer.write("PASSWORD=" + password);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteEnvFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
