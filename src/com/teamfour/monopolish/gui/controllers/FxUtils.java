package com.teamfour.monopolish.gui.controllers;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Class with different util methods used in controllers.
 */
public class FxUtils {
    /**
     * Limit input length and show warning if defined
     * @param max_length Maximum length of the input in characters
     * @param input TextField target element
     * @param warning Node target warning
     */
    public static void limitInputLength(int max_length, TextField input, Node warning) {
        // Add text listener
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check if input is longer than allowed
            if (input.getText().length() > max_length) {

                // Set input text to value before going over the limit
                input.setText(oldValue);

                // Set cursor to the end of the input
                input.positionCaret(input.getText().length());

                // Show warning if defined
                if (warning != null) warning.setVisible(true);
            }

            // Input length is okay
            else {
                // Hide warning if defined
                if (warning != null) warning.setVisible(false);
            }
        });
    }

    /**
     * Give a number a thousand dot format to ease readability
     *
     * @param num Number that will be formatted (String)
     * @return Formatted number
     */
    public static String thousandDecimalFormat(String num) {
        if (num.length() < 4) return num;
        int thousandIndex = num.length() - 3;
        return num.substring(0, thousandIndex) + "." + num.substring(thousandIndex);
    }

    /**
     * Change text color of a text element
     *
     * @param element Target text node
     * @param color New color of the text
     */
    public static void setTextColor(Text element, String color) {
        element.setFill(Paint.valueOf(color));
    }

    /**
     * Change the border color of a node element. <br/>
     * <b>Note: all other styles set to node will be reset!</b>
     *
     * @param element Target node
     * @param color of the border
     */
    public static void setBorderColor(Node element, String color) {
        element.setStyle("-fx-border-color: " + color);
    }

    public static void setScaleOnHover(Pane elem, double scale) {
        elem.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), elem);
            st.setFromX(elem.getScaleX());
            st.setFromY(elem.getScaleY());
            st.setByX(elem.getScaleX()*scale);
            st.setByY(elem.getScaleY()*scale);
            st.setAutoReverse(true);
            st.play();
        });

        elem.setOnMouseExited(e -> {
            System.out.println("leaving");
            ScaleTransition st = new ScaleTransition(Duration.millis(200), elem);
            st.setFromX(elem.getScaleX());
            st.setFromY(elem.getScaleY());
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

    /**
     * Helper method for showing and changing a button element
     * @param msg
     * @param color
     */
    public static void showAndChangeBtn(Button btn, String msg, String color) {
        btn.setVisible(true);
        btn.setDisable(false);
        btn.setText(msg);
        btn.setStyle("-fx-background-color: " + color + ";");
    }

    /**
     * Helper method for showing anc changing a text element
     * @param elem
     * @param msg
     */
    public static void showAndChangeText(Text elem, String msg) {
        elem.setVisible(true);
        elem.setText(msg);
    }
}
