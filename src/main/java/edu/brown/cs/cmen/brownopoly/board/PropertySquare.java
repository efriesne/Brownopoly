package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class PropertySquare extends BoardSquare {
  Property prop;
  public PropertySquare(int id, String name, int[] color) {
    super(name, id);
    this.prop = new Property(id, name, color); 
  }

  @Override
  public int setupEffect() {
    return 1;
  }

  @Override
  public String executeEffect(Player p,  int userInput) {
    String message;
    if (prop.owner() == null) {
      if (p.makeBuyingDecision(prop) || (userInput == 1)) {
        if (p.canBuyOwnable(prop)) {
         p.buyProperty(prop);
          message = " bought " + prop.toString();
        } else {
          message = " cannot afford " + prop.toString();
        }
      } else {
        message = " decided not to buy " + prop.toString();
      }
    } else if (prop.owner().equals(p)) {
      message = " owns this property.";
    } else {
      p.payRent(prop); 
    //still need to figure out utility
      message = " paid " + prop.owner().getName() + prop.rent();
    }
    return p.getName() + message + ".";
  }
}
