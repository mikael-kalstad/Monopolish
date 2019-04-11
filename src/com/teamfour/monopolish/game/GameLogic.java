package com.teamfour.monopolish.game;

import com.teamfour.monopolish.game.entities.EntityManager;
import com.teamfour.monopolish.game.entities.Player;
import com.teamfour.monopolish.game.gamecomponents.Board;
import com.teamfour.monopolish.game.property.*;
import com.teamfour.monopolish.gui.controllers.Handler;
import com.teamfour.monopolish.gui.controllers.MessagePopupController;

import java.sql.SQLException;

/**
 * Contains static methods which can be used by the GameController to perform logical actions
 * and sequences in the game. All actions are performed on the client player. This class was implemented
 * to streamline and create a better overview of all logical operations performed on the instances of
 * 'Game.java', completely excluding such operations from GameController.java'.
 *
 * @author      eirikhem
 * @version     1.3
 */

public class GameLogic {

    public static Game game = Handler.getCurrentGame();

    /**
     * Initalizes the current game. Creates all objects, and prepares the database for a game
     */
    public static void startGame() {
        try {
            // Get the current game from the handler class
            int gameId = game.getGameId();
            System.out.println("Game id: " + gameId);

            // Initialize board and get players from database
            game.setBoard(new Board());
            EntityManager entities = new EntityManager(gameId);
            entities.updateFromDatabase();
            game.setEntities(entities);

            // Transfer money from bank to players
            entities.distributeStartMoney(GameConstants.START_MONEY);

            // Get a list of players and their turn order
            String[] players = entities.getUsernames();
            game.setPlayers(players);

            // Write current player and money amounts to database
            Handler.getGameDAO().setCurrentPlayer(gameId, players[0]);
            updateToDatabase();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to buy a house for the specified street
     * @param street Street to buy house to
     * @return True if enough money and successful
     */
    public static boolean buyHouse(Street street) {
        // Get the house price
        int housePrice = Integer.parseInt(street.getAllRent()[7]);
        Player yourPlayer = game.getEntities().getYou();
        // If no more available houses in bank, return
        if (game.getEntities().getBank().getAvailableHouses() == 0)
            return false;

        // If the player can't afford house, return
        if (yourPlayer.getMoney() < housePrice)
            return false;

        // Make transaction
        game.getEntities().transferMoneyFromBank(yourPlayer.getUsername(), -housePrice);
        game.getEntities().getBank().getHouses(1);
        street.addHouse();
        return true;
    }

    /**
     * Attempts to buy a hotel for the specified street
     * @param street Street to buy hotel to
     * @return True if enough money and successful
     */
    public static boolean buyHotel(Street street) {
        // Get hotel price
        int hotelPrice = Integer.parseInt(street.getAllRent()[7]);
        Player yourPlayer = game.getEntities().getYou();
        // If no more available hotels, return
        if (game.getEntities().getBank().getAvailableHotels() == 0)
            return false;

        // If player can't afford, return
        if (yourPlayer.getMoney() < hotelPrice)
            return false;

        // Make transaction
        game.getEntities().transferMoneyFromBank(yourPlayer.getUsername(), -hotelPrice);
        game.getEntities().getBank().getHotels(1);
        street.addHotel();
        return true;
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
            MessagePopupController.show("The dice are equal, throw again!", "again.png", "Game");
        } else {
            // If normal dice and not in jail, move
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
        // Set your jail status to true
        yourPlayer.setInJail(true);
        // Move to actual jail position
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
        // If you can afford it, get out of jail
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
            MessagePopupController.show("Purchase successful, you are now the owner of " + propertyToPurchase.getName(), "house.png", "Real estate");
            updateToDatabase();
        }

        return result;
    }

    /**
     * Makes the player pay rent to the owner of the current property. If the player doesn't have enough money,
     * pay the last of their funds
     */
    public static boolean payRent() {
        EntityManager entities = game.getEntities();
        Player yourPlayer = entities.getYou();

        // Check if your player has a free parking token, if so return immediately
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
            // Rent calculation varies for what type of property
            if (currentPropertyType == Property.STREET) {
                // If street, we calculate rent based on how many houses and hotels, along with if it's a full set
                boolean ownerHasFullSet = entities.getPlayer(owner).hasFullSet(game.getGameId(),
                        currentProperty.getCategorycolor());
                price = ((Street) currentProperty).getCurrentRent(ownerHasFullSet);
            } else if (currentPropertyType == Property.BOAT) {
                // If it's a boat, rent is based on the number of boats the owner has
                int numberOfBoats = entities.getPlayer(owner).getBoatsOwned();
                price = ((Boat) currentProperty).getRent(numberOfBoats - 1);
            } else {
                // If it's a train, rent is based on your dice throw
                int numberOfTrains = entities.getPlayer(owner).getTrainsOwned();
                int[] lastThrow = game.getDice().getLastThrow();
                price = ((Train) currentProperty).getRent(numberOfTrains - 1, lastThrow[0] + lastThrow[1]);
            }

            // Run transaction
            if (yourPlayer.getMoney() < price) {
                // Need to see if bankrupt to potentially declare them a loser
                checkBankruptcy();
                return false;
            }

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

        return true;
    }

    /**
     * Makes the player pay tax to the bank
     */
    public static boolean payTax() {
        Player yourPlayer = game.getEntities().getYou();
        if (yourPlayer.getMoney() < GameConstants.INCOME_TAX) {
            // Check bankruptcy to potentially declare player a loser
            checkBankruptcy();
            return false;
        }
        game.getEntities().transferMoneyFromBank(game.getEntities().getYou().getUsername(), -GameConstants.INCOME_TAX);
        updateToDatabase();
        return true;
    }

    /**
     * Update all game elements to the database
     */
    public static void updateToDatabase() {
        try {
            // Always check bankruptcy before updating to database, so we can catch this as soon as possible
            checkBankruptcy();
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
        checkBankruptcy();
        // Get the new current player from database
        String newCurrentPlayer = game.getPlayers()[game.getCurrentTurn()];
        if (!currentPlayer.equals(newCurrentPlayer)) {
            // If new turn, and turn number is 0, we know that it's a new round
            if (game.getCurrentTurn() == 0) game.incrementRound();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if your player is bankrupt, if so, set bankrupt and show a message.
     */
    public static void checkBankruptcy() {
        Player yourPlayer = game.getEntities().getYou();
        if (!yourPlayer.isBankrupt() && yourPlayer.checkBankrupt()) {
            yourPlayer.setBankrupt(true);
            MessagePopupController.show("You are now bankrupt!", "bankrupt.png", "Bank");
        }
    }

    /**
     * Pawns the specified property, and pays the player half of the property's worth
     * @param property
     */
    public static boolean pawnProperty(Property property) {
        if (property.isPawned())
            return false;

        property.setPawned(true);
        // When pawning a property, you get half the asking price of the property
        int price = property.getPrice() / 2;
        game.getEntities().transferMoneyFromBank(game.getEntities().getYou().getUsername(), price);
        updateToDatabase();
        return true;
    }

    /**
     * Unpawns the property for a price
     * @param property Property to unpawn
     * @return If player had enough money to complete the transaction
     */
    public static boolean unpawnProperty(Property property) {
        if (!property.isPawned())
            return false;

        Player yourPlayer = game.getEntities().getYou();
        int price = property.getPrice() / 2;
        if (yourPlayer.getMoney() < price)
            return false;

        property.setPawned(false);
        game.getEntities().transferMoneyFromBank(yourPlayer.getUsername(), -price);
        updateToDatabase();
        return true;
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
     * @return Winner username
     */
    public static String stopGame() {
        // End game in database
        String[] players = game.getPlayers();
        for(int i = 0; i<players.length; i++){
            Handler.getPlayerDAO().endGame(game.getGameId(), players[i]);
        }
        Handler.getGameDAO().finishGame(game.getGameId());

        // Delete lobby
        int lobbyId = Handler.getLobbyDAO().getLobbyId(Handler.getAccount().getUsername());
        Handler.getLobbyDAO().deleteLobby(lobbyId);

        return Handler.getGameDAO().getWinner(game.getGameId());
    }

    /**
     * Update all elements in the game from the database, so that changes that other players have
     * done gets shown in your own client
     */
    public static void updateFromDatabase() {
        try {
            // Update entities
            game.getEntities().updateFromDatabase();

            // Load player list again, in case anyone has left
            game.setPlayers(game.getEntities().getUsernames());

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

    /**
     * Called whenever you leave the game
     */
    public static void onPlayerLeave() {
        Player yourPlayer = game.getEntities().getYou();
        String yourUsername = yourPlayer.getUsername();
        // If it's your turn, end your own turn
        if (game.getCurrentTurn() + 1 > game.getPlayers().length) {
            game.setCurrentTurn(game.getPlayers().length - 1);
        }
        if (game.getPlayers()[game.getCurrentTurn()].equals(yourUsername)) {
            endTurn();
        }

        // Remove the player from the lobby and set their status to left
        Handler.getLobbyDAO().removePlayer(yourUsername, Handler.getLobbyDAO().getLobbyId(yourUsername));
        Handler.getPlayerDAO().endGame(game.getGameId(), yourUsername);
        updateToDatabase();
    }
}