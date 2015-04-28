package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.player.Player;

class OwnableSquare extends BoardSquare {

  /**
   * 
   */
  private static final long serialVersionUID = 1717775535336575343L;

  public OwnableSquare(String name, int id) {
    super(name, id);
    // TODO Auto-generated constructor stub
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    String message;
    Ownable own = OwnableManager.getOwnable(getId());
    if (!own.isOwned()) {
      if (p.makeBuyingDecision(own) || (userInput == 1)) {
        if (p.buyOwnable(own)) {
          message = " bought " + own.getName();
        } else {
          message = " cannot afford " + own.getName();
        }
      } else {
        message = " decided not to buy " + own.getName();
      }
    } else if (own.owner().equals(p)) {
      message = " owns this property";
    } else {
      p.payRent(own);
      message = " paid " + own.owner().getName() + " $" + own.rent();
    }
    return p.getName() + message;
  }
}
