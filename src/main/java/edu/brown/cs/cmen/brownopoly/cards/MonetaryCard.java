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
    public void play(Player player) {
        player.addToBalance(amount);
        if (amount < 0) {
          Board.freeparking += amount*-1;
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
