package edu.brown.cs.cmen.brownopoly.player;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;

/*friendly*/class Bank {

  private List<Property> properties;
  private List<Monopoly> monopolies;
  private List<Railroad> railroads;
  private List<Utility> utilities;

  public Bank() {
    properties = new ArrayList<>();
    monopolies = new ArrayList<>();
    railroads = new ArrayList<>();
    utilities = new ArrayList<>();
  }

  public void addProperty(Property p) {

  }

  public void addRailroad(Railroad r) {

  }

  public void addUtility(Utility u) {

  }

  private boolean checkMonopoly(Property p) {
    for (Property other : p.getPropertiesInMonopoly()) {
      if (!properties.contains(other)) {
        return false;
      }
    }
    return true;
  }
}
