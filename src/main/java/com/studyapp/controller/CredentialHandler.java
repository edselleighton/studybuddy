package com.studyapp.controller;

import com.studyapp.db.DatabaseConnection;
import java.util.prefs.Preferences;

public class CredentialHandler {
    private static final Preferences prefs = Preferences.userRoot().node("com/studyapp");

    public static void save(String username, String password) {
        prefs.put("username", username);
        prefs.put("password", password);
    }

    public static String getUsername() {
        return prefs.get("username", "");
    }

    public static String getPassword() {
        return prefs.get("password", "");
    }

    public static void clear() {
        prefs.remove("username");
        prefs.remove("password");
    }

    public static boolean validateCredentials(){
        if (getUsername().isEmpty() || getPassword().isEmpty()) {
            return false;
        }
        if (DatabaseConnection.authenticate(getUsername(), getPassword())) {
            DatabaseConnection.setCredentials(getUsername(), getPassword());
            return true;
        } else {
            clear();
            return false;
        }
    }
}