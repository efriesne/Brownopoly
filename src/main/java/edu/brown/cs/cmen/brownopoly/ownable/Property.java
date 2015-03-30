package edu.brown.cs.cmen.brownopoly.ownable;

import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * 
 * @author npucel
 *
 */
public class Property implements Ownable {

  private Player owner;
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

  @Override
  public void setOwner(Player p) {
    owner = p;
  }

  @Override
  public Player owner() {
    return owner;
  }

  @Override
  public int rent() {
    return MonopolyConstants.getPropertyRent(id, numHouses);
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
