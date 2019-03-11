package com.teamfour.monopolish.gui.controllers;

<<<<<<< HEAD

=======
>>>>>>> 79ecd361c5d3828b93a76570f3661d1c495d5fe0
import javafx.scene.control.Alert;

public class AlertBox {

    public static void display(Alert.AlertType alertType, String title, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
