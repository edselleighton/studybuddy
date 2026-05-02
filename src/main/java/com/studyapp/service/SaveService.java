package com.studyapp.service;

import java.sql.Connection;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.DeckController;
import com.studyapp.controller.FlashcardController;
import com.studyapp.controller.ReviewController;
import com.studyapp.controller.StudyController;
import com.studyapp.db.DatabaseConnection;

public class SaveService {

    public void saveAll(
            DeckController deckController,
            FlashcardController flashcardController,
            StudyController studyController,
            ReviewController reviewController) throws CustomException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                deckController.persistPendingChanges(conn);
                flashcardController.persistPendingChanges(conn);
                studyController.persistNewStudySessions(conn);
                reviewController.persistPendingChanges(conn);
                studyController.finalizeStudySessions(conn);

                conn.commit();

                deckController.markPendingChangesSaved();
                flashcardController.markPendingChangesSaved();
                studyController.markPendingChangesSaved();
                reviewController.markPendingChangesSaved();
            } catch (Exception e) {
                conn.rollback();
                throw new CustomException("Failed to Save Changes: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Failed to Save Changes: " + e.getMessage());
        }
    }
}
