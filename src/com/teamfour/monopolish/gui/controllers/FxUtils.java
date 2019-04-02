package com.teamfour.monopolish.gui.controllers;

import javafx.scene.Node;
import javafx.scene.control.TextField;

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
}
