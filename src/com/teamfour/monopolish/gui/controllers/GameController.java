package com.teamfour.monopolish.gui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Random;

/**
 * Controller class for game view
 *
 * @author Bård Hestmark
 * @version 1.5
 */

public class GameController {

    private ArrayList<Text> eventList = new ArrayList<>();
    private ArrayList<FxPlayer> playerList = new ArrayList<>(); //hentes fra et annet sted, lobby?
    //@FXML private Label p1name, p1money, p2name, p2money, p3name, p3money;
    @FXML
    private GridPane gamegrid;
    @FXML
    private ListView eventlog;

    @FXML
    public void initialize() {
        playerList.add(new FxPlayer(FxPlayer.getMAX(), FxPlayer.getMAX()));
        playerList.add(new FxPlayer(FxPlayer.getMAX(), FxPlayer.getMAX()));
        playerList.add(new FxPlayer(FxPlayer.getMAX(), FxPlayer.getMAX()));
        playerList.add(new FxPlayer(FxPlayer.getMAX(), FxPlayer.getMAX()));

        drawPlayers();
    }

    public void moveffs() {
        Random rand = new Random();
        movePlayer(playerList.get(0), rand.nextInt(11)+1);
    }

    public void moveffs2() {
        Random rand = new Random();
        movePlayer(playerList.get(1), rand.nextInt(11)+1);
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

    private void addToEventlog(String s) {
        eventList.add(new Text(s));

        int focus = eventList.size();
        eventlog.getItems().clear();
        eventlog.getItems().addAll(eventList);
        eventlog.scrollTo(focus);
    }
}