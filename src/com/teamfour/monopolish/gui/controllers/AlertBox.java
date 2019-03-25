package com.teamfour.monopolish.gui.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

public class AlertBox {
    public static Alert display(Alert.AlertType alertType, String title, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        //alert.showAndWait();
        return alert;
    }

    public static GridPane drawAlertBox(String color, String titleMsg, String dialogMsg) {
        GridPane grid = new GridPane();
        grid.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-background-radius: 15px;"
        );

        // Row constraints
        RowConstraints content = new RowConstraints();
        RowConstraints buttons = new RowConstraints();
        content.setPrefHeight(200);
        buttons.setPrefHeight(50);
        grid.getRowConstraints().addAll(content, buttons);

        // Content section
        Text title = new Text(titleMsg);
        Text msg = new Text(dialogMsg);
        grid.add(title, 0, 0);
        grid.add(msg, 1, 0);

        // Buttons section
        Button yes = new Button();
        Button no = new Button();
        yes.setId("yes");
        no.setId("no");
        grid.add(yes, 0, 1);
        grid.add(no, 1, 1);

        return grid;
    }
}
