package com.studyapp.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.studyapp.dao.FlashcardDAO;
import com.studyapp.db.DatabaseConnection;
import com.studyapp.model.Flashcard;
import com.studyapp.model.ObjectFactory;

public class FlashcardDAOImpl implements FlashcardDAO {
    @Override
    public void insert(Flashcard flashcard) throws SQLException {
        String sql = "INSERT INTO card (deck_id, question, answer, difficulty, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flashcard.getDeck().getDeckID());
            ps.setString(2, flashcard.getQuestion());
            ps.setObject(3, flashcard.getAnswer());
            ps.setObject(4, flashcard.getDifficulty());
            ps.setObject(5, flashcard.getCreatedAt());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Flashcard flashcard) throws SQLException {
        String sql = "UPDATE card SET question = ?, answer = ?, difficulty = ? WHERE card_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flashcard.getQuestion());
            ps.setString(2, flashcard.getAnswer());
            ps.setString(3, flashcard.getDifficulty());
            ps.setInt(4, flashcard.getCardID());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int cardID) throws SQLException {
        String sql = "DELETE FROM card WHERE card_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cardID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Flashcard findByID(int cardID) {
        String sql = "SELECT * FROM card WHERE card_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cardID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ObjectFactory().createNewCard(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Flashcard> getAllFlashcards() {
        List<Flashcard> allCards = new ArrayList<>();
        String sql = "SELECT * FROM card";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allCards.add(new ObjectFactory().createNewCard(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allCards;
    }
}
