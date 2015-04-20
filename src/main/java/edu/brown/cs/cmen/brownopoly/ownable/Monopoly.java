package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author npucel
 *
 */
public class Monopoly {

  private Set<Property> members;

  public Monopoly(Property... properties) {
    for (Property p : properties) {
      members.add(p);
    }
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
