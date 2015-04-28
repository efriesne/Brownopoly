package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class RailroadSquare extends BoardSquare {
  /**
   * 
   */
  private static final long serialVersionUID = 3914331136877382363L;

  public RailroadSquare(int id, String name) {
    super(name, id);
    OwnableManager.addRailroad(new Railroad(id, name));
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    String message;
    Railroad railroad = OwnableManager.getRailroad(getId());
    if (railroad.owner() == null) {
      if (p.makeBuyingDecision(railroad) || (userInput == 1)) {
        if (p.buyRailroad(railroad)) {
          message = " bought " + railroad.getName();
        } else {
          message = " cannot afford " + railroad.getName();
        }
      } else {
        message = " decided not to buy " + railroad.getName();
      }
    } else if (railroad.owner().equals(p)) {
      message = " owns this property";
    } else {
      p.payRent(railroad);
      message = " paid " + railroad.owner().getName() + " $" + railroad.rent();
    }
    return p.getName() + message;
  }

}
