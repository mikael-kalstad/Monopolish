package com.teamfour.monopolish.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameController {

    @FXML private Button rolldice;
    @FXML private Button next;
    @FXML private GridPane gamegrid;
    @FXML private ListView playerinfo; //TextFlow playerinfo må ha en tabell eller en annen løsning som gjør at den ikke overfylles
    ArrayList<Text> textList = new ArrayList<>();
    ArrayList<FxPlayer> players = new ArrayList<>();

    FxPlayer FXPlayer = new FxPlayer(9,9); //for testing purposes

    @FXML
    private void rollDice(){
        Random rand = new Random();
        rolldice.setOnAction(e -> System.out.println(rand.nextInt(6)));
    }

    @FXML //testmetode
    private void ifNext(){
        next.setOnAction(e -> System.out.println("Nei"));
    }

    private void addToPlayerinfo(String s){
        textList.add(new Text(s));

        int focus = textList.size();
        playerinfo.getItems().clear();
        playerinfo.getItems().addAll(textList);
        playerinfo.scrollTo(focus);
    }

    public void openLoginScene(ActionEvent e) throws IOException {
        Parent login = FXMLLoader.load(getClass().getResource("LoginFXML.fxml"));
        Scene newScene = new Scene(login);

        Stage nyttVindu = (Stage) ((Node)e.getSource()).getScene().getWindow();

        nyttVindu.setScene(newScene);
        nyttVindu.show();
    }

    //bare et eksempel for å teste varselbokser:
    public void aWarning(){
        AlertBox.display(
                Alert.AlertType.WARNING, "Ikke faen", "Whoops", "You're not even on the bård, how can you pay rent?");
    }

    @FXML
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

    @FXML
    public void move1(){
        movePlayer(1);
    }
}