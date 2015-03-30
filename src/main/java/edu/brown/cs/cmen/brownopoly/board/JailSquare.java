package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class JailSquare extends BoardSquare{

  public JailSquare(int id) {
    super(id);
  }

  @Override
  public int executeEffect(Player p) {
    p.moveToJail();
    return 0;
  }

}
