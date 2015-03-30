package edu.brown.cs.cmen.brownopoly.ownable;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * 
 * @author npucel
 *
 */
public class Railroad implements Ownable {

  private Player owner;
  private boolean mortgaged;
  private int id;
  private static final int PRICE = 200;

  @Override
  public int rent() {
    // return Constants.getRailroadRent(owner.numRailroads())
    return 0;
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
  public Player owner() {
    return owner;
  }

  @Override
  public void setOwner(Player p) {
    owner = p;
  }

}
