package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/31/15.
 */
public class GetOutOfJailFree implements Card {
    private String name = "Get Out of Jail Free";

    @Override
    public String play(Player player) {
        player.addJailFree();
        return "";
    }

    @Override
    public String getName() {
        return name;
    }
}
