package edu.brown.cs.cmen.brownopoly.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;

class Bank implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2838419343581513647L;
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

  void addProperty(Property p) {
    if (checkMonopoly(p)) {
      Monopoly monopoly = new Monopoly(p);
      monopolies.add(monopoly);
      for (Property prop : p.getPropertiesInMonopoly()) {
        properties.remove(prop);
      }
    } else {
      properties.add(p);
    }
    OwnableManager.addOwned(p);
  }

  void addRailroad(Railroad r) {
    railroads.add(r);
    OwnableManager.addOwned(r);
  }

  void addUtility(Utility u) {
    utilities.add(u);
    OwnableManager.addOwned(u);
  }

  void removeProperty(Property p) {
    properties.remove(p);
    OwnableManager.addUnowned(p);
    // not handled for when p is in monopoly
  }

  void removeRailroad(Railroad r) {
    railroads.remove(r);
    OwnableManager.addUnowned(r);
  }

  void removeUtility(Utility u) {
    utilities.remove(u);
    OwnableManager.addUnowned(u);
  }

  Monopoly removeMonopolyProperty(Property p) {

    for (Monopoly m : monopolies) {
      if (m.getProperties().contains(p)) {
        m.clear();
        properties.addAll(m.getProperties());
        return m;
      }
    }
    return null;
  }

  void removeOwnables(String[][] ownables) {
    // properties in monopolies
    for (String id : ownables[0]) {
      Property property = OwnableManager.getProperty(Integer.parseInt(id));
      monopolies.remove(removeMonopolyProperty(property));
      removeProperty(property);
    }

    for (String id : ownables[1]) {
      removeProperty(OwnableManager.getProperty(Integer.parseInt(id)));
    }
    // railroads
    for (String id : ownables[2]) {
      removeRailroad(OwnableManager.getRailroad(Integer.parseInt(id)));
    }
    // utilities
    for (String id : ownables[3]) {
      removeUtility(OwnableManager.getUtility(Integer.parseInt(id)));
    }
  }

  void addOwnables(String[][] ownables) {
    // properties in monopolies
    for (String id : ownables[0]) {
      addProperty(OwnableManager.getProperty(Integer.parseInt(id)));
    }
    // properties
    for (String id : ownables[1]) {
      addProperty(OwnableManager.getProperty(Integer.parseInt(id)));
    }
    // railroads
    for (String id : ownables[2]) {
      addRailroad(OwnableManager.getRailroad(Integer.parseInt(id)));
    }
    // utilities
    for (String id : ownables[3]) {
      addUtility(OwnableManager.getUtility(Integer.parseInt(id)));
    }
  }

  List<Property> getProperties() {
    return Collections.unmodifiableList(properties);
  }

  List<Railroad> getRailroads() {
    return Collections.unmodifiableList(railroads);
  }

  List<Utility> getUtilities() {
    return Collections.unmodifiableList(utilities);
  }

  List<Monopoly> getMonopolies() {
    return Collections.unmodifiableList(monopolies);
  }

  List<Property> getValidHouseProps(boolean building) {
    List<Property> valids = new ArrayList<>();
    for (Monopoly m : monopolies) {
      if (building) {
        valids.addAll(m.canBuildHouses());
      } else {
        valids.addAll(m.canSellHouses());
      }
    }
    return valids;
  }

  List<Ownable> getValidMortgageProps(boolean mortgaging) {
    List<Ownable> valids = new ArrayList<>();
    for (Monopoly m : monopolies) {
      List<Property> currProps = new ArrayList<>();
      boolean hasHouses = false;
      for (Property p : m.getProperties()) {
        if (p.getNumHouses() > 0) {
          hasHouses = true;
          break;
        }
        if (!p.isMortgaged() && mortgaging) {
          currProps.add(p);
        } else if (p.isMortgaged() && !mortgaging) {
          currProps.add(p);
        }
      }
      if (!hasHouses) {
        valids.addAll(currProps);
      }
    }
    if (mortgaging) {
      valids.addAll(getDemortgaged());
    } else {
      valids.addAll(getMortgaged());
    }
    return valids;
  }

  /**
   * Finds all mortgaged Ownables, excluding Properties within a Monopoly
   * 
   * @return
   */
  private List<Ownable> getMortgaged() {
    List<Ownable> mortgaged = getMortgagedHelper(railroads, true);
    mortgaged.addAll(getMortgagedHelper(properties, true));
    mortgaged.addAll(getMortgagedHelper(utilities, true));
    return mortgaged;
  }

  /**
   * Finds all demortgaged Ownables, excluding Properties within a Monopoly
   * 
   * @return
   */
  private List<Ownable> getDemortgaged() {
    List<Ownable> demortgaged = getMortgagedHelper(railroads, false);
    demortgaged.addAll(getMortgagedHelper(properties, false));
    demortgaged.addAll(getMortgagedHelper(utilities, false));
    return demortgaged;
  }

  private List<Ownable> getMortgagedHelper(
      Iterable<? extends Ownable> ownables, boolean findMortgaged) {
    List<Ownable> valids = new ArrayList<>();
    for (Ownable o : ownables) {
      if (o.isMortgaged() && findMortgaged) {
        valids.add(o);
      } else if (!o.isMortgaged() && !findMortgaged) {
        valids.add(o);
      }
    }
    return valids;
  }

  int propertyWealth() {
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

  public boolean checkMonopoly(Property p) {
    for (Property other : p.getPropertiesInMonopoly()) {
      if (!properties.contains(other)) {
        return false;
      }
    }
    return true;
  }
}
