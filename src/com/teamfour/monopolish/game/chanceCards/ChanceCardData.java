package com.teamfour.monopolish.game.chanceCards;

import java.util.Random;

public class ChanceCardData {
    private static ChanceCard[] data = new ChanceCard[]{
            new ChanceCardBank("The bank does not like you, they demand $1200.", "file:res/gui/MessagePopup/dollarNegative.png", 1200),
            new ChanceCardBank("You received $500 and roses from a secret lover <3.", "file:res/gui/ChanceCard/rose.png", 500),
            new ChanceCardBank("Studded tires are not allowed in central Trondheim, pay 2500 in fines.", "file:res/gui/MessagePopup/dollarPositive.png", 2500),
            new ChanceCardBank("On your way to work you stumbled upon a suit case with $10.000!", "file:res/gui/ChanceCard/suitcase.png", 10000),
            new ChanceCardBank("On your trip to Mexico you ate a delicious sandwich at the airport, too bad it cost $3500", "file:res/gui/ChanceCard/sandwich.png", 3500),
//            new ChanceCardBank("", "file:res/gui/ChanceCard/sandwich.png", 3500),
    };

    public static ChanceCard getRandomChanceCard() {
        Random random = new Random();
        return data[random.nextInt(data.length-1)];
    }
}
