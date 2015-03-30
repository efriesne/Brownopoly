package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class JustVisitingSquare extends BoardSquare {

  public JustVisitingSquare(int id) {
    super(id);
  }

  @Override
  public int executeEffect(Player p) {
    return 0;
  }

}
