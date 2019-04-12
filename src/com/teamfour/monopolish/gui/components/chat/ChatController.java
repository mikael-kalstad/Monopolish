package com.teamfour.monopolish.gui.components.chat;

import com.teamfour.monopolish.gui.FxUtils;
import com.teamfour.monopolish.gui.Handler;
import com.teamfour.monopolish.gui.views.game.GameControllerDrawFx;
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

/**
 * Controller class for chat GUI. It will handle all logic related to chat communication with the database, refreshing, notifications and updating the chat.
 */
public class ChatController {
    // chat elements
    private @FXML Pane chatContainer;
    private @FXML Pane chatMessagesContainer;
    private @FXML ScrollPane chatMessageScrollPane;
    private @FXML TextField chatInput;
    private @FXML Pane chatWarning;
    private @FXML Circle unreadContainer;
    private @FXML Text unreadValue;

    private int current_msg_count = 0;
    private boolean chatOpen = false;

    // Timer that will periodically update the chat
    private static Timer chatTimer = new Timer();

    /**
     * Will run once on mount/render
     */
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

                    notificationCheck();
                });
            }
        };

        chatTimer = new Timer();
        long delay = 1000L; // Delay before update refreshTimer starts
        long period = 1000L; // Delay between each update/refresh
        chatTimer.scheduleAtFixedRate(task, delay, period);

         int CHAT_MAX_CHARACTERS = 40;
         FxUtils.limitInputLength(CHAT_MAX_CHARACTERS, chatInput, chatWarning);
    }

    /**
     * Check if notifications should be rendered
     */
    private void notificationCheck() {
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

    /**
     * Get chat timer to be able to stop timer if needed.
     * @return Instance of chat timer
     */
    public static Timer getChatTimer() {
        return chatTimer;
    }
}
