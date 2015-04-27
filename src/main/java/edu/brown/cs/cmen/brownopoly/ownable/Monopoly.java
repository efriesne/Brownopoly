package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.brown.cs.cmen.brownopoly.game.Game;

/**
 * 
 * @author npucel
 *
 */
public class Monopoly {

  private Set<Property> members;

  public Monopoly(Property prop) {
    members = new HashSet<>();
    members.add(prop);
    for (Property p : prop.getPropertiesInMonopoly()) {
      members.add(p);
    }
    setMonopoly(true);
  }

  public void setMonopoly(boolean mono) {
    for (Property p : members) {
      p.setMonopoly(mono);
    }
  }

  public List<Property> getProperties() {
    return Collections.unmodifiableList(new ArrayList<>(members));
  }

  public List<Property> canBuildHouses() {
    List<Property> canBuild = new ArrayList<>();
    if (hasMortgagedProperty()) {
      return canBuild;
    }
    List<Integer> numHouses = new ArrayList<>();
    // System.out.println("--------------");
    for (Property p : members) {
      // System.out.println("ID: " + p.getId() + ", houses: " +
      // p.getNumHouses());
      int hotel = p.hasHotel() ? 1 : 0;
      // if p has a hotel, add an additional "house" to its actual number of
      // houses
      numHouses.add(p.getNumHouses() + hotel);
    }
    if (allEqual(numHouses)) {
      if (numHouses.get(0) <= Game.numHousesForHotel()) {
        canBuild.addAll(members);
      }
      return canBuild;
    }
    int maxNumHouses = Collections.max(numHouses);
    for (Property p : members) {
      int hotel = p.hasHotel() ? 1 : 0;
      assert p.getNumHouses() + hotel == maxNumHouses
          || p.getNumHouses() + hotel == maxNumHouses - 1;
      if (p.getNumHouses() == maxNumHouses - 1) {
        canBuild.add(p);
      }
    }
    return canBuild;
  }

  public List<Property> canSellHouses() {
    List<Property> canSell = new ArrayList<>();
    List<Integer> numHouses = new ArrayList<>();
    for (Property p : members) {
      int hotel = p.hasHotel() ? 1 : 0;
      // if p has a hotel, add an additional "house" to its actual number of
      // houses
      numHouses.add(p.getNumHouses() + hotel);
    }
    if (allEqual(numHouses)) {
      if (numHouses.get(0) > 0) {
        canSell.addAll(members);
      }
      return canSell;
    }
    // at least one property has a house if we've made it here, no properties
    // should be mortgaged
    assert !hasMortgagedProperty();
    int maxNumHouses = Collections.max(numHouses);
    for (Property p : members) {
      int hotel = p.hasHotel() ? 1 : 0;
      assert p.getNumHouses() + hotel == maxNumHouses
          || p.getNumHouses() + hotel == maxNumHouses - 1;
      if (p.getNumHouses() + hotel == maxNumHouses) {
        canSell.add(p);
      }
    }
    return canSell;
  }

  private boolean allEqual(List<Integer> nums) {
    for (int i = 1; i < nums.size(); i++) {
      if (nums.get(0) != nums.get(i)) {
        return false;
      }
    }
    return true;
  }

  public boolean hasMortgagedProperty() {
    for (Property p : members) {
      if (p.isMortgaged()) {
        return true;
      }
    }
    return false;
  }

  public void clear() {
    for (Property p : members) {
      p.setMonopoly(false);
      while(p.getNumHouses() > 0) {
        p.owner().sellHouse(p);
      }
    }
  }

}
