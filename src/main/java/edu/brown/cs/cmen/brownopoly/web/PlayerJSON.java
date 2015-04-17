package edu.brown.cs.cmen.brownopoly.web;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class PlayerJSON {

  private PropertyJSON[] properties;
  private RailroadJSON[] railroads;
  private UtilityJSON[] utilities;
  private MonopolyJSON[] monopolies;
  private boolean isHuman;
  private int balance, position;
  private String name;

  public PlayerJSON(Player p) {
    // isHuman = p.isHuman();
    balance = p.getBalance();
    position = p.getPosition();
    name = p.getName();
    // p.getBank()
    // pass in Bank to following methods
    properties = getProperties(p);
    monopolies = getMonopolies(p);
    railroads = getRailroads(p);
    utilities = getUtilities(p);
  }

  private RailroadJSON[] getRailroads(Player p) {
    List<Railroad> rails = p.getRailroads();
    RailroadJSON[] objs = new RailroadJSON[rails.size()];
    for (int i = 0; i < objs.length; i++) {
      objs[i] = new RailroadJSON(rails.get(i));
    }
    return objs;
  }

  private UtilityJSON[] getUtilities(Player p) {
    List<Utility> utils = p.getUtilities();
    UtilityJSON[] objs = new UtilityJSON[utils.size()];
    for (int i = 0; i < objs.length; i++) {
      objs[i] = new UtilityJSON(utils.get(i));
    }
    return objs;
  }

  private PropertyJSON[] getProperties(Player p) {
    List<Property> props = p.getProperties();
    PropertyJSON[] objs = new PropertyJSON[props.size()];
    for (int i = 0; i < objs.length; i++) {
      objs[i] = new PropertyJSON(props.get(i));
    }
    return objs;
  }

  private MonopolyJSON[] getMonopolies(Player p) {
    List<Monopoly> mons = p.getMonopolies();
    MonopolyJSON[] objs = new MonopolyJSON[mons.size()];
    for (int i = 0; i < objs.length; i++) {
      objs[i] = new MonopolyJSON(mons.get(i));
    }
    return objs;
  }

}
