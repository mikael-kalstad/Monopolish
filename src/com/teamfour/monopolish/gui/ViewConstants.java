package com.teamfour.monopolish.gui;

/**
 * View constants that contain filenames for .fxml files.
 * References to .fxml files should use an enum from this class,
 * to prevent inconsistency in filenames within the application
 *
 * @author Mikael Kalstad
 * @version 1.0
 */
public enum ViewConstants {
    /**
     * Reference to the file location of the .fxml view files
     */
    FILE_PATH("/com/teamfour/monopolish/gui/"),

    /**
     * References to the .fxml view files
     */
    LOGIN("views/login/login.fxml"),
    REGISTER("views/register/register.fxml"),
    DASHBOARD("views/dashboard/dashboard.fxml"),
    LOBBY("views/lobby/lobby.fxml"),
    GAME("views/game/game.fxml"),
    CHAT("components/chat/chat.fxml"),
    FORFEIT("components/forfeit/forfeit.fxml"),
    MESSAGE_POPUP("components/messagePopup/messagePopup.fxml"),
    CHANCE_CARD("components/cards/chanceCard.fxml"),
    SPECIAL_CARD("components/cards/specialCard.fxml"),
    PROPERTY_DIALOG("components/propertyActions/propertyDialog.fxml"),
    SEND_TRADE("components/sendTrade/sendTrade.fxml");

    private String value;

    /**
     * Method that is needed to get the actual value of the enum
     *
     * @return enum value
     */
    public String getValue() {
        return value;
    }

    /**
     * Constructor for the enums.
     *
     * @param value a value the enum holds
     */
    ViewConstants(String value) {
        this.value = value;
    }
}
