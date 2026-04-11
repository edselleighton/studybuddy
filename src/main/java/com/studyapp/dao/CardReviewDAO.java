package com.studyapp.dao;

import java.sql.SQLException;
import java.util.List;

import com.studyapp.model.CardReview;

public interface CardReviewDAO {
    public void insert(CardReview cardReview) throws SQLException;
    public CardReview findByID(int reviewID);
    public List<CardReview> getAllReviews();
    public int getLastID();
    public void delete(int reviewID) throws SQLException;
}
