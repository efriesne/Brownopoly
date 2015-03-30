package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class MonetarySquare extends BoardSquare {
  private int moneyChange;
  public MonetarySquare(int id, int moneyChange) {
    super(id);
    this.moneyChange = moneyChange;
  }

  @Override
  public int executeEffect(Player p) {
    p.changeMoney(moneyChange);
    return 0;
  }

}
