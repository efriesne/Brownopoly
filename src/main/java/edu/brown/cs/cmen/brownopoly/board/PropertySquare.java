package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class PropertySquare extends BoardSquare {
  /**
   * 
   */
  private static final long serialVersionUID = -3984973051958734224L;

  public PropertySquare(int id, String name, int[] color) {
    super(name, id);
    OwnableManager.addProperty(new Property(id, name, color));
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    String message;
    Property prop = OwnableManager.getProperty(getId());
    System.out.println(prop);
    System.out.println(prop.owner());
    System.out.println(OwnableManager.getProperty(p.getPosition()));
    System.out.println(OwnableManager.getProperty(p.getPosition()).owner());
    if (prop.owner() == null) {
      if (p.makeBuyingDecision(prop) || (userInput == 1)) {
        if (p.buyProperty(prop)) {
          message = " bought " + prop.getName();
        } else {
          message = " cannot afford " + prop.getName();
        }
      } else {
        message = " decided not to buy " + prop.getName();
      }
    } else if (prop.owner().equals(p)) {
      message = " owns this property";
    } else {
      p.payRent(prop);
      message = " paid " + prop.owner().getName() + " $" + prop.rent();
    }
    return p.getName() + message;
  }
}
