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
  private boolean isAI, inJail, isBankrupt, isBroke, exitedJail;
  private int balance, position, turnsInJail;
  private String id;
  private String name;
  private boolean jailFree;

  PlayerJSON(Player p) {
    this.isAI = p.isAI();
    this.balance = p.getBalance();
    this.position = p.getPosition();
    this.name = p.getName();
    this.properties = getProperties(p);
    this.monopolies = getMonopolies(p);
    this.railroads = getRailroads(p);
    this.utilities = getUtilities(p);
    this.inJail = p.isInJail();
    this.id = p.getId();
    this.turnsInJail = p.getTurnsInJail();
    this.isBankrupt = p.isBankrupt();
    this.isBroke = p.isBroke();
    this.exitedJail = p.exitedJail();
    this.jailFree = p.hasJailFree();
  }

  public String getID() {
    return id;
  }

  public String getName() { return name; }

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
