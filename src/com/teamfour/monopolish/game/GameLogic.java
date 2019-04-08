package com.teamfour.monopolish.game;

import com.teamfour.monopolish.game.entities.EntityManager;
import com.teamfour.monopolish.game.entities.player.Player;
import com.teamfour.monopolish.game.property.Boat;
import com.teamfour.monopolish.game.property.Property;
import com.teamfour.monopolish.game.property.Street;
import com.teamfour.monopolish.game.property.Train;
import com.teamfour.monopolish.gui.controllers.Handler;
import com.teamfour.monopolish.gui.controllers.MessagePopupController;

import java.sql.SQLException;

/**
 * This class contains static methods which can be used by the GameController to perform logical actions
 * and sequences in the game. This class was implemented to streamline and create a better overview of all
 * logical operations performed on the instances of 'Game.java', completely excluding such operations from
 * 'GameController.java'
 *
 * @author      eirikhem
 * @version     1.2
 */

public class GameLogic {

    public static Game game = Handler.getCurrentGame();

    /**
     * Initalizes the current game
     */
    public static void startGame() {
        try {
            // Get the current game from the handler class
            int gameId = game.getGameId();
            System.out.println("Game id: " + gameId);

            // Initialize board and get players from database
            game.setBoard(new Board());
            System.out.println("Loading players...");
            EntityManager entities = new EntityManager(gameId);
            entities.updateFromDatabase();
            game.setEntities(entities);

            // Transfer money from bank to players
            System.out.println("Distributing money...");
            entities.distributeStartMoney(GameConstants.START_MONEY);

            // Get a list of players and their turn order
            System.out.println("Getting turn order...");
            String[] players = entities.getUsernames();
            game.setPlayers(players);
            for (int i = 0; i < players.length; i++) {
                System.out.println((i + 1) + ": " + players[i]);
            }

            // 5. Write current player and money amounts to database
            System.out.println("Writing back to database...");
            Handler.getGameDAO().setCurrentPlayer(gameId, players[0]);
            updateToDatabase();

            System.out.println("Setup completed");
        } catch (SQLException e) {

        }
    }

    /**
     * Rolls the player dice and moves the player accordingly. Position, counters and
     * jail stuff is handled here as well
     */
    public static void rollDice() {
        Player yourPlayer = game.getEntities().getYou();
        // Throw dice and store in array
        int[] dice = game.getDice().throwDice();
        int steps = dice[0] + dice[1];
        int previousPosition = yourPlayer.getPosition();

        // Check if player is in prison. If they are in prison, and they get matching dice, move out of jail
        boolean isInJail = yourPlayer.isInJail();
        if (dice[0] == dice[1]) {
            // Add to throw counter if dice are equal
            game.addThrowCounter();
            // If counter reaches 3, put player in jail
            if (game.getThrowCounter() == 3) {
                goToJail();
                game.setThrowCounter(0);
            } else {
                // If not, move, and get out of jail if you're in jail
                if (isInJail)
                    getOutOfJail();
                yourPlayer.move(steps);
            }
            MessagePopupController.show("The dices are equal, throw again!", "again.png", "Game");
        } else {
            if (!isInJail)
                yourPlayer.move(steps);
            game.setThrowCounter(0);
        }

        // Set free parking if on free parking
        if (game.getBoard().getTileType(yourPlayer.getPosition()) == Board.FREE_PARKING) {
            yourPlayer.setFreeParking(true);
        }

        // If the player lands on a goToJail tile, go straight to jail
        if (game.getBoard().getTileType(yourPlayer.getPosition()) == Board.GO_TO_JAIL) {
            goToJail();
        }

        // If the player passed start, give them money
        if (yourPlayer.getPosition() < previousPosition && !yourPlayer.isInJail()) {
            game.getEntities().transferMoneyFromBank(yourPlayer.getUsername(), GameConstants.ROUND_MONEY);
        }

        // Update to database
        updateToDatabase();
    }

    /**
     * Moves the player to jail
     */
    public static void goToJail() {
        Player yourPlayer = game.getEntities().getYou();
        yourPlayer.setInJail(true);
        yourPlayer.moveTo(game.getBoard().getJailPosition());

        MessagePopupController.show("Criminal scumbag! You are going to jail. Your mother is not proud...", "handcuffs.png", "Jail");

        // Update to database
        updateToDatabase();
    }

    /**
     * Moves the player out of jail
     */
    public static void getOutOfJail() {
        Player yourPlayer = game.getEntities().getYou();
        yourPlayer.setInJail(false);

        MessagePopupController.show("You are out of jail, free as a bird!", "bird.png", "Jail");

        // Update to database
        updateToDatabase();
    }

    /**
     * Attempts to pay bail with the player's money
     * @return True if player has enough money
     */
    public static boolean payBail() {
        Player yourPlayer = game.getEntities().getYou();
        if (yourPlayer.getMoney() >= GameConstants.BAIL_COST) {
            game.getEntities().transferMoneyFromBank(yourPlayer.getUsername(), -GameConstants.BAIL_COST);
            getOutOfJail();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempt to buy property
     * @return True if enough money
     */
    public static boolean purchaseProperty() {
        Player yourPlayer = game.getEntities().getYou();
        Property propertyToPurchase = game.getEntities().getPropertyAtPosition(yourPlayer.getPosition());
        boolean result = game.getEntities().purchaseProperty(yourPlayer, propertyToPurchase);

        if (result) {
            MessagePopupController.show("Purchase successful, you are now the owner of " + propertyToPurchase.getName(), null, "Real estate");
            updateToDatabase();
        }

        return result;
    }

    /**
     * Makes the player pay rent to the owner of the current property. If the player doesn't have enough money,
     * pay the last of their funds
     */
    public static void payRent() {
        EntityManager entities = game.getEntities();
        Player yourPlayer = entities.getYou();

        // Check if your player has a free parking token, if so return immediately
        System.out.println("Free parking: " + yourPlayer.hasFreeParking());
        if (yourPlayer.hasFreeParking()) {
            MessagePopupController.show("You have a 'Free Parking' token! You don't have to pay rent here", "parking.png");
            yourPlayer.setFreeParking(false);
        } else {

            // Get position, property object and owner name
            int position = yourPlayer.getPosition();
            Property currentProperty = entities.getPropertyAtPosition(position);
            String owner = currentProperty.getOwner();

            // Get type of property
            int currentPropertyType = currentProperty.getType();
            int price;
            if (currentPropertyType == Property.STREET) {
                boolean ownerHasFullSet = entities.getPlayer(owner).hasFullSet(game.getGameId(),
                        currentProperty.getCategorycolor());
                price = ((Street) currentProperty).getCurrentRent(ownerHasFullSet);
            } else if (currentPropertyType == Property.BOAT) {
                int numberOfBoats = entities.getPlayer(owner).getBoatsOwned();
                price = ((Boat) currentProperty).getRent(numberOfBoats);
            } else {
                int numberOfTrains = entities.getPlayer(owner).getTrainsOwned();
                int[] lastThrow = game.getDice().getLastThrow();
                price = ((Train) currentProperty).getRent(numberOfTrains, lastThrow[0] + lastThrow[1]);
            }

            // Run transaction
            entities.transferMoneyFromTo(yourPlayer.getUsername(), currentProperty.getOwner(), price);

            MessagePopupController.show(
                    "You have paid " +
                            currentProperty.getAllRent()[0] +
                            " in rent to " + currentProperty.getOwner()
                    , "dollarNegative.png"
                    , "Real estate");
        }

        // Finally, update to database
        updateToDatabase();
    }

    /**
     * Makes the player pay tax to the bank
     */
    public static void payTax() {
        game.getEntities().transferMoneyFromBank(game.getEntities().getYou().getUsername(), -GameConstants.INCOME_TAX);
        updateToDatabase();
    }

    /**
     * Update all game elements to the database
     */
    public static void updateToDatabase() {
        try {
            // Updates all entities
            game.getEntities().updateToDatabase();

            // Updates the current player
            Handler.getGameDAO().setCurrentPlayer(game.getGameId(), game.getPlayers()[game.getCurrentTurn()]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks the database and sees if it's become a new turn since the last check
     * @return True if new turn
     */
    public static boolean waitForTurn() {
        // Get the current player
        String currentPlayer = game.getPlayers()[game.getCurrentTurn()];
        updateFromDatabase();
        // Get the new current player from database
        String newCurrentPlayer = game.getPlayers()[game.getCurrentTurn()];
        if (!currentPlayer.equals(newCurrentPlayer)) {
            // If new turn, and turn number is 0, we know that it's a new round
            if (game.getCurrentTurn() == 0) game.incrementRound();
            checkBankruptcy();
            return true;
        } else {
            return false;
        }

        // TODO: Bankruptcy check, game end blabla
    }

    /**
     * Check if your player is bankrupt, if so, set bankrupt and show a message.
     */
    public static void checkBankruptcy() {
        Player yourPlayer = game.getEntities().getYou();
        if (yourPlayer.checkBankrupt()) {
            yourPlayer.setBankrupt(true);
            MessagePopupController.show("You are now bankrupt!", "Bank");
        }
    }

    /**
     * Ends your own turn
     */
    public static void endTurn() {
        game.incrementTurn();
        updateToDatabase();
    }

    /**
     * Will be run when the game is finished or the players choose to forfeit.
     * It will update and clean up the database to make sure scores are saved,
     * and that the game and lobby is deleted to avoid issues when players play again later.
     */
    public static void endGame() {
        // End game in database
        String[] players = game.getPlayers();
        for(int i = 0; i<players.length; i++){
            Handler.getPlayerDAO().endGame(Handler.getCurrentGameId(), players[i]);
        }
        Handler.getGameDAO().finishGame(Handler.getCurrentGameId());

        // Delete lobby
        int lobbyId = Handler.getLobbyDAO().getLobbyId(Handler.getAccount().getUsername());
        Handler.getLobbyDAO().deleteLobby(lobbyId);
    }

    /**
     * Update all elements in the game from the database, so that changes that other players have
     * done gets shown in your own client
     */
    public static void updateFromDatabase() {
        try {
            // Update entities
            game.getEntities().updateFromDatabase();

            // Load player list again
            //game.setPlayers(game.getEntities().getUsernames());

            // Update turn number
            String currentPlayer = Handler.getGameDAO().getCurrentPlayer(game.getGameId());
            String[] players = game.getPlayers();
            for (int i = 0; i < players.length; i++) {
                if (players[i].equals(currentPlayer)) {
                    game.setCurrentTurn(i);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void onPlayerLeave() {
        String yourUsername = game.getEntities().getYou().getUsername();
        // If it's your turn, end your own turn
        if (game.getPlayers()[game.getCurrentTurn()].equals(yourUsername)) {
            endTurn();
        }

        Handler.getLobbyDAO().removePlayer(yourUsername, Handler.getLobbyDAO().getLobbyId(yourUsername));
        game.getEntities().removePlayer(yourUsername);
        updateToDatabase();
    }
}