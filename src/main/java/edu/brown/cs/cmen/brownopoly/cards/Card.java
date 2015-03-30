package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

public interface Card {
    public void play(Player player);
    public String getName();
}