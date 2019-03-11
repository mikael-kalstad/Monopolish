package com.teamfour.monopolish.gui.controllers;


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
