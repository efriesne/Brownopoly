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
<<<<<<< HEAD
  public boolean makeBuyingDecision(Ownable p) {
=======
  public boolean makeBuyingDecision(Ownable ownable) {
    //prompt for user input
>>>>>>> 35d92d7f3545b6ddd2e935e463b0a9f51fa7840e
    return false;
  }

  @Override
  public void startTurn() {
    // doesn't do much for human
  }

}
