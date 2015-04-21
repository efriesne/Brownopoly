package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/27/15.
 */
public class MonetaryCard implements Card {
    private String name;
    private int amount;

    public MonetaryCard(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    @Override
    public String play(Player player) {
        player.addToBalance(amount);
        if(amount < 0) {
            int posAmount = (amount * -1);
            Board.freeParking += posAmount;
            return " paid " + posAmount + " dollars!";
        } else {
            return " collected " + amount + " dollars!";
        }
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String getName() {
        return name;
    }
}
