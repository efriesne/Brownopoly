package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public abstract class BoardSquare {
  private int id;
  private String name;

  public BoardSquare(String name, int id) {
    this.name = name;
    this.id = id;
  }

  public int getId() {
    return id;
  }

  // public String getSquareName() {
  // return MonopolyConstants.getName(id);
  // }

  public String getName() {
    return name;
  }

  public abstract String executeEffect(Player p);

}
