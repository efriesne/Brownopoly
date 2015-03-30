package edu.brown.cs.cmen.brownopoly.ownable;

/**
 * 
 * @author npucel
 *
 */
public class Railroad implements Ownable {

  // private Player owner;
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

  // public Player owner();

}
