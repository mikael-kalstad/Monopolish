package com.teamfour.monopolish.game.chanceCards;

import java.util.Random;

public class ChanceCardData {
    private static ChanceCard[] data = new ChanceCard[]{
            new ChanceCardBank("You owe the bank $1000, pay up boy!", "file:res/gui/MessagePopup/dollarNegative.png", 1000),
            new ChanceCardBank("The bank owes you $500, get rich bitch!", "file:res/gui/MessagePopup/dollarPositie", 500)
    };

    public static ChanceCard getRandomChanceCard() {
        Random random = new Random();
        return data[random.nextInt(data.length-1)];
    }
}
