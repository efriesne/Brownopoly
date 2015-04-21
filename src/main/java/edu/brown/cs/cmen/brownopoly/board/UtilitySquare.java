package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class UtilitySquare extends BoardSquare {
  Utility util;
  public UtilitySquare(int id, String name) {
    super(name, id);
    util = new Utility(id, name);
    OwnableManager.addUtility(this.util);
  }
  
  @Override
  public String executeEffect(Player p,  int userInput) {
    String message;
    if (util.owner() == null) {
      if (p.makeBuyingDecision(util) || (userInput == 1)) {
        if (p.buyUtility(util)) {
          message = " bought " + util.getName();
        } else {
          message = " cannot afford " + util.getName();
        }
      } else {
        message = " decided not to buy " + util.getName();
      }
    } else if (util.owner().equals(p)) {
      message = " owns this property";
    } else {
      p.payRent(util);
      message = " paid " + util.owner().getName() + " $" + util.rent();
    }
    return p.getName() + message;
  }

}


