package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class Human extends Player {
  private StringBuilder name;
  public Human(String name, List<Property> startingProperties) {
    super(name, startingProperties);
  }

  @Override
  public boolean makeBuyingDecision() {
    //prompt for user input
    return false;
  }

}
