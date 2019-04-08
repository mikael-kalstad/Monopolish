// TEST CONCEPT DOES NOT WORK!

//package com.teamfour.monopolish.gui.controllers;
//
//import com.teamfour.monopolish.game.Board;
//import com.teamfour.monopolish.game.Game;
//import javafx.scene.control.Button;
//import javafx.scene.layout.Pane;
//
///**
// * Controller class for handling GUI and game logic actions with cards on a specific position
// */
//public class CardController extends GameController {
//    // Button msg constants
//    private final String BTN_BAIL_MSG = "Pay bail";
//    private final String BTN_BUY_MSG = "Buy property";
//    private final String BTN_RENT_MSG = "Pay rent";
//    private final String BTN_TAX_MSG = "Pay tax";
//
//    // Button color constants
//    private final String BTN_BAIL_COLOR = "green";
//    private final String BTN_BUY_COLOR = "green";
//    private final String BTN_RENT_COLOR = "orange";
//    private final String BTN_TAX_COLOR = "red";
//
//    // Card message constants
//    private final String CARD_MSG_OWNNED = "Property owned by";
//
//    // Instance of game
//    private final Game game = Handler.getCurrentGame();
//    private final String USERNAME = Handler.getAccount().getUsername();
//
//    /**
//     * Handle all GUI and game logic changes when landing on this position
//     */
//    public void showCardOnPosition(int position) {
//        int tileType = Handler.getCurrentGame().getBoard().getTileType(position);
//        Pane card = null;
//
//        // No buttons or card msg is shown by default
//        actionBtn.setVisible(false);
//        cardMsg.setVisible(false);
//
//        switch (tileType) {
//            case Board.PROPERTY:
//                // Get property card
//                card = GameControllerDrawFx.createPropertyCard(game.getEntities().getPropertyAtPosition(position));
//
//                // Check if property has an owner
//                String propertyOwner = game.getEntities().getOwnerAtProperty(position);
//
//                // No owner, buying is optional
//                if (propertyOwner == null || propertyOwner.equals("")) {
//                    showAndChangeBtn(BTN_BUY_MSG, BTN_BUY_COLOR);
//                    actionBtn.setOnMouseClicked();
//                }
//
//                // Owned by user, no action
//                else if (propertyOwner.equals(USERNAME)) showAndChangeCardMsg(CARD_MSG_OWNNED + " you");
//
//                // Owned by other player, rent required
//                else {
//                    showAndChangeBtn(BTN_RENT_MSG, BTN_RENT_COLOR);
//                    disableBtnUntilAction(diceBtn);
//                }
//                break;
//
//            case Board.START:
//                card = GameControllerDrawFx.createSpecialCard("Start", "file:res/gui/SpecialCard/start.png", "You will get $4000 if you land or go past start", "#e2885a");
//                break;
//
//            case Board.COMMUNITY_TAX:
//                break;
//
//            case Board.FREE_PARKING:
//                break;
//
//            case Board.JAIL:
//                break;
//
//            case Board.GO_TO_JAIL:
//                break;
//
//            case Board.CHANCE:
//                break;
//        }
//
//        // Show card in container if defined
//        if (card != null)
//            cardContainer.getChildren().add(card);
//    }
//
//    /**
//     * Helper method for showing and changing the action button
//     */
//    private static void showAndChangeBtn(String msg, String color) {
//        super.actionBtn.setVisible(true);
//        super.actionBtn.setText(msg);
//        super.actionBtn.setStyle("-fx-background-color: " + color + ";");
//    }
//
//    /**
//     * Helper method for showing and hiding the card msg
//     * @param msg
//     */
//    private static void showAndChangeCardMsg(String msg) {
//        super.cardMsg.setVisible(true);
//        super.cardMsg.setText(msg);
//    }
//
//    /**
//     * Disable button until actionBtn is clicked
//     * @param btn Target button to be disabled until action
//     */
//    private static void disableBtnUntilAction(Button btn) {
//        btn.setDisable(true);
//
//        super.actionBtn.setOnMouseClicked(e -> btn.setDisable(false));
//    }
//}
