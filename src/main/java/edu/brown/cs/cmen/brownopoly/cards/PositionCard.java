package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/27/15.
 */
public class PositionCard implements Card {
  /**
   * 
   */
  private static final long serialVersionUID = 829941547429567346L;
  private String name;
  private int destination;

  public PositionCard(String name, int destination) {
    this.name = name;
    this.destination = destination;
  }

  @Override
  public String play(Player player) {
    int distance = (destination - player.getPosition() + 40) % 40;
    player.move(distance);
    return "";
    // return ":\nMove forward " + distance + " spaces!";
  }

  @Override
  public String getName() {
    return name;
  }

  public int getDestination() {
    return destination;
  }
}
