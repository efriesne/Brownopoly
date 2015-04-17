package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class Human extends Player {
  // TODO I changed the variable name, sorry -marley
  private StringBuilder builder_name;
  public Human(String name, List<Property> startingProperties) {
    super(name, startingProperties);
  }

  @Override
  public boolean makeBuyingDecision(Ownable ownable) {
    //prompt for user input
    return false;
  }

  @Override
  public void startTurn() {
    // doesn't do much for human
  }

  @Override
  public boolean isAI() {
    return false;
  }

}
