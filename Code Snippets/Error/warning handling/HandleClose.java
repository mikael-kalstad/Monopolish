import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HandleClose extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Adding some text to window
        Label text = new Label("Try closing this window");
        Scene scene = new Scene(text);

        // Setting title and scene
        primaryStage.setTitle("Handle closing");
        primaryStage.setWidth(400);
        primaryStage.setHeight(200);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Handle window closing
        primaryStage.setOnCloseRequest(e -> {
            e.consume(); // Override default closing of window
            closeProgram(primaryStage); // Run closing method
        });
    }

    private void closeProgram(Stage stage) {
        // If answer is yes, close the windows, else don't.
        Boolean answer = ConfirmDialogBox.display("Warning", "Are you sure you want to leave?");
        if (answer) stage.close();
    }
}

class ConfirmDialogBox {
    static boolean answer;

    public static boolean display(String title, String message) {
        Stage window = new Stage();
        window.setHeight(100);
        window.setWidth(250);
        window.setTitle(title);

        // Adding message text
        Label msg = new Label();
        msg.setText(message);

        // Creating btns
        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("No");

        // Btns event listener
        yesBtn.setOnAction(e -> {
            answer = true;
            window.close(); // Close dialog window
        });

        noBtn.setOnAction(e -> {
            answer = false;
            window.close(); // Close dialog window
        });

        // Making grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Adding btns to layout
        grid.add(msg, 0, 0);
        grid.add(yesBtn, 0, 1);
        grid.add(noBtn, 1 , 1);

        // Adding layout to scene
        Scene scene = new Scene(grid);
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
}