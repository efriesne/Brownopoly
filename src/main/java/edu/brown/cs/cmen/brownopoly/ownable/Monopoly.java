package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.*;

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
}
