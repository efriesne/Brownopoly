package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;


public abstract class BoardSquare {
  private int id;
  public BoardSquare(int id) {
    this.id = id;
  }
  
  public int getId() {
    return id;
  }  
  
  public abstract String executeEffect(Player p);
  
}
