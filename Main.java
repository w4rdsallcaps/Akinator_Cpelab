import java.util.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);

            List<Person> people = DataLoader.loadPeople("people.txt");
            List<Question> questions = DataLoader.loadQuestions("questions.txt");
            AkinatorEngine engine = new AkinatorEngine(people, questions);

            boolean running = true;

            while (running) {
                System.out.println("\n=== MINI AKINATOR (TERMINAL) ===");
                System.out.println("Start Game: (y)");
                System.out.println("Input new character: (i)");
                System.out.print("EXIT: (E) ");
                System.out.println("\nEnter choice: ");
                String choice = sc.nextLine().trim().toLowerCase();

                switch (choice) {
                    case "y":
                        engine.reset();
                        playGame(sc, engine, people, questions);
                        break;
                    case "i":
                        addNewCharacter(sc, people, questions);
                        break;
                    case "e":
                        System.out.println("Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid input. Please enter y, i, or E.");
                }
            }

            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private static void playGame(Scanner sc, AkinatorEngine engine, List<Person> people, List<Question> questions) throws IOException {
    System.out.println("\nAnswer with: y = Yes, n = No, i = I don't know\n");

    while (!engine.finished()) {
        Question q = engine.nextQuestion();
        if (q == null) break;

        System.out.println(q.text);
        System.out.print("Your answer (y/n/i): ");
        String ans = sc.nextLine().trim().toLowerCase();

        Boolean answer = null;
        if (ans.equals("y")) answer = true;
        else if (ans.equals("n")) answer = false;
        // 'i' or invalid input = null
        engine.answer(q, answer);

        // Show current best guess and confidence after each question
        Person currentGuess = engine.bestGuess();
        int currentConfidence = engine.bestConfidence();
        if (currentGuess != null) {
            System.out.println("\nCurrent guess: " + currentGuess.name + " (Confidence: " + currentConfidence + "%)\n");
        }
    }

    // Final guess
    Person guess = engine.bestGuess();
    int confidence = engine.bestConfidence();

    System.out.println("\nI guess: " + guess.name);
    System.out.println("Confidence: " + confidence + "%");
    System.out.print("Am I correct? (y/n): ");

    String correct = sc.nextLine().trim().toLowerCase();
    if (correct.equals("n")) {
        addNewCharacter(sc, people, questions);
    } else {
        System.out.println("Great!!!! I guessed your character!");
    }
}

    private static void addNewCharacter(Scanner sc, List<Person> people, List<Question> questions) throws IOException {
        System.out.print("Who were you thinking of? ");
        String name = sc.nextLine();

        Map<String, Boolean> answers = new HashMap<>();
        for (Question q : questions) {
            System.out.println(q.text + " (y/n/i)");
            String ans = sc.nextLine().trim().toLowerCase();
            Boolean answer = null;
            if (ans.equals("y")) answer = true;
            else if (ans.equals("n")) answer = false;
            answers.put(q.key, answer);
        }

        Person newPerson = new Person(name, answers);
        people.add(newPerson);
        DataLoader.savePeople("people.txt", people);

        System.out.println("Thanks! I've learned a new character.");
    }
}
