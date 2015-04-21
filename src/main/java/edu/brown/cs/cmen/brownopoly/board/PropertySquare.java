package edu.brown.cs.cmen.brownopoly.board;


import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class PropertySquare extends BoardSquare {
  Property prop;
  private int input;
  public PropertySquare(int id, String name, int[] color) {
    super(name, id);
    this.prop = new Property(id, name, color); 
    OwnableManager.addProperty(this.prop);
    input = 1;
  }

  @Override
  public int getInput() {
    if (prop.owner() != null) {
      input = 0;
    }
    return input;
  }


  @Override
  public String executeEffect(Player p,  int userInput) {
    String message;
    if (prop.owner() == null) {
      if (p.makeBuyingDecision(prop) || (userInput == 1)) {
        if (p.canBuyOwnable(prop)) {
         p.buyProperty(prop);
          message = " bought " + prop.getName();
        } else {
          message = " cannot afford " + prop.getName();
        }
      } else {
        message = " decided not to buy " + prop.getName();
      }
    } else if (prop.owner().equals(p)) {
        message = " owns this property..";
    } else {
        p.payRent(prop);
        message = " paid " + prop.owner().getName() + prop.rent();
    }
    return p.getName() + message + ".";
  }
}
