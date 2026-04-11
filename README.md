# Study Assistant

Study Assistant is a Java Maven application for managing study decks, flashcards, and study-related records backed by MySQL. The project currently includes a command-line interface for logging in, viewing decks, and browsing cards, with placeholder GUI classes prepared for future work.

## Features

- CLI-based login using MySQL credentials
- Deck listing and deck detail viewing
- Flashcard listing and card detail viewing
- DAO-based data access layer for decks, flashcards, study sessions, and card reviews
- Sample JSON data for import or testing reference

## Tech Stack

- Java 21
- Maven
- MySQL
- Gson

## Project Structure

```text
study-assistant/
|-- pom.xml
|-- README.md
|-- TestDB.sql
|-- sample-data/
|   `-- sample-deck.json
`-- src/
    |-- main/
    |   |-- java/
    |   `-- resources/
    `-- test/
```

## Requirements

- JDK 21
- Maven 3.9+
- MySQL Server

## Database Setup

1. Create the `study_assistant` database in MySQL.
2. Import [`TestDB.sql`](src/main/resources/db/TestDB.sql) to create the tables and seed data.
3. Confirm the database is running before starting the application.


## Run

initialize the database first, then you can run the application using Maven:

```powershell
cd C:\...\study-assistant
mvn javafx:run
```

If you want to compile and run without Maven:

```powershell
cd C:\...\study-assistant
javac -d out (Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object FullName)
java -cp out com.studyapp.Main
```

## Current Notes

- The CLI menu options for viewing decks and cards are working.
- Some menu items and GUI classes are still placeholders for future implementation.
- Database credentials may be saved locally in a `.env` file during login.
