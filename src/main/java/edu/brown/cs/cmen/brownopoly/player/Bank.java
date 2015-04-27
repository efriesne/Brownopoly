package edu.brown.cs.cmen.brownopoly.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
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

  public void addRailroad(Railroad r) {
    railroads.add(r);
    OwnableManager.addOwned(r);
  }

  public void addUtility(Utility u) {
    utilities.add(u);
    OwnableManager.addOwned(u);
  }

  public void removeProperty(Property p) {
    properties.remove(p);
    OwnableManager.addUnowned(p);
    // not handled for when p is in monopoly
  }

  public void removeRailroad(Railroad r) {
    railroads.remove(r);
    OwnableManager.addUnowned(r);
  }

  public void removeUtility(Utility u) {
    utilities.remove(u);
    OwnableManager.addUnowned(u);
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

  public List<Ownable> getMortgaged() {
    List<Ownable> mortgaged = getMortgagedHelper(railroads, true);
    mortgaged.addAll(getMortgagedHelper(properties, true));
    mortgaged.addAll(getMortgagedHelper(utilities, true));
    /*
     * for (Monopoly m : getMonopolies()) {
     * mortgaged.addAll(getMortgagedHelper(m.getProperties(), true)); }
     */
    return mortgaged;
  }

  public List<Ownable> getDemortgaged() {
    List<Ownable> demortgaged = getMortgagedHelper(railroads, false);
    demortgaged.addAll(getMortgagedHelper(properties, false));
    demortgaged.addAll(getMortgagedHelper(utilities, false));
    /*
     * for (Monopoly m : getMonopolies()) {
     * demortgaged.addAll(getMortgagedHelper(m.getProperties(), false)); }
     */
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

  public boolean checkMonopoly(Property p) {
    for (Property other : p.getPropertiesInMonopoly()) {
      if (!properties.contains(other)) {
        return false;
      }
    }
    return true;
  }
}
