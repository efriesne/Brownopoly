package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.ownable.*;

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
  private boolean canMove;
  
  public Player(String name, List<Property> startingProperties) {
    this.name = name;
    this.properties = startingProperties;
    canMove = true;
  }
  
  public void setCanMove(boolean movable) {
    canMove = movable;
  }
  
  public boolean getCanMove() {
    return canMove;
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
  
  public abstract boolean makeBuyingDecision(Ownable ownable);
  
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

  /**
   * this is valid so long as the referee knows to update the utility's rent by prompting the user to roll again
   * @param ownable
   */
  public void payRent(Ownable ownable) {
    int rent = ownable.rent();
    if (wealth() - rent < 0) {
      isBankrupt = true;
    }
    ownable.owner().addToBalance(rent);
    addToBalance(-rent);
  }
  

  public boolean buyProperty(Property property) {
    boolean buy = balance - property.price() > 0;
    if (buy) {
      properties.add(property);
      addToBalance(-1*property.price());
    }
    //check for monopoly
    return buy;
  }

  public boolean buyRailroad(Railroad railroad) {
    boolean buy = balance - railroad.price() > 0;
    if (buy) {
      railroads.add(railroad);
      addToBalance(-1*railroad.price());
    }
    return buy;
  }

  public boolean buyUtility(Utility utility) {
    boolean buy = balance - utility.price() > 0;
    if (buy) {
      utilities.add(utility);
      addToBalance(-1*utility.price());
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
  
  public void addTurnsInJail() {
    turnsInJail++;
  }

  public void addToBalance(int incr) {
    balance += incr;
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
    Board.freeParking += 50;
    exitJail();
  }
  
  public void removeFromGame() {
    //TO DO: is this different if 
    //they went bankrupt by paying the bank vs paying a player
  }
  
  public int payTax(double tax) {
    balance *= tax;
    return balance;
  }

}
