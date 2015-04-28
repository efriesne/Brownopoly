package edu.brown.cs.cmen.brownopoly.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.game.TradeProposal;
import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;

public abstract class Player implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 811086100456401979L;
  private String name;
  private String id;
  private Bank bank;
  private int position, balance, getOutOfJailFree, turnsInJail, lastPosition;
  protected boolean inJail;
  private boolean isBankrupt, exitedJail;
  private boolean isAI;
  private boolean isBroke;
  private List<Player> opponents;

  public Player(String name, List<Property> startingProperties, boolean isAI,
      String id) {
    this.name = name;
    this.bank = new Bank(startingProperties);
    this.isAI = isAI;
    this.id = id;
    this.inJail = false;
    this.turnsInJail = 0;
    this.balance = MonopolyConstants.INITIAL_BANK_BALANCE;
    this.position = 0;
    this.lastPosition = 0;
    this.exitedJail = false;
    this.opponents = new ArrayList<>();
  }

  public boolean isAI() {
    return isAI;
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
    if (position == 12 || position == 28) {
      int roll = (increment <= 12) ? increment : position - lastPosition;
      OwnableManager.getUtility(position).setDiceRoll(roll);
    }
    exitedJail = false;
    return position;
  }

  public abstract boolean makeBuyingDecision(Ownable ownable);

  public abstract boolean makeTradeDecision(TradeProposal proposal);

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

  public void buyHouse(Property p) {
    assert !p.hasHotel();
    p.addHouse();
    addToBalance(-MonopolyConstants.getHouseCost(p.getId()));
  }

  public void sellHouse(Property p) {
    p.removeHouse();
    addToBalance(MonopolyConstants.getHouseCost(p.getId()) / 2);
  }

  public void mortgageOwnable(Ownable ownable) {
    ownable.mortgage();
    addToBalance(ownable.price() / 2);
  }

  public void demortgageOwnable(Ownable ownable) {
    ownable.demortgage();
    int cost = (int) ((ownable.price() / 2) * (11.0 / 10.0));
    addToBalance(-cost);
  }

  public void removeOwnables(String[] ownableIds) {
    for (String id : ownableIds) {
      bank.removeOwnable(OwnableManager.getOwnable(Integer.parseInt(id));
    }
  }

  public void addOwnables(String[] ownableIds) {
    for (String id : ownableIds) {
      bank.addOwnable(OwnableManager.getOwnable(Integer.parseInt(id));
    }
  }

  public boolean trade(Player recipient, String[] initProps, int initMoney,
      String[] recipProps, int recipMoney) {
    boolean accept = recipient.makeTradeDecision(new TradeProposal(this, recipient, initProps, initMoney,
        recipProps, recipMoney));

    if (accept) {
      removeOwnables(initProps);
      recipient.removeOwnables(recipProps);
      addToBalance(-initMoney);
      addOwnables(recipProps);
      recipient.addOwnables(initProps);
      addToBalance(recipMoney);
      addToBalance(-recipMoney);
    }
    return accept;
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

  public List<Railroad> getRailroads() {
    return bank.getRailroads();
  }

  public List<Utility> getUtilities() {
    return bank.getUtilities();
  }

  public List<Property> getValidHouseProps(boolean building) {
    return bank.getValidHouseProps(building);
  }

  public List<Ownable> getValidMortgageProps(boolean mortgaging) {
    return bank.getValidMortgageProps(mortgaging);
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
    exitedJail = true;
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

  public boolean exitedJail() {
    return exitedJail;
  }

}
