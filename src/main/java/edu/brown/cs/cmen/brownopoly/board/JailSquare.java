package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class JailSquare extends BoardSquare{

  public JailSquare(String name, int id) {
    super(name, id);
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    p.moveToJail();
    return p.getName() + "was sent to Jail!";
  }

  @Override
  public int setupEffect() {
    return 0;
  }

}
