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
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameController {

    @FXML private Button rolldice;
    @FXML private Button next;
    @FXML private Circle player1;
    @FXML private GridPane gamegrid;
    @FXML private ListView playerinfo; //TextFlow playerinfo må ha en tabell eller en annen løsning som gjør at den ikke overfylles
    ArrayList<Text> textList = new ArrayList<>();

    @FXML
    private void ifRollDice(){ //testmetode
        Random rand = new Random();
        rolldice.setOnAction(e -> System.out.println(rand.nextInt(6)));
    }

    @FXML //testmetode
    private void ifNext(){
        next.setOnAction(e -> System.out.println("Nei"));
    }

    /*
    testmetode, kan tydeligvis bare lage metodene her uten å vise til knapper el.
    og så velge riktig metode til riktig knapp i scene builder. Metoden over viser en
    annen måte å gjøre det på, men denne måten ser ut til å være enklere, da man ikke må
    hente knappen inn i kontrolleren. */
    @FXML
    private void addToPlayerinfo(){
        textList.add(new Text("hallo\n")); //denne kan/burde ligge i egen metode som kaller denne metoden, gadd ikke akk nå
        int focus = textList.size();

        playerinfo.getItems().clear();
        playerinfo.getItems().addAll(textList);
        playerinfo.scrollTo(focus);
    }

    /*
    kan hende vi må finne en annen måte å løse denne metoden på,
    denne måten instansierer scenen på nytt hver gang den kjøres: */
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

    @FXML public void moveplayer(){

    }

    public Node getPlayerPosition(int col, int row) {
        for (Node node : gamegrid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    public void setGamegrid(){
        gamegrid.getChildren().addAll(Firkant.lagGater());
    }
}