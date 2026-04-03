package com.studyapp.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

import com.studyapp.dao.DeckDAO;
import com.studyapp.db.DatabaseConnection;
import com.studyapp.model.Deck;

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

    // Update name and description only.
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
                if (rs.next()) return createNewDeck(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Build a Deck object from a result row.
    Deck createNewDeck(ResultSet rs){
    try{
        Deck deck = new Deck();
        deck.setDeckID(rs.getInt("deck_id"));
        deck.setName(rs.getString("name"));
        deck.setDescription(rs.getString("description"));
        
        Object createdAtObj = rs.getObject("created_at");
        if (createdAtObj instanceof LocalDateTime) {
            deck.setCreatedAt((LocalDateTime) createdAtObj);
        } else if (createdAtObj instanceof java.sql.Timestamp) {
            deck.setCreatedAt(((java.sql.Timestamp) createdAtObj).toLocalDateTime());
        }
        
        return deck;
    }catch(SQLException e) {
        e.printStackTrace();
    }
    return null;
}
}
