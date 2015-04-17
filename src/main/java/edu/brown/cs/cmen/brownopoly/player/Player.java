package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;
import edu.brown.cs.cmen.brownopoly.ownable.*;

public abstract class Player {
  private String name;
  private Bank bank;
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
    this.bank = new Bank(startingProperties);
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
    int wealth = balance + bank.propertyWealth();
    return wealth;
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
  

  public boolean canBuyOwnable(Ownable property) {
    boolean buy = balance - property.price() > 0;
    if (buy) {
      addToBalance(-1*property.price());
    }
    return buy;
  }
  
  public void buyProperty(Property prop) {
    bank.addProperty(prop);
  }
  
  public void buyUtility(Utility u) {
    bank.addUtility(u);
  }
  
  public void buyRailroad(Railroad r) {
    bank.addRailroad(r);
  }


  public void removeProperty(Property property) {
    bank.removeProperty(property);
  }
  
  public void mortgageProperty(Ownable ownable) {
    ownable.mortgage();
    balance += ownable.value();
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
     return bank.getProperties();
  }

  public List<Monopoly> getMonopolies() {
    return bank.getMonopolies();
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
    inJail = true;
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
  
  public int payTax(double tax) {
    balance *= tax;
    return balance;
  }

}
