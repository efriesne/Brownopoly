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
    public void play(Player player) {
        List<Player> opponents = player.getOpponents();
        for(Player opponent : opponents) {
            opponent.setBalance(opponent.getBalance() + amountPerPlayer);
            player.setBalance(player.getBalance() - amountPerPlayer);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}