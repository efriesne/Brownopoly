package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class MonetarySquare extends BoardSquare {
  private int moneyChange;
  public MonetarySquare(int id, int moneyChange) {
    super(id);
    this.moneyChange = moneyChange;
  }

  @Override
  public String executeEffect(Player p) {
    p.setBalance(moneyChange);
    String change;
    if (moneyChange < 0) {
      change = "paid";
      moneyChange *= -1;
    } else {
      change = "received";
    }
    return p.getName() + " " + change + " " + moneyChange + ".";
  }

}
