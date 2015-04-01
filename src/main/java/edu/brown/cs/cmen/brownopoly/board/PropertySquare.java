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
  public String executeEffect(Player p) {
    String message;
    if (prop.owner() == null) {
      if (p.makeBuyingDecision(prop)) {
        if (p.buyProperty(prop)) {
          message = " bought " + prop.toString();
        } else {
          message = " cannot afford " + prop.toString();
        }
      } else {
        message = " decided not to buy " + prop.toString();
      }
    } else {
      p.payRent(prop);
      message = " paid " + prop.owner().getName() + prop.rent();
    }
    return p.getName() + message + ".";
  }
}
