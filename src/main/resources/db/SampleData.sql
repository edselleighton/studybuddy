USE study_assistant;

-- =============================================================
--  SAMPLE DATA
-- =============================================================

-- -------------------------------------------------------------
--  Deck 1: CMSC 127 Midterms
-- -------------------------------------------------------------
INSERT IGNORE INTO Deck (deck_id, name, description) VALUES
(1, 'CMSC 127 Midterms', 'Covers SQL, ER diagrams, and normalization.');

INSERT IGNORE INTO Card (card_id, deck_id, question, answer, difficulty) VALUES
(1,  1, 'A language used to manage and query relational databases.',
        'SQL', 'Easy'),
(2,  1, 'A column (or set of columns) that uniquely identifies each row in a table.',
        'Primary Key', 'Easy'),
(3,  1, 'A column that references the Primary Key of another table, enforcing referential integrity.',
        'Foreign Key', 'Medium'),
(4,  1, 'The process of organizing a database to reduce redundancy and improve data integrity.',
        'Normalization', 'Medium'),
(5,  1, 'A normal form where every column contains atomic values and each row is unique.',
        '1NF', 'Medium'),
(6,  1, 'A normal form where every non-key attribute is fully dependent on the whole primary key.',
        '2NF', 'Hard'),
(7,  1, 'A normal form where no non-key attribute depends on another non-key attribute.',
        '3NF', 'Hard'),
(8,  1, 'A visual representation of entities, their attributes, and relationships in a database.',
        'ER Diagram', 'Easy'),
(9,  1, 'Describes the number of instances of one entity associated with another (e.g., 1:1, 1:N, M:N).',
        'Cardinality', 'Medium'),
(10, 1, 'A primary key made up of two or more columns that together uniquely identify a row.',
        'Composite Key', 'Hard');

-- -------------------------------------------------------------
--  Deck 2: Java & JDBC
-- -------------------------------------------------------------
INSERT IGNORE INTO Deck (deck_id, name, description) VALUES
(2, 'Java & JDBC', 'Core Java and database connectivity concepts for the project.');

INSERT IGNORE INTO Card (card_id, deck_id, question, answer, difficulty) VALUES
(11, 2, 'A Java API that allows Java programs to connect to and interact with databases.',
        'JDBC', 'Easy'),
(12, 2, 'A precompiled SQL statement that uses placeholders (?), preventing SQL injection.',
        'PreparedStatement', 'Medium'),
(13, 2, 'A design pattern that encapsulates all SQL/data access logic, keeping it separate from business logic.',
        'DAO Pattern', 'Medium'),
(14, 2, 'A pattern separating data (Model), UI (View), and application logic (Controller).',
        'MVC', 'Medium'),
(15, 2, 'A JDBC object that holds query results and allows row-by-row iteration using next().',
        'ResultSet', 'Medium'),
(16, 2, 'A referential action that automatically deletes child rows when the parent row is deleted.',
        'ON DELETE CASCADE', 'Hard'),
(17, 2, 'A design pattern that restricts a class to one instance, commonly used for DB connection management.',
        'Singleton Pattern', 'Medium'),
(18, 2, 'A Java library by Google for converting Java objects to and from JSON.',
        'Gson', 'Easy');

-- -------------------------------------------------------------
--  Deck 3: Spanish Vocabulary (variety deck for UI testing)
-- -------------------------------------------------------------
INSERT IGNORE INTO Deck (deck_id, name, description) VALUES
(3, 'Spanish Vocabulary', 'Basic Spanish words and phrases.');

INSERT IGNORE INTO Card (card_id, deck_id, question, answer, difficulty) VALUES
(19, 3, 'The Spanish word for "Hello".',                          'Hola',                       'Easy'),
(20, 3, 'The Spanish word for "Thank you".',                      'Gracias',                    'Easy'),
(21, 3, 'The Spanish phrase for "Where is the library?"',         'Donde esta la biblioteca?', 'Medium');

-- -------------------------------------------------------------
--  Sample Study Sessions
-- -------------------------------------------------------------
INSERT IGNORE INTO Study_Session (session_id, deck_id, started_at) VALUES
(1, 1, '2025-04-01 18:00:00'),
(2, 2, '2025-04-02 20:00:00'),
(3, 1, '2025-04-05 09:00:00');

-- -------------------------------------------------------------
--  Sample Card Reviews
-- -------------------------------------------------------------
INSERT IGNORE INTO Card_Review (review_id, session_id, card_id, reviewed_at, is_correct) VALUES
(1,  1, 1,  '2025-04-01 18:02:00', TRUE),
(2,  1, 2,  '2025-04-01 18:04:00', TRUE),
(3,  1, 3,  '2025-04-01 18:06:00', FALSE),
(4,  1, 4,  '2025-04-01 18:08:00', TRUE),
(5,  1, 5,  '2025-04-01 18:10:00', FALSE),
(6,  1, 6,  '2025-04-01 18:13:00', FALSE),
(7,  1, 7,  '2025-04-01 18:16:00', TRUE),
(8,  1, 8,  '2025-04-01 18:18:00', TRUE),
(9,  1, 9,  '2025-04-01 18:21:00', TRUE),
(10, 1, 10, '2025-04-01 18:24:00', FALSE),
(11, 2, 11, '2025-04-02 20:02:00', TRUE),
(12, 2, 12, '2025-04-02 20:05:00', TRUE),
(13, 2, 13, '2025-04-02 20:08:00', FALSE),
(14, 2, 14, '2025-04-02 20:11:00', TRUE),
(15, 2, 15, '2025-04-02 20:14:00', TRUE);

UPDATE Study_Session SET ended_at = '2025-04-01 18:25:00' WHERE session_id = 1;
UPDATE Study_Session SET ended_at = '2025-04-02 20:15:00' WHERE session_id = 2;
UPDATE Study_Session SET ended_at = '2026-01-04 06:27:00' WHERE session_id = 3;
