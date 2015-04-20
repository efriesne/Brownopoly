package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class JustVisitingSquare extends BoardSquare {

  public JustVisitingSquare(String name, int id) {
    super(name, id);
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    return null;
  }

  @Override
  public int getInput() {
    return 0;
  }

}
