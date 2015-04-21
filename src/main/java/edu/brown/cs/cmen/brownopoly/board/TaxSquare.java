package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class TaxSquare extends BoardSquare {
  private double tax;
  public TaxSquare(int id, String name, double tax) {
    super(name, id);
    this.tax = tax;
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    int bankRevenue = p.payTax(tax);
    Board.freeParking += bankRevenue;
    return p.getName() + " paid " + bankRevenue + " to the bank.";
  }

  @Override
  public int getInput() {
    return 0;
  }
}
