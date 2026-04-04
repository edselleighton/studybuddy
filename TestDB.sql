drop database if exists study_assistant;


CREATE DATABASE IF NOT EXISTS study_assistant
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE study_assistant;

CREATE TABLE IF NOT EXISTS deck (
    deck_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_deck PRIMARY KEY (deck_id),
    CONSTRAINT uq_deck_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS card (
    card_id INT NOT NULL AUTO_INCREMENT,
    deck_id INT NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    difficulty ENUM('Easy', 'Medium', 'Hard') DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_card PRIMARY KEY (card_id),
    CONSTRAINT fk_card_deck FOREIGN KEY (deck_id)
        REFERENCES deck (deck_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS study_session (
    session_id INT NOT NULL AUTO_INCREMENT,
    deck_id INT NOT NULL,
    started_at DATETIME NOT NULL,
    ended_at DATETIME DEFAULT NULL,
    CONSTRAINT pk_session PRIMARY KEY (session_id),
    CONSTRAINT fk_session_deck FOREIGN KEY (deck_id)
        REFERENCES deck (deck_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS card_review (
    review_id INT NOT NULL AUTO_INCREMENT,
    session_id INT NOT NULL,
    card_id INT NOT NULL,
    reviewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_correct BOOLEAN NOT NULL,
    CONSTRAINT pk_review PRIMARY KEY (review_id),
    CONSTRAINT fk_review_session FOREIGN KEY (session_id)
        REFERENCES study_session (session_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_review_card FOREIGN KEY (card_id)
        REFERENCES card (card_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

INSERT IGNORE INTO deck (deck_id, name, description) VALUES
(1, 'CMSC 127 Midterms', 'Covers SQL, ER diagrams, and normalization.'),
(2, 'Java & JDBC', 'Core Java and database connectivity concepts for the project.'),
(3, 'Spanish Vocabulary', 'Basic Spanish words and phrases.');

INSERT IGNORE INTO card (card_id, deck_id, question, answer, difficulty) VALUES
(1, 1, 'What does SQL stand for?',
    'Structured Query Language', 'Easy'),
(2, 1, 'What is a Primary Key?',
    'A column (or set of columns) that uniquely identifies each row in a table.', 'Easy'),
(3, 1, 'What is a Foreign Key?',
    'A column that references the Primary Key of another table, enforcing referential integrity.', 'Medium'),
(4, 1, 'What is normalization?',
    'The process of organizing a database to reduce redundancy and improve data integrity.', 'Medium'),
(5, 1, 'What is 1NF (First Normal Form)?',
    'Every column contains atomic (indivisible) values and each row is unique.', 'Medium'),
(6, 1, 'What is 2NF (Second Normal Form)?',
    'The table is in 1NF and every non-key attribute is fully dependent on the whole primary key.', 'Hard'),
(7, 1, 'What is 3NF (Third Normal Form)?',
    'The table is in 2NF and no non-key attribute depends on another non-key attribute (no transitive dependency).', 'Hard'),
(8, 1, 'What is an ER Diagram?',
    'Entity-Relationship Diagram, a visual representation of entities, their attributes, and relationships in a database.', 'Easy'),
(9, 1, 'What does cardinality mean in an ER diagram?',
    'The number of instances of one entity that can be associated with instances of another, such as 1:1, 1:N, or M:N.', 'Medium'),
(10, 1, 'What is a composite key?',
    'A primary key made up of two or more columns that together uniquely identify a row.', 'Hard'),
(11, 2, 'What does JDBC stand for?',
    'Java Database Connectivity', 'Easy'),
(12, 2, 'What is a PreparedStatement?',
    'A precompiled SQL statement that uses placeholders (?), preventing SQL injection and improving performance.', 'Medium'),
(13, 2, 'What is the DAO pattern?',
    'Data Access Object, an abstraction layer that encapsulates SQL logic and keeps it separate from business logic.', 'Medium'),
(14, 2, 'What is MVC?',
    'Model-View-Controller, a pattern separating data, UI, and application logic.', 'Medium'),
(15, 2, 'What is a ResultSet in JDBC?',
    'An object holding the result of a SQL query, allowing row-by-row iteration using next().', 'Medium'),
(16, 2, 'What does ON DELETE CASCADE do?',
    'Automatically deletes child rows when the referenced parent row is deleted.', 'Hard'),
(17, 2, 'What is the Singleton pattern?',
    'A design pattern that restricts a class to one instance, commonly used for database connection management.', 'Medium'),
(18, 2, 'What is Gson?',
    'A Java library by Google for serializing and deserializing Java objects to and from JSON.', 'Easy'),
(19, 3, 'How do you say "Hello" in Spanish?',
    'Hola', 'Easy'),
(20, 3, 'How do you say "Thank you" in Spanish?',
    'Gracias', 'Easy'),
(21, 3, 'How do you say "Where is the library?"',
    'Donde esta la biblioteca?', 'Medium');

INSERT IGNORE INTO study_session (session_id, deck_id, started_at, ended_at) VALUES
(1, 1, '2025-04-01 18:00:00', '2025-04-01 18:25:00'),
(2, 2, '2025-04-02 20:00:00', '2025-04-02 20:15:00'),
(3, 1, '2025-04-05 09:00:00', NULL);

INSERT IGNORE INTO card_review (review_id, session_id, card_id, reviewed_at, is_correct) VALUES
(1, 1, 1, '2025-04-01 18:02:00', TRUE),
(2, 1, 2, '2025-04-01 18:04:00', TRUE),
(3, 1, 3, '2025-04-01 18:06:00', FALSE),
(4, 1, 4, '2025-04-01 18:08:00', TRUE),
(5, 1, 5, '2025-04-01 18:10:00', FALSE),
(6, 1, 6, '2025-04-01 18:13:00', FALSE),
(7, 1, 7, '2025-04-01 18:16:00', TRUE),
(8, 1, 8, '2025-04-01 18:18:00', TRUE),
(9, 1, 9, '2025-04-01 18:21:00', TRUE),
(10, 1, 10, '2025-04-01 18:24:00', FALSE),
(11, 2, 11, '2025-04-02 20:02:00', TRUE),
(12, 2, 12, '2025-04-02 20:05:00', TRUE),
(13, 2, 13, '2025-04-02 20:08:00', FALSE),
(14, 2, 14, '2025-04-02 20:11:00', TRUE),
(15, 2, 15, '2025-04-02 20:14:00', TRUE);

SELECT 'deck' AS table_name, COUNT(*) AS row_count FROM deck
UNION ALL
SELECT 'card', COUNT(*) FROM card
UNION ALL
SELECT 'study_session', COUNT(*) FROM study_session
UNION ALL
SELECT 'card_review', COUNT(*) FROM card_review;
