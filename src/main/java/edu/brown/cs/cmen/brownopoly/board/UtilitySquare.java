package edu.brown.cs.cmen.brownopoly.board;

<<<<<<< HEAD
import edu.brown.cs.cmen.brownopoly.ownable.Utility;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class UtilitySquare extends BoardSquare {
  Utility util;
  public UtilitySquare(String name, int id) {
    super(name, id);
    //util = new Utility(id, name);
  }
  
  @Override
  public String executeEffect(Player p,  int userInput) {
    String message;
    if (util.owner() == null) {
      if (p.makeBuyingDecision(util) || (userInput == 1)) {
        if (p.canBuyOwnable(util)) {
         p.buyUtility(util);
          message = " bought " + util.toString();
        } else {
          message = " cannot afford " + util.toString();
        }
      } else {
        message = " decided not to buy " + util.toString();
      }
    } else if (util.owner().equals(p)) {
      message = " owns this property.";
    } else {
      p.payRent(util); 
    //still need to figure out utility
      message = " paid " + util.owner().getName() + util.rent();
    }
    return p.getName() + message + ".";
  }

  @Override
  public int setupEffect() {
    return 1;
  }
=======
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;
import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 4/16/15.
 */
public class UtilitySquare extends BoardSquare {
    Utility utility;
    public UtilitySquare(int id, String name) {
        super(name, id);
        this.utility = new Utility(id, name);
        OwnableManager.addUtility(this.utility);
    }


    @Override
    public String executeEffect(Player p) {
        String message;
        if (utility.owner() == null) {
            if (utility.owner().equals(p)) {
                message = " owns this property.";
            } else {
                if (p.makeBuyingDecision(utility)) {
                    if (p.buyUtility(utility)) {
                        message = " bought " + utility.toString();
                    } else {
                        message = " cannot afford " + utility.toString();
                    }
                } else {
                    message = " decided not to buy " + utility.toString();
                }
            }
        } else {
            p.payRent(utility);
            message = " paid " + utility.owner().getName() + utility.rent();
        }
        return p.getName() + message + ".";
    }
>>>>>>> 35d92d7f3545b6ddd2e935e463b0a9f51fa7840e
}
