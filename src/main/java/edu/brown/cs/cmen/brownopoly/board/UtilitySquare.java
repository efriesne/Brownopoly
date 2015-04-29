package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class UtilitySquare extends BoardSquare {
  /**
   * 
   */
  private static final long serialVersionUID = 4180535061325401999L;

  public UtilitySquare(int id, String name) {
    super(name, id);
    // OwnableManager.addUtility(new Utility(id, name));
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    String message;
    Utility util = OwnableManager.getUtility(getId());
    if (util.owner() == null) {
      if (p.makeBuyingDecision(util) || (userInput == 1)) {
        if (p.buyOwnable(util)) {
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
