package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/31/15.
 */
public class GoToJail implements Card {
    private String name = "Go to Jail";

    @Override
    public void play(Player player) {
        player.moveToJail();
    }

    @Override
    public String getName() {
        return name;
    }
}
