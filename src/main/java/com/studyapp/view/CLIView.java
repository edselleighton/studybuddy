package com.studyapp.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import com.studyapp.controller.CustomException;
import com.studyapp.controller.MainController;
import com.studyapp.model.CardReview;
import com.studyapp.model.Deck;
import com.studyapp.model.Flashcard;
import com.studyapp.model.StudySession;

//MAINLY FOR TESTING OUT IMPLEMENTED METHODS ONLY WITHOUT WORRYING GUI
public class CLIView {
    private MainController mc;
    private Scanner scanner = new Scanner(System.in);

    private static final String BAR =
            "__________________________________________________________";

    public CLIView(MainController mc){
        this.mc = mc;
    }

    public void start(){
        if(mc.tryAutoLogin()){
            mainMenu();
        }else{
            loginAndStart();
        }
    }

    public void loginAndStart() {
        System.out.println("\n--- CONNECT YOUR DATABASE ---");
        while (true) {
            System.out.print("MySQL username: ");
            String username = readLine();
            System.out.print("MySQL password: ");
            String password = readLine();
            try {
                mc.login(username, password);
                System.out.println("Login successful. Credentials saved.\n");
                break;
            } catch (CustomException e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
        mainMenu();
    }

    public void mainMenu(){
        while (true) {
            printMainMenu();
            try {
                int choice = readInt();
                switch (choice) {
                    case 1 -> { manageDecks();}
                    case 2 -> { listCards(mc.allFlashcards());}
                    case 3 -> { printSessions();}
                    case 4 -> { printCardReviews(mc.getAllCardReviews());}
                    case 5 -> { mc.saveChanges();}
                    case 6 -> { System.exit(0);}

                    default -> System.out.println("Invalid choice.\n");
                }
            } catch (CustomException e) {
                System.out.println(e.getMessage() + "\n");
                scanner.nextLine();
            }
        }
    }


//------------ ALL ABOUT DECKS ----------------------//
    void manageDecks(){
        while (true) {
            System.out.println(BAR + "\n--- MANAGE DECKS ---");
            List<Deck> decks = mc.allDecks();

            if (decks.isEmpty()) {
                System.out.println("No decks available.\n");
                return;
            }

            System.out.printf("%-6s %-20s \n", "ID", "NAME");
            for (Deck deck : decks) {
                System.out.printf("%-6d %-20s \n", deck.getDeckID(), deck.getName());
            }

            System.out.print("\n1. SELECT Deck\n2. ADD Deck\n3. Main Menu\nENTER choice: ");
            int choice = readInt();
            switch (choice){
                case 1:
                    System.out.println("\n----- SELECT DECK -----\n");
                    System.out.print("Enter Deck ID to be selected: ");
                    int deckID = readInt();
                    Deck selectedDeck = null;
                    for (Deck deck : decks) {
                        if (deck.getDeckID() == deckID) {
                            selectedDeck = deck;
                            break;
                        }
                    }

                    if (selectedDeck != null) {
                        deckDescription(selectedDeck);
                        return;
                    } else {
                        System.out.println("Deck ID not found. Please try again.");
                    }
                    break;
                case 2:
                    addDeck();
                    break;
                case 3:
                    mainMenu();
                    break;
            }
        }
    }

    void deckDescription(Deck deck){
        while(true){
            System.out.println("\n --- " + deck.getName().toUpperCase() + " ---\n");
            System.out.println("Deck ID: " + deck.getDeckID());
            System.out.println("Cards: " + mc.getFlashcardsByDeck(deck.getDeckID()).size());
            System.out.println("Description: " + deck.getDescription());
            System.out.println("Created at: " + deck.getCreatedAt());

            System.out.println("\nACTIONS: ");
            System.out.println("1. STUDY\n2. EDIT name\n3. EDIT Description\n4. LIST cards in this deck\n5. ADD card\n6. DELETE deck\n7. BACK");
            System.out.print("Enter action: " );
            int choice = readInt();
            switch(choice){
                case 1:
                    study(deck);
                    break;
                case 2:
                    editDeck(0, deck);
                    deckDescription(deck);
                    break;
                case 3:
                    editDeck(1, deck);
                    deckDescription(deck);
                    break;
                case 4:
                    listCards(mc.getFlashcardsByDeck(deck.getDeckID()));
                    break;
                case 5:
                    addCard(deck.getDeckID());
                    break;
                case 6:
                    deleteDeck(deck.getDeckID());
                    mainMenu();
                    break;
                case 7:
                    mainMenu();
                    break;

            }
        }
    }

    void addDeck(){
        while(true){
            System.out.println("\n----- ADD DECK -----\n");
            System.out.print("Enter Deck Name to be added: ");
            String deckName = readLine();
            System.out.print("Enter Deck description (Enter for no description):\n      " );
            String description = readLine();
            try {
                mc.createDeck(deckName, description);
                System.out.println("Deck added successfully.");
                System.out.println(BAR + "\n");
                break;
            } catch (CustomException e) {
                System.out.println(e.getMessage() + "\n");
                System.out.println(BAR + "\n");
            }
        }
    }
    void editDeck(int attribute, Deck deck){
        String value = "";
        while (true){
            try{
                switch(attribute){
                    case 0: //EDIT NAME
                        System.out.print("Enter Deck name: ");
                        value = readLine();
                        deck.setName(value);
                        break;
                    case 1: //EDIT DESCRIPTION
                        System.out.print("Enter description: ");
                        value = readLine();
                        deck.setDescription(value);
                        break;
                }
                mc.updateDeck(deck);
                System.out.println("Deck updated successfully.\n");
                break;
            }catch(CustomException e){
                System.out.println(e.getMessage());
            }
        }
    }

    void deleteDeck(int deckID){
        try {
            mc.deleteDeck(deckID);
            System.out.println("Deck with deck ID: " + deckID + " was deleted.");
            System.out.println(BAR + "\n");
        } catch (CustomException e) {
            System.out.println(e.getMessage());
            System.out.println(BAR + "\n");
        }
    }

//------------- ALL ABOUT CARDS----------------------//

    void listCards(List<Flashcard> flashcards){
        System.out.println(BAR + "\n--- ALL CARDS ---");

        if (flashcards.isEmpty()) {
            System.out.println("No flashcards available.\n");
            return;
        }

        System.out.printf("%-6s %-30s  %-12s\n", "ID", "QUESTION", "DECK ID");
        for (Flashcard card : flashcards) {
            String question = card.getQuestion() == null ? "" : card.getQuestion();
            int deckId = card.getDeck() != null ? card.getDeck().getDeckID() : 0;
            System.out.printf("%-6d %-30.20s   %-12d\n", card.getCardID(), question, deckId);
        }

        while (true) {
            System.out.println("\nEnter card ID to view/manage, or 0 to return:");
            int choice = readInt();
            if (choice == 0) {
                return;
            }

            Flashcard selected = null;
            for (Flashcard card : flashcards) {
                if (card.getCardID() == choice) {
                    selected = card;
                    break;
                }
            }

            if (selected != null) {
                cardDescription(selected);
                return;
            } else {
                System.out.println("Card ID not found. Please try again.");
            }
        }
    }

    void cardDescription(Flashcard card) {
        while (true) {
            System.out.println("\n --- Card " + card.getCardID() + " ---\n");
            System.out.println("Question: " + card.getQuestion());
            System.out.println("Answer: " + card.getAnswer());
            System.out.println("Difficulty: " + card.getDifficulty());
            System.out.println("Deck ID: " + (card.getDeck() != null ? card.getDeck().getDeckID() : "N/A"));
            System.out.println("Created at: " + card.getCreatedAt());

            System.out.println("\nACTIONS: ");
            System.out.println("1. EDIT question\n2. EDIT answer\n3. EDIT difficulty\n4. DELETE card\n5. BACK");
            System.out.print("Enter action: ");
            int choice = readInt();

            switch(choice){
                case 1:
                    editCard(0, card);
                    break;
                case 2:
                    editCard(1, card);
                    break;
                case 3:
                    editCard(2, card);
                    break;
                case 4:
                    deleteCard(card.getCardID());
                    return;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.\n");
            }
        }
    }

    void addCard(int deckID){
        System.out.println("\n--- ADD CARD ---\n");
        System.out.print("Enter question: ");
        String question = readLine();
        System.out.print("Enter answer: ");
        String answer = readLine();
        System.out.print("Enter difficulty level: ");
        String difficulty = readLine();

        try {
            mc.createFlashcard(deckID,question, answer, difficulty);
            System.out.println("Card added successfully.\n");
            System.out.println(BAR + "\n");
        } catch (CustomException e) {
            System.out.println(e.getMessage() + "\n");
            System.out.println(BAR + "\n");
        }
    }

    void editCard(int attribute, Flashcard card) {
        String value = "";
        try {
            switch(attribute) {
                case 0: // EDIT QUESTION
                    System.out.print("Enter new question: ");
                    value = readLine();
                    card.setQuestion(value);
                    break;
                case 1: // EDIT ANSWER
                    System.out.print("Enter new answer: ");
                    value = readLine();
                    card.setAnswer(value);
                    break;
                case 2: // EDIT DIFFICULTY
                    System.out.print("Enter difficulty level: ");
                    value = readLine();
                    card.setDifficulty(value);
                    break;
            }

            mc.updateFlashcard(card);
            System.out.println("Card updated successfully.");
            System.out.println(BAR + "\n");
        } catch(CustomException e) {
            System.out.println(e.getMessage());
            System.out.println(BAR + "\n");
        }
    }

    void deleteCard(int cardID) {
        try {
            mc.deleteFlashcard(cardID);  // Assuming this method exists in MainController
            System.out.println("Card with ID: " + cardID + " was deleted.");
            System.out.println(BAR + "\n");
        } catch (CustomException e) {
            System.out.println(e.getMessage());
            System.out.println(BAR + "\n");
        }
    }

    //------------ ALL ABOUT STUDY SESSIONS ----------------------
    void study(Deck deck){
        LocalDateTime started = LocalDateTime.now();
        LocalDateTime ended = LocalDateTime.now();
        List<Flashcard> flashcards = mc.getFlashcardsByDeck(deck.getDeckID());
        StudySession studySession = new StudySession();

        try {
            studySession = mc.createStudySession(deck.getDeckID(), started);
        }catch (CustomException e){
            System.out.println(e.getMessage());
        }

        int cardCounter = 1;
        boolean[] correctAnswers = new boolean[flashcards.size()];
        int totalCorrect = 0;
        while (cardCounter <= flashcards.size()){
            System.out.println(BAR+"\n--- STUDYING " + deck.getName().toUpperCase() + " ---\n");
            System.out.print("Score: " + totalCorrect + "/" + flashcards.size() + "\n");
            LocalDateTime reviewedAt = LocalDateTime.now();
            Flashcard card = flashcards.get(cardCounter - 1);
            System.out.println(cardCounter++ + ". " + card.getQuestion() + "\n");

            System.out.print("Enter answer: ");
            String answer = readLine().toLowerCase();
            boolean isCorrect = false;
            if(answer.equals(card.getAnswer().toLowerCase())){
                System.out.println("\nCorrect.\n");
                if (!correctAnswers[cardCounter - 2]) {
                    correctAnswers[cardCounter - 2] = true;
                    totalCorrect++;
                }
                isCorrect = true;
            }else{
                System.out.println("\nNot quite right.\n");
                correctAnswers[cardCounter - 2] = false;
            }

            try {
                mc.createCardReview(studySession.getSessionID(), card.getCardID(), reviewedAt, isCorrect);
            } catch (CustomException e) {
                System.out.println(e.getMessage());
            }

            System.out.print("1. NEXT\n2. RETRY\n3. PREVIOUS\n4. FINISH STUDY\nENTER choice: ");
            int choice = readInt();
            switch(choice){
                case 1:
                    break;
                case 2:
                    cardCounter--;
                    break;
                case 3:
                    if(cardCounter == 2){
                        cardCounter = 1;
                        break;
                    }
                    cardCounter = cardCounter-2;
                    break;
                case 4:
                    try {
                        studySession.setEndedAt(LocalDateTime.now());
                        mc.updateEndStudySession(studySession);
                    } catch (CustomException e) {
                        System.out.println(e.getMessage());
                    }
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
        System.out.println("\n" + BAR + "\nYOUR SCORE WAS " + totalCorrect + "/" + flashcards.size() + "\n"+ BAR + "\n");
        studySession.setEndedAt(LocalDateTime.now());
    }

    void printSessions(){
        while (true) {
            System.out.println(BAR + "\n--- ALL SESSIONS ---");
            List<StudySession> sessions = mc.getAllSessions();
            if (sessions.isEmpty()) {
                System.out.println("No sessions available.\n");
                return;
            }

            System.out.printf("%-6s %-20s %-20s\n", "ID", "DECK NAME", "STARTED AT");
            for(StudySession session: sessions){
                System.out.printf("%-6d %-20s %-20s\n", session.getSessionID(), session.getDeck().getName(), session.getStartedAt());
            }

            System.out.print("\n1. SELECT Session\n2. Main Menu\nENTER choice: ");
            int choice = readInt();
            switch(choice){
                case 1:
                    System.out.println("\n----- SELECT SESSION -----\n");
                    System.out.print("Enter Session ID to be selected: ");
                    int sessionID = readInt();
                    StudySession selectedSession = null;
                    for (StudySession session : sessions) {
                        if (session.getSessionID() == sessionID) {
                            selectedSession = session;
                            break;
                        }
                    }

                    if (selectedSession != null) {
                        sessionDescription(selectedSession);
                        return;
                    } else {
                        System.out.println("Session ID not found. Please try again.");
                    }
                    break;
                case 2:
                    mainMenu();
                    return;
            }
        }
    }

    void sessionDescription(StudySession session){
        while(true){
            System.out.println("\n --- Session " + session.getSessionID() + " ---\n");
            System.out.println("Session ID: " + session.getSessionID());
            System.out.println("Deck: " + session.getDeck().getName());
            System.out.println("Started at: " + session.getStartedAt());
            System.out.println("Ended at: " + (session.getEndedAt() != null ? session.getEndedAt() : "Still studying"));

            System.out.println("\nACTIONS: ");
            System.out.println("1. What I Reviewed");
            System.out.println("2. BACK");
            System.out.print("Enter action: " );
            int choice = readInt();
            switch(choice){
                case 1:
                    printCardReviews(mc.getCardReviewsBySession(session.getSessionID()));
                    return;
                case 2:
                    return;
                default:
                    System.out.println("Invalid choice.\n");
            }
        }
    }

    //---------- ALL ABOUT CARD REVIEW ----------------
    void printCardReviews(List<CardReview> reviews){
        while (true) {
            System.out.println(BAR + "\n--- CARDS REVIEWED ---");
            if (reviews.isEmpty()) {
                System.out.println("No card reviews available.\n");
                return;
            }

            System.out.printf("%-6s %-30s %-12s\n", "ID", "QUESTION", "IS CORRECT");
            for(CardReview review: reviews){
                String question = review.getFlashcard().getQuestion() == null ? "" : review.getFlashcard().getQuestion();
                String isCorrect = review.isCorrect() ? "YES" : "NO";
                System.out.printf("%-6d %-30.25s %-12s\n", review.getReviewID(), question, isCorrect);
            }

            System.out.print("\n1. SELECT Review\n2. Main Menu\nENTER choice: ");
            int choice = readInt();
            switch(choice){
                case 1:
                    System.out.println("\n----- SELECT REVIEW -----\n");
                    System.out.print("Enter Review ID to be selected: ");
                    int reviewID = readInt();
                    CardReview selectedReview = null;
                    for (CardReview review : reviews) {
                        if (review.getReviewID() == reviewID) {
                            selectedReview = review;
                            break;
                        }
                    }

                    if (selectedReview != null) {
                        reviewDescription(selectedReview);
                        return;
                    } else {
                        System.out.println("Review ID not found. Please try again.");
                    }
                    break;
                case 2:
                    mainMenu();
                    return;
            }
        }
    }

    void reviewDescription(CardReview cardReview){
        while(true){
            System.out.println("\n --- Card Review " + cardReview.getReviewID() + " ---\n");
            System.out.println("Review ID: " + cardReview.getReviewID());
            System.out.println("Question: " + cardReview.getFlashcard().getQuestion());
            System.out.println("Answer: " + cardReview.getFlashcard().getAnswer());
            System.out.println("Your Answer: " + (cardReview.isCorrect() ? "Correct" : "Incorrect"));
            System.out.println("Reviewed at: " + cardReview.getReviewedAt());
            System.out.println("Session ID: " + cardReview.getStudySession().getSessionID());

            System.out.println("\nACTIONS: ");
            System.out.println("1. BACK");
            System.out.print("Enter action: " );
            int choice = readInt();
            switch(choice){
                case 1:
                    return;
                default:
                    System.out.println("Invalid choice.\n");
            }
        }
    }

    //-----------  HELPER METHODS --------------------
    void printMainMenu() {
        System.out.println(BAR + "\n");
        System.out.println("--- STUDY ASSISTANT APP ---");
        System.out.println("  1. MANAGE decks");
        System.out.println("  2. ALL cards");
        System.out.println("  3. All sessions");
        System.out.println("  4. All card reviews");
        System.out.println("  5. SAVE to database");
        System.out.println("  6. EXIT");
        System.out.print("SELECT: ");
    }

    String readLine() { return scanner.nextLine().trim(); }

    int readInt() {
        while (true) {
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("Enter a valid number: "); }
        }
    }
}
