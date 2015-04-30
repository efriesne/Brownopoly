package edu.brown.cs.cmen.brownopoly.web;

import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

class PropertyJSON {

  private int[] color;
  private boolean mortgaged;
  private String name;
  private int id, numHouses, houseCost;

  public PropertyJSON(Property prop) {
    color = prop.getColor();
    mortgaged = prop.isMortgaged();
    name = prop.getName();
    id = prop.getId();
    houseCost = MonopolyConstants.getHouseCost(id);
    numHouses = prop.getNumHouses();
    if (prop.hasHotel()) {
      numHouses++;
    }
  }

}
