package edu.brown.cs.cmen.brownopoly.board;

import java.io.Serializable;

import edu.brown.cs.cmen.brownopoly.player.Player;

public abstract class BoardSquare implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 3283777195489487221L;
  private int id;
  private String name;

  public BoardSquare(String name, int id) {
    this.name = name;
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public abstract String executeEffect(Player p, int userInput);

}
