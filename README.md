# Study Assistant

Study Assistant is a JavaFX desktop application for creating and managing flashcard decks, studying with type-in quizzes, and tracking session progress - all backed by a MySQL database.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Setup & Installation](#setup--installation)
- [User Manual](#user-manual)
    - [Login / Database Connection](#1-login--database-connection)
    - [Dashboard](#2-dashboard)
    - [My Decks](#3-my-decks)
    - [All Cards](#4-all-cards)
    - [Deck Detail](#5-deck-detail)
    - [Card Detail](#6-card-detail)
    - [Study Mode](#7-study-mode)
    - [Import & Export](#8-import--export)
- [Import File Templates](#import-file-templates)
    - [JSON Template](#json-template)
    - [CSV Template](#csv-template)
- [Project Structure](#project-structure)

---

## Features

- MySQL login with auto-login on subsequent launches
- Dashboard showing accuracy, cards reviewed, study time, and a difficulty breakdown pie chart
- Deck management: create, edit, delete, and paginate through decks
- Flashcard management: create, edit, and delete cards with Easy / Medium / Hard difficulty levels
- Type-in study mode with intelligent answer checking
- Smart answer checker using Levenshtein distance, Jaro-Winkler similarity, cosine n-gram similarity, and WordNet synonym/antonym detection
- Per-card result feedback: **CORRECT**, **CLOSE**, or **INCORRECT**, with the correct answer shown when a typo is accepted as correct
- Import cards from a **JSON** or **CSV** file into any existing or new deck, with a live preview and per-card selection
- Export decks as **JSON** or **CSV**
- Search and sort for both deck and card lists
- Paginated deck and card lists (5 items per page)

---

## Tech Stack

| Component        | Technology                         |
|------------------|------------------------------------|
| Language         | Java 21                            |
| UI Framework     | JavaFX 21.0.2                      |
| Build Tool       | Maven                              |
| Database         | MySQL                              |
| JSON             | Gson 2.10.1                        |
| String Matching  | Apache Commons Text 1.12.0         |
| String Matching  | java-string-similarity 2.0.0       |
| Semantic Matching| extjwnl 2.0.5 + extjwnl-data-wn31  |

---

## Setup & Installation

### Prerequisites

- JDK 21 or later
- Apache Maven 3.6+
- MySQL Server 8.x

### Steps

1. **Clone or download** the project folder.

2. **Ensure your MySQL server** is running and accepting connections on `localhost`.

3. **(Optional) Build the project:**

   ```powershell
   mvn clean install
   ```

4. **Run the application:**

   ```powershell
   mvn javafx:run
   ```

   On the first launch, a login screen will appear. Enter your MySQL username and password. The app will automatically create the database, tables, and sample data. Credentials are stored via the Java Preferences API and used for auto-login on future launches.

---

## User Manual

### 1. Login / Database Connection

**First launch:**
- A login card will appear asking for your MySQL **Username** and **Password**.
- These are your MySQL server credentials (not a separate app account).
- Click **Sign In** to authenticate and load the application.
- If authentication fails, an error message is shown and you can retry.

**Subsequent launches:**
- If valid credentials are already saved, the app skips the login screen and opens directly via the splash screen.
- To remove saved credentials:
    ```powershell
    reg delete HKCU\Software\JavaSoft\Prefs\com\studyapp /f
    ```

---

### 2. Dashboard

The **Dashboard** is the home screen shown after login.

| Section          | Description                                                                 |
|------------------|-----------------------------------------------------------------------------|
| Accuracy         | Percentage of correct answers across all reviewed cards                     |
| Cards Reviewed   | Total number of card review attempts recorded                               |
| Study Time       | Cumulative time spent in study sessions                                     |
| Difficulty Chart | Pie chart showing the distribution of Easy / Medium / Hard cards            |
| Recent Decks     | Quick-access list of recently added decks; click any deck to open it        |

Navigate using the **sidebar** on the left: **Dashboard**, **My Decks**, **All Cards**.

---

### 3. My Decks

Access via the **My Decks** sidebar button.

Displays all your decks in a paginated list (5 per page). Each deck row shows its name, card count, and progress percentage.

**Toolbar:**

| Control         | Action                                                                                       |
|-----------------|----------------------------------------------------------------------------------------------|
| New             | Opens a dialog to create a new deck (name required, description optional)                   |
| Import          | Opens the Import Cards dialog (see [Import & Export](#8-import--export))                   |
| Export          | Opens a dialog to select a deck and format, then saves to a file                            |
| Search          | Filters the deck list by name or description as you type                                    |
| Sort by         | Sorts the list by **Newest** (default), **Oldest**, or **Name**                             |

Each deck row has an **OPEN** button that opens the [Deck Detail](#5-deck-detail) view. Use **Previous / Next** to navigate between pages.

**Creating a deck:**
1. Click **New**.
2. Enter a deck name (must be unique).
3. Optionally enter a description.
4. Click **Create**.

**Deleting a deck:**
- Open the deck via the **Open** button, then use the **Delete** button in the Deck Detail view.
- Deleting a deck permanently removes all its cards and associated study sessions.

---

### 4. All Cards

Access via the **All Cards** sidebar button.

Displays all flashcards across every deck in a paginated list. Each card row shows the question, answer, deck name, and difficulty.

**Toolbar:**

| Control  | Action                                                              |
|----------|---------------------------------------------------------------------|
| New      | Opens a dialog to create a new flashcard (must select a deck)       |
| Search   | Filters the card list by question or answer as you type             |
| Sort by  | Sorts the list by **Newest** (default), **Oldest**, or **Question** |

Each card row has an **OPEN** button that opens the [Card Detail](#6-card-detail) view. Use **Previous / Next** to navigate between pages.

**Creating a flashcard:**
1. Click **New**.
2. Select a target deck from the dropdown.
3. Enter the question and answer.
4. Select a difficulty: **Easy**, **Medium**, or **Hard**.
5. Click **Create**.

---

### 5. Deck Detail

Accessed by clicking **Open** on any deck in the My Decks list.

Displays the deck's name, description, creation date, and all cards belonging to it.

**Sidebar buttons:**

| Button  | Action                                                    |
|---------|-----------------------------------------------------------|
| Edit    | Enables inline editing of the deck name and description   |
| Save    | Saves edits and persists changes to the database          |
| Cancel  | Discards unsaved edits                                    |
| Study   | Starts [Study Mode](#7-study-mode) for this deck          |
| Delete  | Deletes the deck and all its cards                        |
| Back    | Returns to the My Decks list                              |

The card list in the detail view also supports opening individual cards via **Open**.

---

### 6. Card Detail

Accessed by clicking **Open** on any card in All Cards or Deck Detail.

Displays the full question, answer, and difficulty of a flashcard with inline editing.

**Sidebar buttons:**

| Button  | Action                                                  |
|---------|---------------------------------------------------------|
| Edit    | Makes the question, answer, and difficulty editable     |
| Save    | Saves changes and persists them to the database         |
| Cancel  | Discards unsaved changes                                |
| Delete  | Permanently deletes the flashcard                       |
| Back    | Returns to the previous screen                          |

**Difficulty values:** `Easy`, `Medium`, `Hard` (case-insensitive on input; stored as title-case).

---

### 7. Study Mode

Started from the **Deck Detail** view using the **Study** button. The deck must have at least one card.

**How it works:**
1. Cards are presented one by one in sequence.
2. The question is displayed; type your answer in the text area.
3. Press **Submit** or use **Ctrl + Enter** to submit.
4. The result screen shows your answer and one of three outcomes:

| Result        | Meaning                                                                              |
|---------------|--------------------------------------------------------------------------------------|
| **CORRECT**   | Answer matches the expected answer (exact, synonym, or within acceptable similarity) |
| **CLOSE**     | Answer was similar but did not meet the accepted-correct threshold                   |
| **INCORRECT** | Answer did not meet the similarity threshold                                         |

5. Click **Next** to proceed to the next card, or **Retry** to attempt the same card again.
6. When all cards are done, a **Session Complete** dialog shows the final score.
7. The live sidebar during study tracks **Correct**, **Attempts**, and a **progress arc**.

**Answer Checker Logic:**
Answers are evaluated using a combination of:
- Levenshtein edit distance
- Jaro-Winkler similarity
- Bigram and trigram cosine similarity
- WordNet synonym and antonym detection
- Stop-word filtering and Unicode normalization

This allows minor typos, synonym answers, and case/punctuation differences to be accepted as correct.

---

### 8. Import & Export

Accessed via the **Import** and **Export** buttons in My Decks.

**Import:**
1. Click **Import** to open the full-screen **Import Cards** dialog.
2. In the **left panel**, choose where the imported cards will go:
    - **Add to Existing Deck** — select a deck from the dropdown.
    - **Add to New Deck** — enter a deck name (required) and an optional description.
3. Click **IMPORT FROM CSV** or **IMPORT FROM JSON** to open a file chooser and load a file.
4. The **right panel** shows a preview table with columns: checkbox, **QUESTION**, **ANSWER**, and **DIFFICULTY**.
    - Cards whose difficulty was not recognised in the file show a `--Select Difficulty--` dropdown that must be filled before importing.
    - Toggle **EDIT PREVIEW** to edit any card's question, answer, or difficulty inline before saving.
5. Select the cards to import using the per-row checkboxes or the **SELECT ALL** checkbox.
6. Click **IMPORT TO SYSTEM**. A success message reports how many cards were imported, and the view returns to My Decks.

**Export:**
1. Click **Export**.
2. Select the deck to export from the dropdown and choose a file type: **JSON** or **CSV**.
3. A save dialog opens — choose a destination and file name.
4. The file is written with all cards belonging to that deck.

---

## Import File Templates

### JSON Template

Two formats are accepted:

**Flat card array** (default export format):
```json
[
  {
    "question": "What is the question?",
    "answer": "The answer goes here.",
    "difficulty": "Easy"
  },
  {
    "question": "Another question?",
    "answer": "Another answer.",
    "difficulty": "Medium"
  },
  {
    "question": "A harder question?",
    "answer": "A harder answer.",
    "difficulty": "Hard"
  }
]
```

**Deck wrapper object** (also accepted on import):
```json
{
  "deck_name": "Your Deck Name",
  "description": "Optional description of the deck",
  "exported_at": "2025-01-01T00:00:00",
  "cards": [
    {
      "question": "What is the question?",
      "answer": "The answer goes here.",
      "difficulty": "Easy"
    },
    {
      "question": "Another question?",
      "answer": "Another answer.",
      "difficulty": "Medium"
    }
  ]
}
```

> **Notes:**
> - `difficulty` must be `"Easy"`, `"Medium"`, or `"Hard"` (case-insensitive). Unrecognised or missing values show as `--Select Difficulty--` in the preview and must be assigned before importing.
> - `description` is optional; when present in a deck wrapper, the import dialog pre-fills the deck name and description fields.
> - Cards with a blank `question` or `answer` are skipped.
> - The destination deck is always chosen in the Import Cards dialog, regardless of the `deck_name` field in the file.

---

### CSV Template

**With difficulty:**
```csv
question,answer,difficulty
What is the question?,The answer goes here.,Easy
Another question?,Another answer.,Medium
A harder question?,A harder answer.,Hard
```

**Without difficulty:**
```csv
question,answer
What is the question?,The answer goes here.
Another question?,Another answer.
```

> **Notes:**
> - The header row must be exactly `question,answer,difficulty` or `question,answer`.
> - `difficulty` must be `Easy`, `Medium`, or `Hard` (case-insensitive). Unrecognised or missing values show as `--Select Difficulty--` in the preview and must be assigned before importing.
> - Rows with a blank `question` or `answer` are skipped.
> - The destination deck is always chosen in the Import Cards dialog; there are no `deck_name` or `description` columns in the CSV.

---

## Project Structure

```text
studybuddy/
|-- pom.xml
|-- README.md
|-- sample-data/
|   |-- sample-deck.json
|   |-- sample-data2.json
|   |-- sample-with-difficulty.json
|   |-- sample-no-difficulty.json
|   |-- sample-with-difficulty.csv
|   `-- sample-no-difficulty.csv
`-- src/main/
    |-- java/com/studyapp/
    |   |-- Launcher.java              # Executable JAR entry point; delegates to Main
    |   |-- Main.java                  # JavaFX application bootstrap
    |   |-- controller/
    |   |   |-- AnswerChecker.java
    |   |   |-- CredentialHandler.java
    |   |   |-- CustomException.java
    |   |   |-- DeckController.java
    |   |   |-- FlashcardController.java
    |   |   |-- MainController.java    # Central coordinator for UI, controllers, and services
    |   |   |-- ReviewController.java
    |   |   `-- StudyController.java
    |   |-- dao/
    |   |   |-- CardReviewDAO.java
    |   |   |-- DeckDAO.java
    |   |   |-- FlashcardDAO.java
    |   |   |-- StudySessionDAO.java
    |   |   `-- impl/
    |   |       |-- CardReviewDAOImpl.java
    |   |       |-- DeckDAOImpl.java
    |   |       |-- FlashcardDAOImpl.java
    |   |       `-- StudySessionDAOImpl.java
    |   |-- db/
    |   |   `-- DatabaseConnection.java
    |   |-- model/
    |   |   |-- CardReview.java
    |   |   |-- Deck.java
    |   |   |-- Flashcard.java
    |   |   |-- ObjectFactory.java
    |   |   `-- StudySession.java
    |   |-- service/
    |   |   |-- CardJson.java
    |   |   |-- CsvImportExportService.java
    |   |   |-- DeckJson.java
    |   |   |-- ImportPreview.java
    |   |   |-- JsonImportExportService.java
    |   |   `-- SaveService.java
    |   |-- util/
    |   |   `-- UiScale.java
    |   `-- view/
    |       |-- AllCardsPanel.java
    |       |-- CardDetailPanel.java
    |       |-- CLIView.java
    |       |-- DashboardPanel.java
    |       |-- DeckDetailPanel.java
    |       |-- ExitPanel.java
    |       |-- ImportDialogPanel.java
    |       |-- MainFrame.java
    |       |-- MyDeckPanel.java
    |       |-- QuestionPanel.java
    |       |-- ResultPanel.java
    |       |-- SetupPanel.java
    |       |-- SplashScreen.java
    |       `-- StudyPanel.java
    `-- resources/
        `-- db/
            |-- SampleData.sql
            `-- StudyAssistantSchema.sql
```
