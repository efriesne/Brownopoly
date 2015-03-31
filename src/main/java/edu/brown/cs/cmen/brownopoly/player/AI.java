package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class AI extends Player {

  public AI(int numAI, List<Property> startingProperties) {
    super("AI " + numAI, startingProperties);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean makeBuyingDecision() {
    return true;
  }

}
