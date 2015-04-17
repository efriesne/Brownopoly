package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

import java.util.List;

/**
 * Created by codyyu on 3/31/15.
 */
public class EveryPlayerCard implements Card {
    private String name;
    private int amountPerPlayer;
    public EveryPlayerCard(String name, int amountPerPlayer) {
        this.name = name;
        this.amountPerPlayer = amountPerPlayer;
    }
    @Override
    public String play(Player player) {
        List<Player> opponents = player.getOpponents();
        for(Player opponent : opponents) {
            opponent.addToBalance(amountPerPlayer);
            player.addToBalance(-1 * amountPerPlayer);
        }
        if(amountPerPlayer < 0) {
            return " paid " + (-1 * amountPerPlayer) + " dollars to each player!";
        } else {
            return " collected " + amountPerPlayer + " dollars from each player!";
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
