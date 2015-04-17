package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class TaxSquare extends BoardSquare {
  private double tax;
  public TaxSquare(String name, int id, double tax) {
    super(name, id);
    this.tax = tax;
  }

  @Override
<<<<<<< HEAD
  public String executeEffect(Player p, int userInput) {
    int bankrevenue = p.payTax(tax);
    Board.freeParking += bankrevenue;
    return p.getName() + " paid " + bankrevenue + " to the bank.";
=======
  public String executeEffect(Player p) {
    int bankRevenue = p.payTax(tax);
    Board.freeParking += bankRevenue;
    return p.getName() + " paid " + bankRevenue + " to the bank.";
>>>>>>> 35d92d7f3545b6ddd2e935e463b0a9f51fa7840e
  }

  @Override
  public int setupEffect() {
    return 0;
  }
}
