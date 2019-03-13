package com.teamfour.monopolish.gui.controllers;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Draw extends StackPane {

    public Draw(){
        Circle spiller = new Circle(10);
        spiller.setFill(Color.BLUE);
        spiller.setStroke(Color.BLUE);

        setAlignment(Pos.CENTER);
        getChildren().addAll(spiller);
    }

}
