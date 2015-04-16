package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 4/16/15.
 */
public class RailroadSquare extends BoardSquare{
    Railroad railroad;
    public RailroadSquare(int id, String name) {
        super(name, id);
        this.railroad = new Railroad(id, name);
    }


    @Override
    public String executeEffect(Player p) {
        String message;
        if (railroad.owner() == null) {
            if (railroad.owner().equals(p)) {
                message = " owns this property.";
            } else {
                if (p.makeBuyingDecision(railroad)) {
                    if (p.buyRailroad(railroad)) {
                        message = " bought " + railroad.toString();
                    } else {
                        message = " cannot afford " + railroad.toString();
                    }
                } else {
                    message = " decided not to buy " + railroad.toString();
                }
            }
        } else {
            p.payRent(railroad);
            message = " paid " + railroad.owner().getName() + railroad.rent();
        }
        return p.getName() + message + ".";
    }
}
