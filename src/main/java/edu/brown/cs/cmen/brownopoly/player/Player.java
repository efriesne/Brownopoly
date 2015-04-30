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
    this.getOutOfJailFree = 0;
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

  public abstract String makeMortgageDecision(String message);

  public abstract TradeProposal makeTradeProposal();
  public abstract String makeBuildDecision();

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
    if(!ownable.isMortgaged()) {
      int rent = ownable.rent();
      addToBalance(-rent);
      if (!this.isBankrupt()) {
        ownable.owner().addToBalance(rent);
      } else {
        ownable.owner().addToBalance(wealth());
      }
    }
  }

  public boolean canBuyOwnable(Ownable property) {
    return balance - property.price() > 0;
  }

  public boolean buyOwnable(Ownable ownable) {
    if (canBuyOwnable(ownable)) {
      addToBalance(-ownable.price());
      ownable.setOwner(this);
      bank.addOwnable(ownable);
      return true;
    }
    return false;
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
      bank.removeOwnable(OwnableManager.getOwnable(Integer.parseInt(id)));
    }
  }

  public void addOwnables(String[] ownableIds) {
    for (String id : ownableIds) {
      bank.addOwnable(OwnableManager.getOwnable(Integer.parseInt(id)));
      OwnableManager.getOwnable(Integer.parseInt(id)).setOwner(this);
    }
  }

  public boolean trade(Player recipient, String[] initProps, int initMoney,
      String[] recipProps, int recipMoney) {
    boolean accept = recipient.makeTradeDecision(new TradeProposal(this,
        recipient, initProps, initMoney, recipProps, recipMoney));
    if (accept) {
      removeOwnables(initProps);
      recipient.removeOwnables(recipProps);

      addOwnables(recipProps);
      addToBalance(-initMoney);
      addToBalance(recipMoney);

      recipient.addOwnables(initProps);
      recipient.addToBalance(initMoney);
      recipient.addToBalance(-recipMoney);
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
    exitedJail = true;
    exitJail();
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
      isBroke = false;
    } else {
      balance += incr;
    }
  }

  public int getPosition() {
    return position;
  }

  public void moveToJail() {
    position = MonopolyConstants.JUSTVISITING_ID;
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

  private void removeOpponent(Player p) {
    opponents.remove(p);
  }
  public void clear() {
    bank.clear();
    for (Player p : opponents) {
      p.removeOpponent(this);
    }
  }

}
