package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.GameLogic;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Controller class for game view
 *
 * @author Bård Hestmark
 * @version 1.5
 */

public class GameController {

    private GameLogic gameLogic = new GameLogic(1);
    private ArrayList<Text> eventList = new ArrayList<>();
    private ArrayList<FxPlayer> playerList = new ArrayList<>(); //hentes fra et annet sted, lobby?
    //@FXML private Label p1name, p1money, p2name, p2money, p3name, p3money;
    @FXML
    private Button rolldice;
    @FXML
    private TextFlow propertycard;
    @FXML
    private GridPane gamegrid;
    @FXML
    private ListView eventlog;

    @FXML
    public void initialize() {
        // Load gamelogic and initialize the game setup
        try {
            gameLogic.setupGame();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Draw players based on the number of players
        String[] playerTurns = gameLogic.getTurns();
        for (int i = 0; i < playerTurns.length; i++) {
            playerList.add(new FxPlayer(playerTurns[i], FxPlayer.getMAX(), FxPlayer.getMAX()));
        }

        drawPlayers();
    }

    private void drawDice(){

    }

    public void setRolldice(){
        int[] dice = null;
        try {
            dice = gameLogic.throwDice();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int dice1 = dice[0];
        int dice2 = dice[1];
        String s = "Threw dice:  "+ dice1 + ",  " + dice2;
        addToEventlog(s);
        movePlayer(playerList.get(0), dice1+dice2);
    }

    public void drawPlayers() {

        for (FxPlayer player : playerList) {
            GridPane.setConstraints(player, player.getPosX(), player.getPosY());
        }
        checkForOthers(playerList.get(0));

        gamegrid.getChildren().clear();
        gamegrid.getChildren().addAll(playerList);
    }

    public void movePlayer(FxPlayer player, int steps) {
        player.move(steps);
        player.setAlignment(Pos.CENTER);
        GridPane.setConstraints(player, player.getPosX(), player.getPosY());

        gamegrid.getChildren().clear();
        gamegrid.getChildren().addAll(playerList);

        checkForOthers(player);

        String pos = "moved to X: " + player.getPosX() + " Y:" + player.getPosY();
        addToEventlog(pos);
    }

    private void checkForOthers(FxPlayer player) {

        ArrayList<FxPlayer> checklist = new ArrayList<>();

        for (FxPlayer p : playerList) {
            if ((p.getPosX() == player.getPosX()) && (p.getPosY() == player.getPosY())) {
                checklist.add(p);
            }
        }

        if (checklist.size() > 1) {
            if (checklist.size() == 2) {
                checklist.get(0).setAlignment(Pos.CENTER_LEFT);
                checklist.get(1).setAlignment(Pos.CENTER_RIGHT);
            }
            if (checklist.size() == 3) {
                checklist.get(0).setAlignment(Pos.CENTER_LEFT);
                checklist.get(1).setAlignment(Pos.CENTER_RIGHT);
                checklist.get(2).setAlignment(Pos.BOTTOM_CENTER);
            }
            if (checklist.size() == 4) {
                checklist.get(0).setAlignment(Pos.CENTER_LEFT);
                checklist.get(1).setAlignment(Pos.CENTER_RIGHT);
                checklist.get(2).setAlignment(Pos.BOTTOM_LEFT);
                checklist.get(3).setAlignment(Pos.BOTTOM_RIGHT);
            }
        }
    }

    public void addToEventlog(String s) {
        eventList.add(new Text(s));

        int focus = eventList.size();
        eventlog.getItems().clear();
        eventlog.getItems().addAll(eventList);
        eventlog.scrollTo(focus);
    }
}