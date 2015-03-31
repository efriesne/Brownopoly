package edu.brown.cs.cmen.brownopoly.ownable;

import edu.brown.cs.cmen.brownopoly.game.Game;
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
    assert numHouses < Game.numHousesForHotel();
    numHouses++;
  }

  public void removeHouse() {
    assert numHouses > 0;
    numHouses--;
  }

  public int getNumHouses() {
    return numHouses == Game.numHousesForHotel() ? numHouses - 1 : numHouses;
  }

  public boolean hasHotel() {
    return numHouses == Game.numHousesForHotel();
  }

  @Override
  public int price() {
    return MonopolyConstants.getPropertyPrice(id);
  }
}
