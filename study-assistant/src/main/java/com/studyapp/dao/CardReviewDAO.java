package com.studyapp.dao;

import java.sql.SQLException;

import com.studyapp.model.CardReview;

public interface CardReviewDAO {
    public void insert(CardReview cardReview) throws SQLException;
    public CardReview findByID(int reviewID);
}
