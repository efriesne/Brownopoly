package edu.brown.cs.cmen.brownopoly.ownable;

import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * 
 * @author npucel
 *
 */
public class Railroad implements Ownable {

  private Player owner;
  private String name;
  private boolean mortgaged;
  private int id;
  private static final int PRICE = 200;

  public Railroad(int id, String name) {
    this.mortgaged = false;
    this.owner = null;
    this.id = id;
    this.name = name;
  }

  @Override
  public int rent() {
    return MonopolyConstants.getRailroadRent(owner.getRailroads().size());
  }

  @Override
  public void mortgage() {
    mortgaged = true;
    owner.addToBalance(PRICE / 2);
  }

  @Override
  public void demortgage() {
    mortgaged = false;
    owner.addToBalance(-PRICE);
  }

  @Override
  public boolean isMortgaged() {
    return mortgaged;
  }

  @Override
  public int price() {
    return PRICE;
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
  public int getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

}
