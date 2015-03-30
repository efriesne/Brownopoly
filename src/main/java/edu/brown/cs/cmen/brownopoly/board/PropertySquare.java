package edu.brown.cs.cmen.brownopoly.board;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class PropertySquare extends BoardSquare {
  Property prop;
  public PropertySquare(int id, String name) {
    super(id);
    prop = new Property(id, name);
  }


  @Override
  public int executeEffect(Player p) {
    return 0;
  }
}
