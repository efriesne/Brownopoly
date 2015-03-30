package edu.brown.cs.cmen.brownopoly.ownable;

import edu.brown.cs.cmen.brownopoly.MonopolyConstants;

/**
 * 
 * @author npucel
 *
 */
public class Property implements Ownable {

  // private Player owner;
  private int numHouses, id;
  private boolean hasMonopoly, mortgaged;
  private String name;

  public Property(int id, String name) {
    numHouses = 0;
    hasMonopoly = false;
    mortgaged = false;
    // owner = null
    this.id = id;
    this.name = name;
  }

  // public Player owner();

  @Override
  public int rent() {
    // return MonopolyConstants.getRent(id, numHouses)
    return 0;
  }

  @Override
  public void mortgage() {

  }

  @Override
  public void demortgage() {

  }

  public void setMonopoly(boolean mono) {
    hasMonopoly = true;
  }

  public void addHouse() {
    assert numHouses < 5;
    numHouses++;
  }

  public void removeHouse() {
    assert numHouses > 0;
    numHouses--;
  }

  @Override
  public int price() {
    return MonopolyConstants.getPropertyPrice(id);
  }
}
