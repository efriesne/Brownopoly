package edu.brown.cs.cmen.brownopoly.ownable;

/**
 * 
 * @author npucel
 *
 */
public class Utility implements Ownable {

  private boolean mortgaged;
  private int id;
  private static final int PRICE = 150;

  // private Player owner;

  @Override
  public int rent() {
    return 0;
  }

  // public Player owner();

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
