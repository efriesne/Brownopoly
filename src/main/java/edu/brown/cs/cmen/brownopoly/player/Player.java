package edu.brown.cs.cmen.brownopoly.player;

import java.util.Collections;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.ownable.*;

public abstract class Player {
  private String name;
  private String id;
  private Bank bank;
  private int position, balance, getOutOfJailFree, turnsInJail, lastPosition;
  private boolean inJail, isBankrupt, canMove, isAI, isBroke;
  private List<Player> opponents;

  public Player(String name, List<Property> startingProperties, boolean isAI, String id) {
    this.name = name;
    this.bank = new Bank(startingProperties);
    this.isAI = isAI;
    this.id = id;
    this.inJail = false;
    this.canMove = true;
    this.turnsInJail = 0;
    this.balance = MonopolyConstants.INITIAL_BANK_BALANCE;
    this.position = 0;
    this.lastPosition = 0;
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
    lastPosition = position;
    position += increment;
    if (position >= MonopolyConstants.NUM_BOARDSQUARES) {
      balance += MonopolyConstants.GO_CASH;
    }
    position %= MonopolyConstants.NUM_BOARDSQUARES;
    if(position == 12 || position == 28) {
      int roll = (increment <= 12) ? increment : position - lastPosition;
      OwnableManager.getUtility(position).setDiceRoll(roll);
    }
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
    return balance + bank.propertyWealth();
  }

  /**
   * this is valid so long as the referee knows to update the utility's rent by
   * prompting the user to roll again
   * 
   * @param ownable
   */
  public void payRent(Ownable ownable) {
    int rent = ownable.rent();
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
      prop.setOwner(this);
      return true;
    }
    return false;
  }

  public boolean buyUtility(Utility u) {
    if (canBuyOwnable(u)) {
      addToBalance(-1 * u.price());
      bank.addUtility(u);
      u.setOwner(this);
      return true;
    }
    return false;
  }

  public boolean buyRailroad(Railroad r) {
    if (canBuyOwnable(r)) {
      addToBalance(-1 * r.price());
      bank.addRailroad(r);
      r.setOwner(this);
      return true;
    }
    return false;
  }

  public void removeProperty(Property property) {
    bank.removeProperty(property);
    property.setOwner(null);
  }

  public void removeRailroad(Railroad r) {
    bank.removeRailroad(r);
    r.setOwner(null);
  }

  public void removeUtility(Utility u) {
    bank.removeUtility(u);
    u.setOwner(null);
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
  
  public List<Railroad> getRailroads() {
    return bank.getRailroads();
  }
  
  public List<Utility> getUtilities() {
    return bank.getUtilities();
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
    if (balance + incr < 0) {
      isBroke = true;
    } else {
      isBroke = false;
    }
    if (wealth() + incr < 0) {
      isBankrupt = true;
    }
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

  public void payTax(int tax) {
    addToBalance(-tax);
  }

  public String getId() {
    return id;
  }
  
  public boolean isBroke() {
    return isBroke;
  }

}
