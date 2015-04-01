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
  
  public String getName() {
    return name;
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
  
  public abstract boolean makeBuyingDecision(Property prop);
  
  public abstract void startTurn();
  
  public boolean isBankrupt() {
    return isBankrupt;
  }
  
  public int wealth() {
    int wealth = 0;
    for (Property p : properties) {
      wealth += p.price();
    }
    for (Monopoly m : monopolies) {
      for (Property p : m.getProperties()) {
        wealth += p.value();
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
  

  public boolean buyProperty(Property property) {
    boolean buy = balance - property.price() > 0;
    if (buy) {
      properties.add(property);
      setBalance(-1*property.price());
    }
    return buy;
  }

  public void removeProperty(Property property) {
    properties.remove(property);
  }
  
  public void mortgageProperty(Property property) {
    property.mortgage();
    balance += property.value(); 
    //change to include mortgage price and should be just 
    //mortgage if no houses/hotels
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
  
  public void exitJail() {
    inJail = false;
    turnsInJail = 0;
  }
  public void payBail() {
    balance -= 50;
    exitJail();
  }
  
  public void removeFromGame() {
    //TO DO: is this different if 
    //they went bankrupt by paying the bank vs paying a player
  }

}
