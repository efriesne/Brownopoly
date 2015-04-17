package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class FreeParkingSquare extends BoardSquare {
  public FreeParkingSquare(String name, int id) {
    super(name, id);
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    int freeParking = Board.freeParking;
    p.addToBalance(freeParking);
    return p.getName() + " received " + freeParking + ".";
  }

  @Override
  public int getInput() {
    return 0;
  }

  
}
