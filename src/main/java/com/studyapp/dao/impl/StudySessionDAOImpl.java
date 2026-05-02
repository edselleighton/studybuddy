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

public class StudySessionDAOImpl implements StudySessionDAO{
    @Override
    public void insert(StudySession studySession) throws SQLException{
        try(Connection conn = DatabaseConnection.getConnection()) {
            insert(conn, studySession);
        }
    }

    @Override
    public void insert(Connection conn, StudySession studySession) throws SQLException{
        String sql = "INSERT INTO study_session (session_id, deck_id, started_at, ended_at) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studySession.getSessionID());
            ps.setInt(2, studySession.getDeckID());
            ps.setObject(3, studySession.getStartedAt());
            ps.setObject(4, studySession.getEndedAt());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateEnd(int sessionID, LocalDateTime endedAt) throws SQLException{
        try(Connection conn = DatabaseConnection.getConnection()) {
            updateEnd(conn, sessionID, endedAt);
        }
    }

    @Override
    public void updateEnd(Connection conn, int sessionID, LocalDateTime endedAt) throws SQLException{
        String sql = "UPDATE study_session SET ended_at = ? WHERE session_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setObject(1, endedAt);
            ps.setObject(2, sessionID);
            ps.executeUpdate();        
        }
    }

    @Override
    public StudySession findByID(int sessionID){
        String sql = "SELECT * FROM study_session WHERE session_id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
                    ps.setInt(1, sessionID);
                    try(ResultSet rs = ps.executeQuery()){
                        if(rs.next()) return new ObjectFactory().createStudySession(rs);
                    }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<StudySession> getAllSessions(){
        List<StudySession> allSessions = new ArrayList<>();
        String sql = "SELECT * FROM study_session";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allSessions.add(new ObjectFactory().createStudySession(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return allSessions;
    }

    @Override
    public int getLastID(){
        String sql = "SELECT MAX(session_id) as max_id FROM study_session";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("max_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 999;
    }

    @Override
    public void delete(int sessionID) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            delete(conn, sessionID);
        }
    }

    @Override
    public void delete(Connection conn, int sessionID) throws SQLException {
        String sql = "DELETE FROM study_session WHERE session_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionID);
            ps.executeUpdate();
        }
    }
}

