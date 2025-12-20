import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
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

        stage.setTitle("Markinator");
        showMenu();
        stage.show();
    }

    /* =======================
            UI HELPERS
       ======================= */
    
    private StackPane createBaseLayout() {
        StackPane root = new StackPane();
        try {
            URL bgUrl = getClass().getResource("/background.png");
            if (bgUrl != null) {
                root.setStyle("-fx-background-image: url('" + bgUrl.toExternalForm() + "'); " +
                              "-fx-background-size: cover; -fx-background-position: center;");
            } else {
                root.setStyle("-fx-background-color: #330000;"); 
            }
        } catch (Exception e) {
            root.setStyle("-fx-background-color: black;");
        }
        return root;
    }

    private void styleButton(Button b) {
        b.setStyle("-fx-background-color: white; -fx-background-radius: 25; -fx-border-color: #555; " +
                   "-fx-border-radius: 25; -fx-min-width: 250; -fx-min-height: 45; " +
                   "-fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle() + "-fx-background-color: #eeeeee;"));
        b.setOnMouseExited(e -> b.setStyle(b.getStyle() + "-fx-background-color: white;"));
    }

    /* =======================
            MENU SCENE
       ======================= */
    private void showMenu() {
        StackPane root = createBaseLayout();
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        Label title = new Label("MARKINATOR");
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: gold; " +
                       "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

        // The only Genie image remains here on the start menu
        ImageView menuGenie = new ImageView();
        try {
            URL genieUrl = getClass().getResource("/genie.png");
            if (genieUrl != null) {
                menuGenie.setImage(new Image(genieUrl.toExternalForm()));
                menuGenie.setFitHeight(250);
                menuGenie.setPreserveRatio(true);
            }
        } catch (Exception e) {}

        Button startBtn = new Button("Start Game");
        Button addBtn = new Button("Add Character");
        Button exitBtn = new Button("Exit");

        styleButton(startBtn);
        styleButton(addBtn);
        styleButton(exitBtn);

        startBtn.setOnAction(e -> startGame());
        addBtn.setOnAction(e -> showAddCharacter());
        exitBtn.setOnAction(e -> stage.close());

        content.getChildren().addAll(title, menuGenie, startBtn, addBtn, exitBtn);
        root.getChildren().add(content);
        stage.setScene(new Scene(root, 800, 650));
    }

    /* =======================
            GAME SCENE
       ======================= */
    private void startGame() {
        engine.reset();
        StackPane root = createBaseLayout();
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        Label questionLabel = new Label();
        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(400);
        questionLabel.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                               "-fx-padding: 30; -fx-font-size: 20px; -fx-text-alignment: center;");

        Label guessLabel = new Label();
        guessLabel.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-font-size: 14px;");

        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("No");
        Button idkBtn = new Button("I Don't Know");

        styleButton(yesBtn);
        styleButton(noBtn);
        styleButton(idkBtn);

        yesBtn.setOnAction(e -> handleAnswer(true, questionLabel, guessLabel));
        noBtn.setOnAction(e -> handleAnswer(false, questionLabel, guessLabel));
        idkBtn.setOnAction(e -> handleAnswer(null, questionLabel, guessLabel));

        // Content now only has the question bubble, buttons, and confidence text
        content.getChildren().addAll(questionLabel, yesBtn, noBtn, idkBtn, guessLabel);
        root.getChildren().add(content);

        stage.setScene(new Scene(root, 800, 650));
        updateNextQuestion(questionLabel);
    }

    private void updateNextQuestion(Label qLabel) {
        if (engine.finished()) {
            showEndScreen();
            return;
        }
        currentQuestion = engine.nextQuestion();
        if (currentQuestion != null) qLabel.setText(currentQuestion.text);
    }

    private void handleAnswer(Boolean ans, Label qLabel, Label gLabel) {
        engine.answer(currentQuestion, ans);
        Person guess = engine.bestGuess();
        if (guess != null) {
            gLabel.setText("Thinking: " + guess.name + " (" + engine.bestConfidence() + "%)");
        }
        updateNextQuestion(qLabel);
    }

    /* =======================
            END SCENE
       ======================= */
    private void showEndScreen() {
        StackPane root = createBaseLayout();
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        Person guess = engine.bestGuess();
        int conf = engine.bestConfidence();

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);
        resultLabel.setMaxWidth(500);
        resultLabel.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                             "-fx-padding: 30; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-alignment: center;");

        if (conf <= 10 || guess == null) {
            resultLabel.setText("I'm stumped! I don't know who you are thinking of.\n\nWould you like to teach me?");
            Button addBtn = new Button("Yes, I'll Teach You!");
            Button menuBtn = new Button("No, Back to Menu");
            styleButton(addBtn); styleButton(menuBtn);
            addBtn.setOnAction(e -> showAddCharacter());
            menuBtn.setOnAction(e -> showMenu());
            content.getChildren().addAll(resultLabel, addBtn, menuBtn);
        } else {
            resultLabel.setText("I guess: " + guess.name + "!\nAm I correct?");
            Button correctBtn = new Button("Yes, You Got It!");
            Button wrongBtn = new Button("No, You're Wrong");
            Button menuBtn = new Button("Back to Menu");
            styleButton(correctBtn); styleButton(wrongBtn); styleButton(menuBtn);

            correctBtn.setOnAction(e -> {
                resultLabel.setText("I knew it! I am the best!");
                content.getChildren().removeAll(correctBtn, wrongBtn);
            });

            wrongBtn.setOnAction(e -> showAddCharacter());
            menuBtn.setOnAction(e -> showMenu());
            content.getChildren().addAll(resultLabel, correctBtn, wrongBtn, menuBtn);
        }

        root.getChildren().add(content);
        stage.setScene(new Scene(root, 800, 650));
    }

    /* =======================
        ADD CHARACTER SCENE
       ======================= */
    private void showAddCharacter() {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #222; -fx-padding: 30;");

        Label title = new Label("Add New Character");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name...");
        nameField.setMaxWidth(300);

        VBox qList = new VBox(10);
        Map<Question, ComboBox<String>> inputs = new HashMap<>();

        for (Question q : questions) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label qL = new Label(q.text);
            qL.setStyle("-fx-text-fill: white;");
            ComboBox<String> cb = new ComboBox<>();
            cb.getItems().addAll("Yes", "No", "I don't know");
            cb.setValue("I don't know");
            inputs.put(q, cb);
            row.getChildren().addAll(cb, qL);
            qList.getChildren().add(row);
        }

        ScrollPane scroll = new ScrollPane(qList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);
        scroll.setStyle("-fx-background: #333; -fx-border-color: #333;");

        Button saveBtn = new Button("Save Character");
        styleButton(saveBtn);
        saveBtn.setOnAction(e -> {
            try {
                saveCharacter(nameField.getText(), inputs);
                showMenu();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        Button cancelBtn = new Button("Cancel");
        styleButton(cancelBtn);
        cancelBtn.setOnAction(e -> showMenu());

        content.getChildren().addAll(title, nameField, scroll, saveBtn, cancelBtn);
        stage.setScene(new Scene(content, 800, 650));
    }

    private void saveCharacter(String name, Map<Question, ComboBox<String>> inputs) throws IOException {
        Map<String, Boolean> answers = new HashMap<>();
        for (var entry : inputs.entrySet()) {
            String val = entry.getValue().getValue();
            Boolean ans = val.equals("Yes") ? true : (val.equals("No") ? false : null);
            answers.put(entry.getKey().key, ans);
        }
        people.add(new Person(name, answers));
        DataLoader.savePeople("people.txt", people);
    }

    public static void main(String[] args) { 
        launch();
     }
}