package com.studyapp.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.studyapp.dao.CardReviewDAO;
import com.studyapp.db.DatabaseConnection;
import com.studyapp.model.CardReview;
import com.studyapp.model.ObjectFactory;
import com.studyapp.model.StudySession;

public class CardReviewDAOImpl implements CardReviewDAO{
    @Override
    public void insert(CardReview cardReview) throws SQLException{
        String sql = "INSERT INTO card_review (review_id, session_id, card_id, reviewed_at, is_correct) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, cardReview.getReviewID());
            ps.setInt(2, cardReview.getStudySession().getSessionID());
            ps.setInt(3, cardReview.getFlashcard().getCardID());
            ps.setObject(4, cardReview.getReviewedAt());
            ps.setBoolean(5, cardReview.isCorrect());
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

    @Override
    public List<CardReview> getAllReviews(){
        List<CardReview> allReviews = new ArrayList<>();
        String sql = "SELECT * FROM card_review";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allReviews.add(new ObjectFactory().createNewReview(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return allReviews;
    }

    @Override
    public void delete(int reviewID) {
        String sql = "DELETE FROM card_review WHERE review_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getLastID(){
        String sql = "SELECT MAX(review_id) as max_id FROM card_review";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("max_id");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 999;
    }
}
