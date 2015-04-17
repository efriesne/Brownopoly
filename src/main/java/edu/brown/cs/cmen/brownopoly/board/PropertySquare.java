package edu.brown.cs.cmen.brownopoly.board;

<<<<<<< HEAD
=======
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
>>>>>>> 35d92d7f3545b6ddd2e935e463b0a9f51fa7840e
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class PropertySquare extends BoardSquare {
  Property prop;

  public PropertySquare(int id, String name, int[] color) {
    super(name, id);
<<<<<<< HEAD
    this.prop = new Property(id, name, color); 
  }

  @Override
  public int setupEffect() {
    return 1;
  }

=======
    this.prop = new Property(id, name, color);
    OwnableManager.addProperty(this.prop);
  }

>>>>>>> 35d92d7f3545b6ddd2e935e463b0a9f51fa7840e
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
<<<<<<< HEAD
      } else {
        message = " decided not to buy " + prop.toString();
      }
    } else if (prop.owner().equals(p)) {
      message = " owns this property.";
    } else {
      p.payRent(prop); 
    //still need to figure out utility
=======
      }
    } else {
      p.payRent(prop);
>>>>>>> 35d92d7f3545b6ddd2e935e463b0a9f51fa7840e
      message = " paid " + prop.owner().getName() + prop.rent();
    }
    return p.getName() + message + ".";
  }
}
