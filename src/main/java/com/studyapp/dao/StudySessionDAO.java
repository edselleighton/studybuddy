package com.studyapp.dao;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import com.studyapp.model.StudySession;

public interface StudySessionDAO {
    public void insert(StudySession studySession) throws SQLException;
    public void updateEnd(LocalDateTime endedAt) throws SQLException;
    public StudySession findByID(int sessionID);
    public List<StudySession> getAllSessions();
}
