import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class App extends Application {

    private Stage stage;

    private List<Person> people;
    private List<Question> questions;
    private AkinatorEngine engine;

    private Question currentQuestion;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        people = DataLoader.loadPeople("people.txt");
        questions = DataLoader.loadQuestions("questions.txt");
        engine = new AkinatorEngine(people, questions);

        stage.setTitle("Mini Akinator");
        showMenu();
        stage.show();
    }

    /* =======================
            MENU SCENE
       ======================= */
    private void showMenu() {
        Label title = new Label("MINI AKINATOR");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button startBtn = new Button("Start Game");
        Button addBtn = new Button("Add Character");
        Button exitBtn = new Button("Exit");

        startBtn.setOnAction(e -> startGame());
        addBtn.setOnAction(e -> showAddCharacter());
        exitBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(15, title, startBtn, addBtn, exitBtn);
        root.setStyle("-fx-padding: 30; -fx-alignment: center;");

        stage.setScene(new Scene(root, 400, 300));
    }

    /* =======================
            GAME SCENE
       ======================= */
    private void startGame() {
        engine.reset();

        Label questionLabel = new Label();
        questionLabel.setStyle("-fx-font-size: 16px;");

        Label guessLabel = new Label();

        Button yesBtn = new Button("YES");
        Button noBtn = new Button("NO");
        Button idkBtn = new Button("I DON'T KNOW");

        yesBtn.setOnAction(e -> answer(true, questionLabel, guessLabel));
        noBtn.setOnAction(e -> answer(false, questionLabel, guessLabel));
        idkBtn.setOnAction(e -> answer(null, questionLabel, guessLabel));

        VBox root = new VBox(15, questionLabel, guessLabel,
                yesBtn, noBtn, idkBtn);
        root.setStyle("-fx-padding: 30; -fx-alignment: center;");

        stage.setScene(new Scene(root, 450, 300));
        nextQuestion(questionLabel);
    }

    private void nextQuestion(Label questionLabel) {
        if (engine.finished()) {
            showEndScreen();
            return;
        }

        currentQuestion = engine.nextQuestion();
        if (currentQuestion != null) {
            questionLabel.setText(currentQuestion.text);
        }
    }

    private void answer(Boolean ans, Label questionLabel, Label guessLabel) {
        engine.answer(currentQuestion, ans);

        Person guess = engine.bestGuess();
        if (guess != null) {
            guessLabel.setText(
                "Current Guess: " + guess.name +
                " (" + engine.bestConfidence() + "%)"
            );
        }

        nextQuestion(questionLabel);
    }

    /* =======================
            END SCENE
       ======================= */
    private void showEndScreen() {
        Person guess = engine.bestGuess();

        Label result = new Label(
            "I guess: " + guess.name + "\nConfidence: " +
            engine.bestConfidence() + "%"
        );
        result.setStyle("-fx-font-size: 16px;");

        Button correctBtn = new Button("Correct!");
        Button wrongBtn = new Button("Wrong");
        Button menuBtn = new Button("Back to Menu");

        correctBtn.setOnAction(e -> showMenu());
        wrongBtn.setOnAction(e -> showAddCharacter());
        menuBtn.setOnAction(e -> showMenu());

        VBox root = new VBox(15, result, correctBtn, wrongBtn, menuBtn);
        root.setStyle("-fx-padding: 30; -fx-alignment: center;");

        stage.setScene(new Scene(root, 450, 300));
    }

    /* =======================
        ADD CHARACTER SCENE
       ======================= */
    private void showAddCharacter() {
        TextField nameField = new TextField();
        nameField.setPromptText("Character Name");

        VBox questionBox = new VBox(5);
        Map<Question, ComboBox<String>> inputs = new HashMap<>();

        for (Question q : questions) {
            ComboBox<String> cb = new ComboBox<>();
            cb.getItems().addAll("Yes", "No", "I don't know");
            cb.setValue("I don't know");

            inputs.put(q, cb);
            questionBox.getChildren().addAll(new Label(q.text), cb);
        }

        Button saveBtn = new Button("Save Character");
        Button cancelBtn = new Button("Cancel");

        saveBtn.setOnAction(e -> {
            try {
                saveCharacter(nameField.getText(), inputs);
                showMenu();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        cancelBtn.setOnAction(e -> showMenu());

        ScrollPane scroll = new ScrollPane(questionBox);
        scroll.setFitToWidth(true);

        VBox root = new VBox(10, nameField, scroll, saveBtn, cancelBtn);
        root.setStyle("-fx-padding: 20;");

        stage.setScene(new Scene(root, 500, 500));
    }

    private void saveCharacter(String name, Map<Question, ComboBox<String>> inputs)
            throws IOException {

        Map<String, Boolean> answers = new HashMap<>();

        for (Map.Entry<Question, ComboBox<String>> entry : inputs.entrySet()) {
            String val = entry.getValue().getValue();
            Boolean ans = null;
            if (val.equals("Yes")) ans = true;
            else if (val.equals("No")) ans = false;

            answers.put(entry.getKey().key, ans);
        }

        people.add(new Person(name, answers));
        DataLoader.savePeople("people.txt", people);
    }

    public static void main(String[] args) {
        launch();
    }
}
