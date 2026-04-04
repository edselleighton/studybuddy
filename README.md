# Study Assistant Prototype

This branch contains the JavaFX prototype for the Study Assistant application. It focuses on the graphical user interface flow for database setup, dashboard navigation, deck views, and study-related screens while sharing core backend structure with the main branch.

## Features

- JavaFX-based login and setup screen
- Dashboard, deck detail, all cards, and study session prototype screens
- Sidebar-based application navigation
- MySQL-backed data access layer for decks, flashcards, study sessions, and card reviews
- Shared backend support for credential handling and database authentication

## Tech Stack

- Java 21
- Maven
- JavaFX 21
- MySQL
- Gson

## Project Structure

```text
study-assistant/
|-- pom.xml
|-- README.md
|-- TestDB.sql
|-- src/
|   `-- main/
|       |-- java/
|       `-- resources/
`-- .gitignore
```

## Requirements

- JDK 21
- Maven 3.9+
- MySQL Server

## Database Setup

1. Create the `study_assistant` database in MySQL.
2. Import `TestDB.sql` to create the schema and seed data.
3. Start MySQL before launching the prototype.

The setup screen asks for your MySQL credentials at runtime. If the user selects the remember option, credentials may be stored locally in `.env`.

## Run

From the project root:

```powershell
cd C:\Users\edsel\study-assistant
mvn javafx:run
```

If needed, you can also build the project with:

```powershell
cd C:\Users\edsel\study-assistant
mvn compile
```

## Prototype Notes

- This branch is UI-first and keeps the JavaFX application flow as the main entry point.
- The CLI flow from `main` is not the primary runtime path on this branch.
- Backend structure has been partially aligned with `main` to make future merges cleaner.
- Several UI screens are still prototype-level and may use placeholder content.
