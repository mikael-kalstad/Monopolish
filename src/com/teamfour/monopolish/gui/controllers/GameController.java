package com.teamfour.monopolish.gui.controllers;

import com.teamfour.monopolish.game.entities.player.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
/**
 * Controller class for game view
 *
 * @author Bård Hestmark
 * @version 1.5
 */

public class GameController {

    @FXML private GridPane gamegrid;
    @FXML private ListView eventlog;
    @FXML private Label p1name, p1money, p2name, p2money, p3name, p3money;
    ArrayList<Text> eventList = new ArrayList<>();
    ArrayList<FxPlayer> players = new ArrayList<>();

    FxPlayer FXPlayer = new FxPlayer(9,9); //for testing purposes

    private void addToEventlog(String s){
        eventList.add(new Text(s));

        int focus = eventList.size();
        eventlog.getItems().clear();
        eventlog.getItems().addAll(eventList);
        eventlog.scrollTo(focus);
    }

    public void drawplayer() throws Exception{
        for(FxPlayer player : players) {
            if (player == FXPlayer){
                throw new Exception("Already drawn");
            }
        }   
        players.add(FXPlayer);
        GridPane.setConstraints(FXPlayer, FXPlayer.getPosX(), FXPlayer.getPosY());

        gamegrid.getChildren().clear();
        gamegrid.getChildren().addAll(players);
    }

    public void movePlayer(int steps) {
        FXPlayer.move(steps);

        GridPane.setConstraints(FXPlayer, FXPlayer.getPosX(), FXPlayer.getPosY());

        gamegrid.getChildren().clear();
        gamegrid.getChildren().addAll(players);
    }

    public void showDice(){
        FxPlayer terning = new FxPlayer(3,3);
        GridPane.setConstraints(terning, terning.getPosX(), terning.getPosY());
        players.add(terning);
        gamegrid.getChildren().clear();
        gamegrid.getChildren().add(terning);

    }

    public void playerInfo(){

    }

    public void move1(){
        movePlayer(1);
        addToEventlog(FXPlayer.toString());
    }
}