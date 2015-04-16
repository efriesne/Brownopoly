package edu.brown.cs.cmen.brownopoly.board;

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
}
