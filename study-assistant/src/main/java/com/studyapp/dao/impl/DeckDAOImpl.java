package com.studyapp.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.studyapp.dao.DeckDAO;
import com.studyapp.db.DatabaseConnection;
import com.studyapp.model.Deck;
import com.studyapp.model.ObjectFactory;

public class DeckDAOImpl implements DeckDAO{
    @Override
    public void insert(Deck deck) throws SQLException {
        String sql = "INSERT INTO deck (name, description, created_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deck.getName());
            ps.setString(2, deck.getDescription());
            ps.setObject(3, deck.getCreatedAt());
            ps.executeUpdate();
        }
    }

    /// ONLY NAME AND DESCRIPTION CAN BE UPDATED
    @Override
    public void update(Deck deck) throws SQLException {
        String sql = "UPDATE deck SET name = ?, description = ? WHERE deck_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deck.getName());
            ps.setString(2, deck.getDescription());
            ps.setInt(3, deck.getDeckID());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int deckID) throws SQLException{
        String sql = "DELETE FROM deck WHERE deck_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deckID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Deck findByID(int deckID) {
        String sql = "SELECT * FROM deck WHERE deck_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deckID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new ObjectFactory().createNewDeck(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
