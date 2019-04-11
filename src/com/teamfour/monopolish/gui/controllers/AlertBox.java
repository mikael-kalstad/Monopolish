package com.teamfour.monopolish.gui.controllers;

import javafx.scene.control.Alert;

/**
 * Class for displaying an alert box for confirmation or warning.
 */
public class AlertBox {
    /**
     *
     * @param alertType Target type for alert box
     * @param title Title of the dialog
     * @param header Header text inside dialog
     * @param message Msg text inside dialog
     * @return Instance of Alert (Show with method showAndWait())
     */
    public static Alert display(Alert.AlertType alertType, String title, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        return alert;
    }
}
