package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/31/15.
 */
public class GoBack3Spaces implements Card {
  /**
   * 
   */
  private static final long serialVersionUID = -8010727916590825642L;
  private String name = "Go Back 3 Spaces";

  @Override
  public String play(Player player) {
    player.move(-3);
    return "";
  }

  @Override
  public String getName() {
    return name;
  }
}
