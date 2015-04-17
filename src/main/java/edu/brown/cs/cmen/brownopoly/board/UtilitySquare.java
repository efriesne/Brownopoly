package edu.brown.cs.cmen.brownopoly.board;

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
}
