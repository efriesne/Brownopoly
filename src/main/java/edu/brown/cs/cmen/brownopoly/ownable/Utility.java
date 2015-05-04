package edu.brown.cs.cmen.brownopoly.ownable;

import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * 
 * @author npucel
 *
 */
public class Utility implements Ownable {

  /**
   * 
   */
  private static final long serialVersionUID = 3429759012687682383L;
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
    if(diceRoll == 0) {
      diceRoll = MonopolyConstants.EXPECTED_DICE_ROLL;
    }
    return diceRoll
        * MonopolyConstants.getUtilityRent(owner.getUtilities().size());
  }

  @Override
  public Player owner() {
    return owner;
  }

  @Override
  public boolean isOwned() {
    return owner != null;
  }

  @Override
  public void setOwner(Player p) {
    owner = p;
  }

  @Override
  public void mortgage() {
    mortgaged = true;
  }

  @Override
  public void demortgage() {
    mortgaged = false;
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
  public String getType() {
    return "utility";
  }

  @Override
  public boolean isMortgaged() {
    return mortgaged;
  }

  public void setDiceRoll(int dice) {
    diceRoll = dice;
  }

}
