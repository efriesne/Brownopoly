package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/27/15.
 */
public class PositionCard implements Card {
    private String name;
    private int destination;

    public PositionCard(String name, int destination) {
        this.name = name;
        this.destination = destination;
    }

    @Override
    public void play(Player player) {
        player.move((destination - player.getPosition()) % 40);
    }

    @Override
    public String getName() {
        return name;
    }
    public int getDestination() {
        return destination;
    }
}
