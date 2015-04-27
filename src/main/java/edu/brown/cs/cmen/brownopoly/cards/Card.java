package edu.brown.cs.cmen.brownopoly.cards;

import java.io.Serializable;

import edu.brown.cs.cmen.brownopoly.player.Player;

public interface Card extends Serializable {
  public String play(Player player);

  public String getName();
}
