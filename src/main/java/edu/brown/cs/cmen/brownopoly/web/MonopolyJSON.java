package edu.brown.cs.cmen.brownopoly.web;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class MonopolyJSON {

  private PropertyJSON[] members;

  public MonopolyJSON(Monopoly monopoly) {
    List<Property> props = monopoly.getProperties();
    members = new PropertyJSON[props.size()];
    for (int i = 0; i < members.length; i++) {
      members[i] = new PropertyJSON(props.get(i));
    }
  }

}
