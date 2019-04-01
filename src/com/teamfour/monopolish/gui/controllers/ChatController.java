package com.teamfour.monopolish.gui.controllers;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ChatController {
    // Chat elements
    @FXML Pane chatContainer;
    @FXML Pane chatMessagesContainer;
    @FXML ScrollPane chatMessageScrollPane;
    @FXML TextField chatInput;
    @FXML Pane chatWarning;
    @FXML Circle unreadContainer;
    @FXML Text unreadValue;

    int current_msg_count = 0;
    boolean chatOpen = false;
    int CHAT_MAX_CHARACTERS = 40;

    // Timer that will periodically update the chat
    private Timer chatTimer = new Timer();

     @FXML public void initialize() {
        // "Close" chat / move it down
        chatContainer.setTranslateY(275);

        // Update chat messages periodically
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    ArrayList<String[]> chatContent = Handler.getGameDAO().getChat(Handler.getCurrentGameId());

                    // Reset all chat messages
                    chatMessagesContainer.getChildren().clear();

                    // Go through all the messages and add them to the chat
                    for (String[] message : chatContent) {
                        GameControllerDrawFx.createChatRow(
                                chatMessagesContainer,
                                message[0].trim(),
                                message[2].trim(),
                                message[1].trim()
                        );

                        // Scroll to bottom of the chat
                        chatMessageScrollPane.setVvalue(1);
                    }

                    if (!chatOpen && current_msg_count < chatMessagesContainer.getChildren().size()) {
                        unreadValue.setVisible(true);
                        unreadContainer.setVisible(true);
                        String unreadMsgCount = "9+";
                        if (chatMessagesContainer.getChildren().size() - current_msg_count < 10) {
                            unreadMsgCount = String.valueOf(chatMessagesContainer.getChildren().size() - current_msg_count);
                            unreadValue.setStyle("-fx-font-size: 16px");
                        } else {
                            unreadValue.setStyle("-fx-font-size: 12px");
                        }
                        unreadValue.setText(unreadMsgCount);
                    }
                    else {
                        unreadValue.setVisible(false);
                        unreadContainer.setVisible(false);
                    }
                });
            }
        };

        chatTimer = new Timer();
        long delay = 1000L; // Delay before update refreshTimer starts
        long period = 1000L; // Delay between each update/refresh
        chatTimer.scheduleAtFixedRate(task, delay, period);

        // Check if chat input has reached max number of characters
        chatInput.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check if input is longer than allowed
            if (chatInput.getText().length() > CHAT_MAX_CHARACTERS) {
                // Set input text to value before going over the limit
                chatInput.setText(oldValue);

                // Set cursor to the end of the input
                chatInput.positionCaret(chatInput.getText().length());

                // Change border style and show warning
                chatInput.setStyle("-fx-border-color: orange");
                chatWarning.setVisible(true);
            } else {
                // Reset border style and hide warning
                chatInput.setStyle("-fx-border-color: white");
                chatWarning.setVisible(false);
            }
        });
    }

    /**
     * This method will open or close the chat,
     * depending if the chat is open or closed.
     */
    public void toggleChat() {
        // Slider animation for chat-container
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), chatContainer);

        // Close chat
        if (chatOpen) {
            // Set to default position
            tt.setByY(275);
            tt.play();
            chatOpen = false;
            current_msg_count = chatMessagesContainer.getChildren().size();
        }

        // Open chat
        else {
            // Move up
            tt.setByY(-275);
            tt.play();
            chatOpen = true;
        }
    }

    /**
     * Sends a string text from the chat input box to the chat
     */
    public void addChatMessage() {
        if (chatInput.getText().trim().isEmpty()) {
            chatInput.setStyle("-fx-border-color: yellow;");
        } else {
            chatInput.setStyle("-fx-border-color: white;");
            Handler.getGameDAO().addChatMessage(Handler.getAccount().getUsername(), chatInput.getText().trim());

            // Reset input text
            chatInput.setText("");
        }
    }

    public Timer getChatTimer() {
        return chatTimer;
    }
}
