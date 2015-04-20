package edu.brown.cs.cmen.brownopoly.player;

import java.util.Collections;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;

public abstract class Player {
  private String name;
  private Bank bank;
  private int position, balance, getOutOfJailFree, turnsInJail;
  private boolean inJail, isBankrupt, canMove, isAI;
  private List<Player> opponents;

  public Player(String name, List<Property> startingProperties, boolean isAI) {
    this.name = name;
    this.bank = new Bank(startingProperties);
    this.isAI = isAI;
    canMove = true;
  }

  public boolean isAI() {
    return isAI;
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
    return Collections.unmodifiableList(opponents);
  }

  public void addOpponent(Player player) {
    opponents.add(player);
  }

  public int move(int increment) {
    position += increment;
    if (position >= MonopolyConstants.NUM_BOARDSQUARES) {
      balance += MonopolyConstants.GO_CASH;
    }
    position %= MonopolyConstants.NUM_BOARDSQUARES;
    return position;
  }

  public abstract boolean makeBuyingDecision(Ownable ownable);

  public abstract void startTurn();

  public boolean isBankrupt() {
    return isBankrupt;
  }

  public boolean hasNegativeBalance() {
    return balance < 0;
  }

  public int wealth() {
    int wealth = balance + bank.propertyWealth();
    return wealth;
  }

  /**
   * this is valid so long as the referee knows to update the utility's rent by
   * prompting the user to roll again
   * 
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
    return balance - property.price() > 0;
  }

  public boolean buyProperty(Property prop) {
    if (canBuyOwnable(prop)) {
      addToBalance(-1 * prop.price());
      bank.addProperty(prop);
      return true;
    }
    return false;
  }

  public boolean buyUtility(Utility u) {
    if (canBuyOwnable(u)) {
      addToBalance(-1 * u.price());
      bank.addUtility(u);
      return true;
    }
    return false;
  }

  public boolean buyRailroad(Railroad r) {
    if (canBuyOwnable(r)) {
      addToBalance(-1 * r.price());
      bank.addRailroad(r);
      return true;
    }
    return false;
  }

  public void removeProperty(Property property) {
    bank.removeProperty(property);
  }

  public void removeRailroad(Railroad r) {
    bank.removeRailroad(r);
  }

  public void removeUtility(Utility u) {
    bank.removeUtility(u);
  }

  public void mortgageOwnable(Ownable ownable) {
    ownable.mortgage();
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

  public Bank getBank() {
    return bank;
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
    position = MonopolyConstants.JAIL_POSITION;
    inJail = true;
  }

  public void exitJail() {
    inJail = false;
    turnsInJail = 0;
  }

  public void payBail() {
    balance -= MonopolyConstants.JAIL_BAIL;
    Board.freeParking += MonopolyConstants.JAIL_BAIL;
    exitJail();
  }

  public int payTax(double tax) {
    balance *= tax;
    return balance;
  }

}
