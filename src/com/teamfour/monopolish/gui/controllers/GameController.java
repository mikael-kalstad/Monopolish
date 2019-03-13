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

    @FXML private Button rolldice, next;
    @FXML private GridPane gamegrid;
    @FXML private ListView playerinfo;

    ArrayList<Text> textList = new ArrayList<>();
    ArrayList<Draw> spillere = new ArrayList<>();

    @FXML
    private void ifRollDice(){ //testmetode
        Random rand = new Random();
        rolldice.setOnAction(e -> System.out.println(rand.nextInt(6)));
    }

    @FXML //testmetode
    private void ifNext(){
        next.setOnAction(e -> System.out.println("Nei"));
    }

    @FXML
    private void addToPlayerinfo(){
        textList.add(new Text("hallo\n")); //denne kan/burde ligge i egen metode som kaller denne metoden, gadd ikke akk nå
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

    public void lagspiller(){
        Draw spiller = new Draw();

        spillere.add(spiller);
        GridPane.setConstraints(spiller, 0, 5);

        gamegrid.getChildren().clear();
        gamegrid.getChildren().addAll(spillere);
    }

    public void flyttSpillerXY(int spillernr, int posX, int posY) {
        Draw spiller = new Draw();

        spillere.remove(spillernr);
        spillere.add(spillernr, spiller);

        GridPane.setConstraints(spiller, posX,posY);

        gamegrid.getChildren().clear();
        gamegrid.getChildren().addAll(spillere);
    }
}