package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/31/15.
 */
public class GoToJail implements Card {
    private String name = "Go to Jail";

    @Override
    public String play(Player player) {
        player.moveToJail();
        return " was sent to jail!";
    }

    @Override
    public String getName() {
        return name;
    }
}
