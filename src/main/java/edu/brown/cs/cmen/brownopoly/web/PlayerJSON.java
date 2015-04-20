package edu.brown.cs.cmen.brownopoly.web;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;
import edu.brown.cs.cmen.brownopoly.player.Bank;
import edu.brown.cs.cmen.brownopoly.player.Player;

class PlayerJSON {

  private PropertyJSON[] properties;
  private RailroadJSON[] railroads;
  private UtilityJSON[] utilities;
  private MonopolyJSON[] monopolies;
  private boolean isAI, inJail;
  private int balance, position;
  private String id;
  private String name;

  public PlayerJSON(Player p) {
    isAI = p.isAI();
    balance = p.getBalance();
    position = p.getPosition();
    name = p.getName();
    Bank b = p.getBank();
    properties = getProperties(b);
    monopolies = getMonopolies(b);
    railroads = getRailroads(b);
    utilities = getUtilities(b);
  }

  private RailroadJSON[] getRailroads(Bank b) {
    List<Railroad> rails = b.getRailroads();
    RailroadJSON[] objs = new RailroadJSON[rails.size()];
    for (int i = 0; i < objs.length; i++) {
      objs[i] = new RailroadJSON(rails.get(i));
    }
    return objs;
  }

  private UtilityJSON[] getUtilities(Bank b) {
    List<Utility> utils = b.getUtilities();
    UtilityJSON[] objs = new UtilityJSON[utils.size()];
    for (int i = 0; i < objs.length; i++) {
      objs[i] = new UtilityJSON(utils.get(i));
    }
    return objs;
  }

  private PropertyJSON[] getProperties(Bank b) {
    List<Property> props = b.getProperties();
    PropertyJSON[] objs = new PropertyJSON[props.size()];
    for (int i = 0; i < objs.length; i++) {
      objs[i] = new PropertyJSON(props.get(i));
    }
    return objs;
  }

  private MonopolyJSON[] getMonopolies(Bank b) {
    List<Monopoly> mons = b.getMonopolies();
    MonopolyJSON[] objs = new MonopolyJSON[mons.size()];
    for (int i = 0; i < objs.length; i++) {
      objs[i] = new MonopolyJSON(mons.get(i));
    }
    return objs;
  }

}
