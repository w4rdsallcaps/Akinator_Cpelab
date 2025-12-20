import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane; // Ensure this is imported
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MainController implements Initializable {

    // --- GUI ELEMENTS ---
    @FXML private VBox startMenuBox;
    @FXML private AnchorPane gamePane;
    @FXML private ImageView gameSprite;
    @FXML private Label lblQuestion;
    
    // --- ADD CHARACTER ELEMENTS ---
    @FXML private VBox addCharBox;
    @FXML private TextField txtNameInput;
    @FXML private VBox questionsContainer; // The new list container

    // --- ENGINE & DATA ---
    private AkinatorEngine engine;
    private MediaPlayer musicPlayer;
    private Random random = new Random();
    
    // DATA STORAGE
    private List<Question> allQuestions; // Store questions here so we can access them later
    private Question currentQuestion; 
    private boolean isGuessing = false;

    // MAP TO STORE USER INPUTS (Question -> Dropdown Box)
    private Map<Question, ComboBox<String>> newCharInputs = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. LOAD DATA
        try {
            String peopleFile = "people.txt";
            String questionsFile = "questions.txt";
            
            // Load lists into class-level variables
            List<Person> people = DataLoader.loadPeople(peopleFile);
            allQuestions = DataLoader.loadQuestions(questionsFile); // Save this for the Add Screen!
            
            engine = new AkinatorEngine(people, allQuestions);
            System.out.println("Engine started!");
        } catch (Exception e) {
            System.out.println("CRITICAL ERROR: Data missing.");
            e.printStackTrace();
        }

        // 2. PLAY MUSIC
        try {
            String musicPath = getClass().getResource("/Resource/audio/music.mp3").toExternalForm();
            musicPlayer = new MediaPlayer(new Media(musicPath));
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.play();
        } catch (Exception e) { }
    }

    // --- START GAME ---
    @FXML
    public void onStartClicked() {
        if (engine == null) return;
        startMenuBox.setVisible(false);
        gamePane.setVisible(true);
        currentQuestion = engine.nextQuestion();
        updateGameScreen();
    }

    // --- OPEN ADD CHARACTER SCREEN (Dynamic Generation) ---
    @FXML
    public void onAddCharClicked() {
        startMenuBox.setVisible(false);
        gamePane.setVisible(false);
        addCharBox.setVisible(true);
        
        // 1. Clear previous list
        questionsContainer.getChildren().clear();
        newCharInputs.clear();
        txtNameInput.clear();

        // 2. Loop through ALL questions and create a row for each
        for (Question q : allQuestions) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            // The Dropdown
            ComboBox<String> cb = new ComboBox<>();
            cb.getItems().addAll("Yes", "No", "I don't know");
            cb.setValue("I don't know"); // Default
            cb.setStyle("-fx-pref-width: 120;");

            // The Label
            Label qL = new Label(q.text);
            qL.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

            // Add to row
            row.getChildren().addAll(cb, qL);
            
            // Add row to the screen container
            questionsContainer.getChildren().add(row);
            
            // Remember this input for saving later
            newCharInputs.put(q, cb);
        }
    }

    // --- SAVE NEW CHARACTER ---
    @FXML
    public void onSaveCharClicked() {
        String name = txtNameInput.getText().trim();
        if (name.isEmpty()) return;

        try {
            // 1. Collect answers from the dropdowns
            Map<String, Boolean> attributes = new HashMap<>();
            
            for (Map.Entry<Question, ComboBox<String>> entry : newCharInputs.entrySet()) {
                Question q = entry.getKey();
                String val = entry.getValue().getValue();
                
                // Convert text to Boolean
                Boolean boolVal = null;
                if (val.equals("Yes")) boolVal = true;
                else if (val.equals("No")) boolVal = false;
                
                attributes.put(q.key, boolVal);
            }

            // 2. Create and Save the Person
            List<Person> currentPeople = DataLoader.loadPeople("people.txt");
            currentPeople.add(new Person(name, attributes));
            DataLoader.savePeople("people.txt", currentPeople);

            // 3. Restart Engine with new data
            initialize(null, null);
            System.out.println("Saved new character: " + name);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        onCancelAddClicked(); // Go back to menu
    }

    @FXML
    public void onCancelAddClicked() {
        addCharBox.setVisible(false);
        startMenuBox.setVisible(true);
    }

    // --- GAME LOGIC (Unchanged) ---
    @FXML public void onYesClicked() { processAnswer("YES"); }
    @FXML public void onNoClicked()  { processAnswer("NO"); }
    @FXML public void onIdkClicked() { processAnswer("IDK"); }

    private void processAnswer(String answerString) {
        if (isGuessing) {
            resetGame();
            return;
        }
        if (currentQuestion == null) return;

        Boolean userAns = null;
        if (answerString.equals("YES")) userAns = true;
        else if (answerString.equals("NO")) userAns = false;

        engine.answer(currentQuestion, userAns);
        currentQuestion = engine.nextQuestion();
        updateGameScreen();
    }

    private void updateGameScreen() {
        if (currentQuestion == null) {
            isGuessing = true;
            Person guess = engine.bestGuess();
            if (guess != null) lblQuestion.setText("Is it... " + guess.name + "?");
            else lblQuestion.setText("I am stumped! Add this character?");
            return;
        }

        isGuessing = false;
        lblQuestion.setText(currentQuestion.text);
        updateSprite(engine.bestConfidence());
    }

    private void updateSprite(int percent) {
        String baseName = "low";
        int variants = 2;
        if (percent >= 86) { baseName = "high"; variants = 1; } 
        else if (percent >= 71) { baseName = "good"; variants = 2; } 
        else if (percent >= 51) { baseName = "neutral"; variants = 3; } 
        else if (percent >= 21) { baseName = "doubt"; variants = 3; } 
        
        int pick = random.nextInt(variants) + 1;
        String finalFileName = baseName + "_" + pick + ".png";

        try {
            Image img = new Image(getClass().getResourceAsStream("/Resource/assets/" + finalFileName));
            gameSprite.setImage(img);
        } catch (Exception e) { }
    }
    
    private void resetGame() {
        engine.reset();
        gamePane.setVisible(false);
        startMenuBox.setVisible(true);
        lblQuestion.setText("Ready?");
        isGuessing = false;
        updateSprite(50);
    }

    @FXML public void onExitClicked() { System.exit(0); }
}