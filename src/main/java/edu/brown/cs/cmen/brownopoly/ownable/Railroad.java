package edu.brown.cs.cmen.brownopoly.ownable;

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

  public int getId() {
    return id;
  }

}
