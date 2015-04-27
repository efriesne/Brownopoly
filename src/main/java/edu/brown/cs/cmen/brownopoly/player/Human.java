package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class Human extends Player {
  // TODO I changed the variable name, sorry -marley
  private StringBuilder builder_name;
  public Human(String name, List<Property> startingProperties, boolean isAI, String id) {
    super(name, startingProperties, isAI, id);
  }

  @Override
  public boolean makeBuyingDecision(Ownable ownable) {return false;}

  @Override
  public void startTurn() {
    // doesn't do much for human
  }

  @Override
  public boolean makeTradeDecision(String[][] initProps, int initMoney, String[][] recipProps, int recipMoney) {return true;}


}
