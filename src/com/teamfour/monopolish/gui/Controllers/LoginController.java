import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController extends Application {
    @FXML private TextField usernameInput;
    @FXML private TextField passwordInput;
    @FXML private Button loginBtn;
    @FXML private Button registerBtn;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML file to scene
        Parent login = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene loginScene = new Scene(login);
        loginScene.getStylesheets().add("styles.css");

        // Add scene to stage
        primaryStage.setTitle("Monopoly");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public void login() {
        //usernameInput.getStyleClass().removeAll();
        usernameInput.getStyleClass().add("warning");
        usernameInput.setStyle("styles.css");

        System.out.println(
                "logging in... \n" +
                "username: " + usernameInput.getText() + "\n" +
                "password: " + passwordInput.getText());
    }
}
