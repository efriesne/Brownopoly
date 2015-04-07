package edu.brown.cs.cmen.brownopoly.board;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Player;

//TODO: Either make property square an "OwnableSquare" or make separate classes for utility and railroad
public class PropertySquare extends BoardSquare {
  Property prop;
  public PropertySquare(int id, String name, int[] color) {
    super(name, id);
    prop = new Property(id, name, color);
  }


  @Override
  public String executeEffect(Player p) {
    String message;
    if (prop.owner() == null) {
      if (prop.owner().equals(p)) {
        message = " owns this property.";
      } else {
        if (p.makeBuyingDecision(prop)) {
          if (p.buyProperty(prop)) {
            message = " bought " + prop.toString();
          } else {
            message = " cannot afford " + prop.toString();
          }
        } else {
          message = " decided not to buy " + prop.toString();
        }
      }
      } else {
      p.payRent(prop);
      message = " paid " + prop.owner().getName() + prop.rent();
    }
    return p.getName() + message + ".";
  }
}
