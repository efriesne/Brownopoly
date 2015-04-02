package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class FreeParkingSquare extends BoardSquare {
  public FreeParkingSquare(int id) {
    super(id);
  }

  @Override
  public String executeEffect(Player p) {
    int freeparking = Board.freeParking;
    p.addToBalance(freeparking);   
    return p.getName() + " received " + freeparking + ".";
  }

  
}
