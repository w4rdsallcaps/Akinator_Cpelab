import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // LOAD YOUR FXML FILE
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            
            // SETUP THE SCENE
            Scene scene = new Scene(root);
            
            // (Optional) Add CSS if you have it, otherwise delete this line
            // scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            
            primaryStage.setTitle("The Markinator");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Keeps the window size fixed
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}