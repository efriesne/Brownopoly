package edu.brown.cs.cmen.brownopoly.cards;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/31/15.
 */
public class EveryPlayerCard implements Card {
  /**
   * 
   */
  private static final long serialVersionUID = 1966083861877150376L;
  private String name;
  private int amountPerPlayer;

  public EveryPlayerCard(String name, int amountPerPlayer) {
    this.name = name;
    this.amountPerPlayer = amountPerPlayer;
  }

  @Override
  public String play(Player player) {
    List<Player> opponents = player.getOpponents();
    for (Player opponent : opponents) {
      opponent.addToBalance(amountPerPlayer);
      player.addToBalance(-1 * amountPerPlayer);
    }
    if (amountPerPlayer >= 0) {
      return ":\nPay $" + amountPerPlayer + " to each player!";
    } else {
      return ":\nCollect $" + -1 * (amountPerPlayer) + " from each player!";
    }
  }

  @Override
  public String getName() {
    return name;
  }
}
