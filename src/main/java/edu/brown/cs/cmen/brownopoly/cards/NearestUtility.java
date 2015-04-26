package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/31/15.
 */
public class NearestUtility implements Card {
    private String name = "Advance to Nearest Utility";

    @Override
    public String play(Player player) {
        int distance1 = (28 - player.getPosition() + 40) % 40;
        int distance2 = (12 - player.getPosition() + 40) % 40;
        if(distance1 < distance2) {
            player.move(distance1);
            //return ":\nMove forward " + distance1 + " spaces!";
        } else {
            player.move(distance2);
            //return ":\nMove forward " + distance2 + " spaces!";
        }
        return "";
    }

    @Override
    public String getName() {
        return name;
    }
}
