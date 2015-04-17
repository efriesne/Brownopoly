package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

public interface Card {
    public String play(Player player);
    public String getName();
}