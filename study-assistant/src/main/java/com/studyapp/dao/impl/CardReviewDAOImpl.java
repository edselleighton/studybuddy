package com.studyapp.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.studyapp.dao.CardReviewDAO;
import com.studyapp.db.DatabaseConnection;
import com.studyapp.model.CardReview;
import com.studyapp.model.ObjectFactory;

public class CardReviewDAOImpl implements CardReviewDAO{
    @Override
    public void insert(CardReview cardReview) throws SQLException{
        String sql = "INSERT INTO card_review (session_id, card_id, reviewed_at, is_correct) VALUES (?, ?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, cardReview.getStudySession().getSessionID());
            ps.setInt(2, cardReview.getFlashcard().getCardID());
            ps.setObject(3, cardReview.getReviewedAt());
            ps.setBoolean(4, cardReview.isCorrect());
            ps.executeUpdate();
        }
    }

    @Override
    public CardReview findByID(int reviewedID){
        String sql = "SELECT * FROM card_review WHERE review_id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) return new ObjectFactory().createNewReview(rs);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
