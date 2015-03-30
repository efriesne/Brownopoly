package edu.brown.cs.cmen.brownopoly.ownable;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * 
 * @author npucel
 *
 */
public class Utility implements Ownable {

  private boolean mortgaged;
  private int id;
  private static final int PRICE = 150;

  private Player owner;

  @Override
  public int rent() {
    return 0;
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

}
