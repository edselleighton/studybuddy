package com.studyapp.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.studyapp.dao.StudySessionDAO;
import com.studyapp.db.DatabaseConnection;
import com.studyapp.model.ObjectFactory;
import com.studyapp.model.StudySession;

public class StudySessionDAOImpl implements StudySessionDAO {
    @Override
    public void insert(StudySession studySession) throws SQLException {
        String sql = "INSERT INTO study_session (deck_id, started_at, ended_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studySession.getDeck().getDeckID());
            ps.setObject(2, studySession.getStartedAt());
            ps.setObject(3, studySession.getEndedAt());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateEnd(LocalDateTime endedAt) throws SQLException {
        String sql = "UPDATE study_session SET ended_at = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, endedAt);
            ps.executeUpdate();
        }
    }

    @Override
    public StudySession findByID(int sessionID) {
        String sql = "SELECT * FROM study_session WHERE session_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ObjectFactory().createStudySession(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<StudySession> getAllSessions() {
        List<StudySession> allSessions = new ArrayList<>();
        String sql = "SELECT * FROM study_session";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allSessions.add(new ObjectFactory().createStudySession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allSessions;
    }
}
