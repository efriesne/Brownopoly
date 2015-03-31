package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;

public abstract class Player {
  private String name;
  private List<Property> properties;
  private List<Monopoly> monopolies;
  private List<Railroad> railroads;
  private List<Utility> utilities;
  private int getOutOfJailFree;
  private boolean inJail;
  private int position;
  private int balance;
  private boolean isBankrupt;
  private int turnsInJail;
  private List<Player> opponents;
  
  public Player(String name, List<Property> startingProperties) {
    this.name = name;
    this.properties = startingProperties;
  }
  
  public List<Player> getOpponents() {
    return opponents;
  }
  
  public void addOpponent(Player player) {
    opponents.add(player);
  }
  
  public int move(int increment) {
    position += increment;
    if (position >= 40) {
      balance += 200;
    }
    position %= 40;
    return position;
  }
  
  public abstract boolean makeBuyingDecision();
  
  public boolean isBankrupt() {
    return isBankrupt;
  }
  
  //TODO
  public int wealth() {
    int wealth = 0;
    for (Property p : properties) {
      wealth += p.price();
    }
    for (Monopoly m : monopolies) {
      for (Property p : m.getProperties()) {
        wealth += p.price();
      }
    }
    for (Railroad r : railroads) {
      wealth += r.price();
    }
    for (Utility u : utilities) {
      wealth += u.price();
    }
    wealth *= .5;
    return wealth + balance;
  }
  
  public void payRent(Property property) {
    int rent = property.rent();
    if (wealth() - rent < 0) {
      isBankrupt = true;
    }
    property.owner().setBalance(rent);
    setBalance(-rent);
  }

  public void buyProperty(Property property) {
    properties.add(property);
    setBalance(-1*property.price());
  }

  public void removeProperty(Property property) {
    properties.remove(property);
  }

  public void receiveTrade(List<Property> properties, int moneyToGet) {
    properties.addAll(properties);
    balance += moneyToGet;
  }

  public int getBalance() {
    return balance;
  }

  public List<Property> getProperties() {
     return properties;
  }

  public List<Monopoly> getMonopolies() {
    return monopolies;
  }

  public void addJailFree() {
    getOutOfJailFree++;
  }

  public boolean hasJailFree() {
    return getOutOfJailFree > 0;
  }

  public void useJailFree() {
    getOutOfJailFree--;
  }

  public boolean isInJail() {
    return inJail;
  }

  public int getTurnsInJail() {
    return turnsInJail;
  }

  public void setBalance(int newBalance) {
    balance += newBalance;
  }

  public int getPosition() {
    return position;
  }

  public void moveToJail() {
    position = 10;
  }

}
