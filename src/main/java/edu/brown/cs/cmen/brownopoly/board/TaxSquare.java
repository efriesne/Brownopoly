package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class TaxSquare extends BoardSquare {
  private int tax;
  public TaxSquare(int id, String name, int tax) {
    super(name, id);
    this.tax = tax;
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    p.payTax(tax);
    Board.freeParking += tax;
    return p.getName() + " paid $" + tax + " to the bank.";
  }
}
