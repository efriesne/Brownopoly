package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class JustVisitingSquare extends BoardSquare {

  public JustVisitingSquare(int id) {
    super(id);
  }

  @Override
  public String executeEffect(Player p) {
    return p.getName() + " landed on Just Visiting!";
  }

}
