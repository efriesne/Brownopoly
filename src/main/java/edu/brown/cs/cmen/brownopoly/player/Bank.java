package edu.brown.cs.cmen.brownopoly.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;

public class Bank {

  private List<Property> properties;
  private List<Monopoly> monopolies;
  private List<Railroad> railroads;
  private List<Utility> utilities;

  public Bank(List<Property> startingProperties) {
    properties = startingProperties;
    monopolies = new ArrayList<>();
    railroads = new ArrayList<>();
    utilities = new ArrayList<>();
  }

  public void addProperty(Property p) {
    properties.add(p);
  }

  public void addRailroad(Railroad r) {
    railroads.add(r);
  }

  public void addUtility(Utility u) {
    utilities.remove(u);
  }

  public void removeProperty(Property p) {
    properties.remove(p);
  }

  public void removeRailroad(Railroad r) {
    railroads.remove(r);
  }

  public void removeUtility(Utility u) {
    utilities.remove(u);
  }

  public List<Property> getProperties() {
    return Collections.unmodifiableList(properties);
  }

  public List<Railroad> getRailroads() {
    return Collections.unmodifiableList(railroads);
  }

  public List<Utility> getUtilities() {
    return Collections.unmodifiableList(utilities);
  }

  public List<Monopoly> getMonopolies() {
    return Collections.unmodifiableList(monopolies);
  }

  public int propertyWealth() {
    int wealth = 0;
    for (Property p : properties) {
      wealth += p.value();
    }
    for (Monopoly m : monopolies) {
      for (Property p : m.getProperties()) {
        wealth += p.value();
      }
    }
    for (Railroad r : railroads) {
      wealth += r.price() / 2;
    }
    for (Utility u : utilities) {
      wealth += u.price() / 2;
    }
    return wealth;
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
