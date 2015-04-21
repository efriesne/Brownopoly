package edu.brown.cs.cmen.brownopoly.ownable;

import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * 
 * @author npucel
 *
 */
public class Utility implements Ownable {

  private boolean mortgaged;
  private String name;
  private int id, diceRoll;
  private static final int PRICE = 150;
  private Player owner;

  public Utility(int id, String name) {
    this.mortgaged = false;
    this.owner = null;
    this.id = id;
    this.name = name;
  }

  @Override
  public int rent() {
    return diceRoll * MonopolyConstants.getUtilityRent(owner.getUtilities().size());
  }

  @Override
  public Player owner() {
    return owner;
  }

  @Override
  public void setOwner(Player p) {
    owner = p;
  }

  @Override
  public void mortgage() {

  }

  @Override
  public void demortgage() {

  }

  @Override
  public int price() {
    return PRICE;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isMortgaged() {
    return mortgaged;
  }
  
  public void setDiceRoll(int dice) {
    diceRoll = dice;
  }

}
