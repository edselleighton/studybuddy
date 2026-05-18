-- =============================================================
--  Study Assistant App - Schema
--  Safe to run multiple times
-- =============================================================

CREATE DATABASE IF NOT EXISTS study_assistant
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE study_assistant;

-- -------------------------------------------------------------
--  TABLE: Deck
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Deck (
    deck_id     INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_deck      PRIMARY KEY (deck_id),
    CONSTRAINT uq_deck_name UNIQUE (name)
);

-- -------------------------------------------------------------
--  TABLE: Card
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Card (
    card_id     INT          NOT NULL AUTO_INCREMENT,
    deck_id     INT          NOT NULL,
    question    TEXT         NOT NULL,
    answer      TEXT         NOT NULL,
    difficulty  ENUM('Easy','Medium','Hard') DEFAULT 'Medium',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_card      PRIMARY KEY (card_id),
    CONSTRAINT fk_card_deck FOREIGN KEY (deck_id)
        REFERENCES Deck (deck_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- -------------------------------------------------------------
--  TABLE: Study_Session
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Study_Session (
    session_id  INT      NOT NULL AUTO_INCREMENT,
    deck_id     INT      NOT NULL,
    started_at  DATETIME NOT NULL,
    ended_at    DATETIME DEFAULT NULL,

    CONSTRAINT pk_session      PRIMARY KEY (session_id),
    CONSTRAINT fk_session_deck FOREIGN KEY (deck_id)
        REFERENCES Deck (deck_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- -------------------------------------------------------------
--  TABLE: Card_Review
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Card_Review (
    review_id   INT      NOT NULL AUTO_INCREMENT,
    session_id  INT      NOT NULL,
    card_id     INT      NOT NULL,
    reviewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_correct  BOOLEAN  NOT NULL,

    CONSTRAINT pk_review         PRIMARY KEY (review_id),
    CONSTRAINT fk_review_session FOREIGN KEY (session_id)
        REFERENCES Study_Session (session_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_review_card    FOREIGN KEY (card_id)
        REFERENCES Card (card_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- =============================================================
--  VIEWS
-- =============================================================
CREATE OR REPLACE VIEW deck_progress AS
    SELECT d.deck_id,
           COUNT(cr.review_id) AS total_reviews,
           SUM(CASE WHEN cr.is_correct = 1 THEN 1 ELSE 0 END) AS correct_reviews,
           IFNULL(ROUND((SUM(CASE WHEN cr.is_correct = 1 THEN 1 ELSE 0 END) / COUNT(cr.review_id)) * 100, 2), 0) AS progress
    FROM deck d
    LEFT JOIN study_session ss ON d.deck_id = ss.deck_id
    LEFT JOIN card_review cr ON ss.session_id = cr.session_id
    GROUP BY d.deck_id;

-- =============================================================
--  INDEXES
-- =============================================================
SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = 'study_assistant'
              AND table_name = 'study_session'
              AND index_name = 'idx_study_started'
        ),
        'SELECT 1',
        'CREATE INDEX idx_study_started ON study_session(started_at)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = 'study_assistant'
              AND table_name = 'study_session'
              AND index_name = 'idx_study_ended'
        ),
        'SELECT 1',
        'CREATE INDEX idx_study_ended ON study_session(ended_at)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = 'study_assistant'
              AND table_name = 'card'
              AND index_name = 'idx_card_question'
        ),
        'SELECT 1',
        'CREATE INDEX idx_card_question ON card(question(100))'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =============================================================
--  TRIGGERS
-- =============================================================
DROP TRIGGER IF EXISTS trg_chk_session_active;

DELIMITER //
CREATE TRIGGER trg_chk_session_active
BEFORE INSERT ON card_review
FOR EACH ROW
BEGIN
    DECLARE session_status DATETIME;
    SELECT ended_at INTO session_status
    FROM study_session
    WHERE session_id = NEW.session_id;

    IF session_status IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot add card review to a finished session.';
    END IF;
END //
DELIMITER ;
