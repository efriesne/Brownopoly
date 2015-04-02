package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class TaxSquare extends BoardSquare {
  private double tax;
  public TaxSquare(int id, double tax) {
    super(id);
    this.tax = tax;
  }

  @Override
  public String executeEffect(Player p) {
    int bankrevenue = p.payTax(tax);
    Board.freeparking += bankrevenue;
    return p.getName() + " paid " + bankrevenue + " to the bank.";
  }
}
