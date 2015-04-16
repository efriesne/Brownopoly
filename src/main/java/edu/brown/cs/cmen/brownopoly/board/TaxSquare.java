package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class TaxSquare extends BoardSquare {
  private double tax;
  public TaxSquare(String name, int id, double tax) {
    super(name, id);
    this.tax = tax;
  }

  @Override
  public String executeEffect(Player p) {
    int bankRevenue = p.payTax(tax);
    Board.freeParking += bankRevenue;
    return p.getName() + " paid " + bankRevenue + " to the bank.";
  }
}
