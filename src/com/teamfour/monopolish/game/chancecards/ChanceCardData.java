package com.teamfour.monopolish.game.chancecards;

import java.util.Random;

public class ChanceCardData {
    private static ChanceCard[] data = new ChanceCard[]{
            new ChanceCardBank("The bank does not like you, they demand $1200.", "file:res/gui/MessagePopup/dollarNegative.png", -1200),
            new ChanceCardBank("You received $500 and roses from a secret lover.", "file:res/gui/ChanceCard/rose.png", 500),
            new ChanceCardBank("Studded tires are not allowed in central Trondheim, pay 500 in fines.", "file:res/gui/MessagePopup/dollarNegative.png", -500),
            new ChanceCardBank("On your way to work you stumbled upon a suit case with $3.000!", "file:res/gui/ChanceCard/suitcase.png", 3000),
            new ChanceCardBank("On your trip to Mexico you ate a delicious sandwich at the airport, too bad it cost $200", "file:res/gui/ChanceCard/sandwich.png", -200),
            new ChanceCardBank("You received money on your tax return, $1800", "file:res/gui/MessagePopup/dollarPositive.png", 1800),
            new ChanceCardBank("You owe money on your tax return, $1300", "file:res/gui/MessagePopup/dollarNegative.png", -1300),
            new ChanceCardBank("Congratulations, you have won the lottery! Maybe it is time to buy that Yacht? Maybe not... 333.373 other people also won the lottery, total reward comes at $15.", "file:res/gui/MessagePopup/dollarPositive.png", 15),
            new ChanceCardBank("'Microsoft' called to warn you about some awful virus on your computer, you should have know something was up with that accent. Total cost $2500.", "file:res/gui/chanceCard/bug.png", -2500),
            new ChanceCardPosition("One of the most wonderful things in life is to wake up and enjoy a cuddle with somebody; unless you are in prison. To bad you are going to prison.", "file:res/gui/MessagePopup/handcuffs.png", 9),
            new ChanceCardPosition("Go to start!", "file:res/gui/MessagePopup/again.png", 0),
            new ChanceCardPlayers("You did not like your opponents, so you stole $600 from each and everyone.", "file:res/gui/chanceCard/stealing.png", 600),
            new ChanceCardPlayers("You wrecked the cars to the other players, pay everyone $900 for mechanical repair fees.", "file:res/gui/chanceCard/crash.png", -900)
    };

    public static ChanceCard getRandomChanceCard() {
        Random random = new Random();
        return data[random.nextInt(data.length)];
    }
}
