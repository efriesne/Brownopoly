package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/31/15.
 */
public class NearestRailroad implements Card {
    private String name = "Advance to Nearest Railroad";

    @Override
    public void play(Player player) {
        int position = player.getPosition();
        int distance1 = (5 - position) % 40;
        int distance2 = (15 - position) % 40;
        int distance3 = (25 - position) % 40;
        int distance4 = (35 - position) % 40;
        if(distance1 <= 10) {
            player.move(distance1);
        } else if(distance2 <= 10) {
            player.move(distance2);
        } else if(distance3 <= 10) {
            player.move(distance3);
        } else {
            player.move(distance4);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
