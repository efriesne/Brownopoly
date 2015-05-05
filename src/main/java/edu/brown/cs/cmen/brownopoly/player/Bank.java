package edu.brown.cs.cmen.brownopoly.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.*;

class Bank implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2838419343581513647L;
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
    Collections.sort(properties, new CompareProperty());
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

  void removeProperty(Property p) {
    if (p.isInMonopoly()) {
      monopolies.remove(removeMonopolyProperty(p));
    }
    properties.remove(p);
    OwnableManager.addUnowned(p);
  }

  void removeRailroad(Railroad r) {
    railroads.remove(r);
    OwnableManager.addUnowned(r);
  }

  void removeUtility(Utility u) {
    utilities.remove(u);
    OwnableManager.addUnowned(u);
  }


  void removeOwnable(Ownable ownable) {
    String type = ownable.getType();
    if (type.equals("railroad")) {
      removeRailroad(OwnableManager.getRailroad(ownable.getId()));
    } else if (type.equals("utility")) {
      removeUtility(OwnableManager.getUtility(ownable.getId()));
    } else if (type.equals("property")) {
      removeProperty(OwnableManager.getProperty(ownable.getId()));
    }
  }

  void addOwnable(Ownable ownable) {
    // properties in monopolies
    String type = ownable.getType();
    if (type.equals("railroad")) {
      addRailroad(OwnableManager.getRailroad(ownable.getId()));
    } else if (type.equals("utility")) {
      addUtility(OwnableManager.getUtility(ownable.getId()));
    } else if (type.equals("property")) {
      addProperty(OwnableManager.getProperty(ownable.getId()));
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
  public List<Ownable> getMortgagedNonMonopolies() {
    List<Ownable> mortgaged = getMortgagedHelper(railroads, true);
    mortgaged.addAll(getMortgagedHelper(properties, true));
    mortgaged.addAll(getMortgagedHelper(utilities, true));
    return mortgaged;
  }

  public List<Ownable> getMortgaged() {
    List<Ownable> toReturn = getMortgagedNonMonopolies();
    for(Monopoly monopoly : getMonopolies()) {
      toReturn.addAll(getMortgagedHelper(monopoly.getProperties(), true));
    }
    return toReturn;
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
      if (!p.isMortgaged()) {
        wealth += p.value();
      }
    }
    for (Monopoly m : monopolies) {
      for (Property p : m.getProperties()) {
        if (!p.isMortgaged()) {
          wealth += p.value();
        }
      }
    }
    for (Railroad r : railroads) {
      if (!r.isMortgaged()) {
        wealth += r.price() / 2;
      }
    }
    for (Utility u : utilities) {
      if (!u.isMortgaged()) {
        wealth += u.price() / 2;
      }
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

  public void clear() {
    for (int i = 0; i < properties.size(); i++) {
      Property p = properties.get(i);
      removeOwnable(p);
      p.demortgage();
    }
    for (int i = 0; i < monopolies.size(); i++) {
      List<Property> props = monopolies.get(i).getProperties();
      for (int j = 0; j < props.size(); j++) {
        Property p = props.get(j);
        removeOwnable(p);
        p.demortgage();
      }
    }
    for (int i = 0; i < railroads.size(); i++) {
      Railroad r = railroads.get(i);
      removeOwnable(r);
      r.demortgage();
    }
    for (int i = 0; i < utilities.size(); i++) {
      Utility u = utilities.get(i);
      removeOwnable(u);
      u.demortgage();
    }
  }
}
