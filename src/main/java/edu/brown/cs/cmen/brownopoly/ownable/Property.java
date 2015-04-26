package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
  // color is in RGB format
  private int[] color;
  private boolean hasMonopoly, mortgaged;
  private String name;
  private Set<Property> monopolyProperties;

  public Property(int id, String name, int[] color) {
    numHouses = 0;
    hasMonopoly = false;
    mortgaged = false;
    // owner = null
    this.id = id;
    this.name = name;
    this.color = color;

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
    int mult = 1;
    if (hasMonopoly && numHouses == 0) {
      mult = 2;
    }
    return mult * MonopolyConstants.getPropertyRent(id, numHouses);
  }

  @Override
  public void mortgage() {
    assert numHouses == 0;
    mortgaged = true;
  }

  @Override
  public void demortgage() {
    assert numHouses == 0;
    mortgaged = false;
  }

  @Override
  public boolean isMortgaged() {
    return mortgaged;
  }

  public void setMonopoly(boolean mono) {
    hasMonopoly = true;
  }

  public void addHouse() {
    assert numHouses <= Game.numHousesForHotel();
    numHouses++;
  }

  public void removeHouse() {
    assert numHouses > 0;
    numHouses--;
  }

  public int getNumHouses() {
    return numHouses == Game.numHousesForHotel() + 1 ? numHouses - 1
        : numHouses;
  }

  public boolean hasHotel() {
    return numHouses == Game.numHousesForHotel() + 1;
  }

  /**
   * Returns the value of the houses and hotels this property has. The value of
   * a house or hotel is half of its cost.
   * 
   * @return
   */
  public int value() {
    // include mortgage
    // houses + hotels + price /2
    int houseVal = MonopolyConstants.getHouseCost(id) * numHouses;
    return (houseVal + price()) / 2;
  }

  @Override
  public int price() {
    return MonopolyConstants.getPropertyPrice(id);
  }

  public void joinMonopoly() {
    monopolyProperties = initMonopolyProperties();
  }

  private Set<Property> initMonopolyProperties() {
    Set<Property> set = new HashSet<>();
    for (int i : MonopolyConstants.getPropertiesInMonopoly(id)) {
      set.add(OwnableManager.getProperty(i));
    }
    return set;
  }

  @Override
  public int getId() {
    return id;
  }

  public Set<Property> getPropertiesInMonopoly() {
    return Collections.unmodifiableSet(monopolyProperties);
  }

  @Override
  public String getName() {
    return name;
  }

  public int[] getColor() {
    return Arrays.copyOf(color, color.length);
  }
}
